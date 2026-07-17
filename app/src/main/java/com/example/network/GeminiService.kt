package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.data.BloodRequestEntity
import com.example.data.UserEntity
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini API Request Models (Annotated with Moshi) ---

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float = 0.2f,
    val responseMimeType: String = "application/json"
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

// --- Retrofit API Interface ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

// --- Gemini API Retrofit Client ---

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

// --- Gemini Response Data Class ---

@JsonClass(generateAdapter = true)
data class AiAnalysisResult(
    val priorityScore: Double, // 0.0 to 100.0
    val priorityReason: String,
    val isSpam: Boolean,
    val spamReason: String,
    val isDuplicate: Boolean,
    val duplicateReason: String,
    val recommendedDonorIds: List<String>,
    val rankingExplanation: String
)

object GeminiService {
    private const val TAG = "GeminiService"

    // Fallback heuristic scoring if API key is not present or network fails
    fun calculateLocalHeuristics(
        newRequest: BloodRequestEntity,
        existingRequests: List<BloodRequestEntity>,
        availableDonors: List<UserEntity>
    ): AiAnalysisResult {
        // Priority Heuristic
        var priority = 50.0
        val issueLower = newRequest.patientIssue.lowercase()
        if (newRequest.isEmergency) priority += 25.0
        if (issueLower.contains("accident") || issueLower.contains("operation") || issueLower.contains("surgery") || issueLower.contains("সিজার") || issueLower.contains("দুর্ঘটনা")) {
            priority += 15.0
        }
        val hb = newRequest.hemoglobin?.toDoubleOrNull()
        if (hb != null && hb < 8.0) {
            priority += 10.0
        }
        priority = priority.coerceAtMost(100.0)

        val priorityReason = if (priority > 85.0) {
            "Critical: Emergency flag requested, severe surgical or trauma case with low hemoglobin level."
        } else if (priority > 65.0) {
            "High Priority: Time-critical surgery or blood exchange is scheduled soon."
        } else {
            "Medium Priority: Regular planned transfusion or treatment."
        }

        // Spam detection Heuristic
        var isSpam = false
        var spamReason = "Clean request. Verified contact number formatting and appropriate hospital detail."
        if (newRequest.patientIssue.length < 5 || newRequest.hospitalName.length < 5) {
            isSpam = true
            spamReason = "Suspicious: Field entries are too short or contain nonsensical characters."
        } else if (newRequest.contactPhone.length < 11) {
            isSpam = true
            spamReason = "Suspicious: Invalid contact phone length for Bangladesh."
        }

        // Duplicate detection Heuristic
        var isDuplicate = false
        var duplicateReason = "No matching active requests found from this contact or location."
        for (req in existingRequests) {
            if (req.id != newRequest.id && req.status != "Fulfilled") {
                val matchesPhone = req.contactPhone == newRequest.contactPhone
                val matchesHospitalAndBg = req.hospitalName == newRequest.hospitalName && req.bloodGroup == newRequest.bloodGroup
                if (matchesPhone || matchesHospitalAndBg) {
                    isDuplicate = true
                    duplicateReason = "Duplicate Detected: An active request for ${newRequest.bloodGroup} at ${newRequest.hospitalName} is already pending."
                    break
                }
            }
        }

        // Ranking Match Heuristic
        val rankedDonors = availableDonors
            .filter { it.bloodGroup == newRequest.bloodGroup && it.verificationStatus == "Verified" }
            .sortedWith(compareByDescending<UserEntity> { it.isAvailable }
                .thenByDescending { it.badge == "Hero" || it.badge == "Top Donor" }
                .thenBy { it.lastDonationDate ?: "" }
            )
        val recommendedIds = rankedDonors.take(5).map { it.id.toString() }

        val explanation = "Matched ${rankedDonors.size} verified donors of group ${newRequest.bloodGroup} in Cumilla area. High priority given to active, highly badge-rated, and available donors."

        return AiAnalysisResult(
            priorityScore = priority,
            priorityReason = priorityReason,
            isSpam = isSpam,
            spamReason = spamReason,
            isDuplicate = isDuplicate,
            duplicateReason = duplicateReason,
            recommendedDonorIds = recommendedIds,
            rankingExplanation = explanation
        )
    }

    suspend fun analyzeBloodRequest(
        newRequest: BloodRequestEntity,
        existingRequests: List<BloodRequestEntity>,
        availableDonors: List<UserEntity>
    ): AiAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key found, executing local heuristic matching.")
            return@withContext calculateLocalHeuristics(newRequest, existingRequests, availableDonors)
        }

        val donorsJson = JSONArray()
        availableDonors.take(15).forEach { donor ->
            val obj = JSONObject()
            obj.put("id", donor.id)
            obj.put("name", donor.name)
            obj.put("bloodGroup", donor.bloodGroup)
            obj.put("area", donor.area)
            obj.put("isAvailable", donor.isAvailable)
            obj.put("totalDonations", donor.totalDonations)
            obj.put("badge", donor.badge)
            donorsJson.put(obj)
        }

        val existingJson = JSONArray()
        existingRequests.take(10).forEach { req ->
            val obj = JSONObject()
            obj.put("id", req.id)
            obj.put("bloodGroup", req.bloodGroup)
            obj.put("hospitalName", req.hospitalName)
            obj.put("contactPhone", req.contactPhone)
            obj.put("status", req.status)
            existingJson.put(obj)
        }

        val prompt = """
            Analyze this new blood donation request and rank matching donors.
            
            NEW REQUEST DETAILS:
            - Patient Issue: ${newRequest.patientIssue}
            - Blood Group Needed: ${newRequest.bloodGroup}
            - Quantity: ${newRequest.quantity}
            - Hemoglobin: ${newRequest.hemoglobin ?: "N/A"}
            - Hospital: ${newRequest.hospitalName}
            - Area: ${newRequest.area}
            - Contact Phone: ${newRequest.contactPhone}
            - Is Emergency: ${newRequest.isEmergency}
            
            EXISTING ACTIVE REQUESTS:
            $existingJson
            
            AVAILABLE REGISTERED DONORS:
            $donorsJson
            
            You must return a valid JSON object matching the schema below:
            {
              "priorityScore": 85.5, // Double from 0.0 to 100.0. Higher means critical trauma/surgery.
              "priorityReason": "string explanation",
              "isSpam": false, // true if request is clearly fake, contains random/junk texts, or illegal contact
              "spamReason": "string explanation",
              "isDuplicate": false, // true if another active request exists with matching phone or same group+hospital combo
              "duplicateReason": "string explanation",
              "recommendedDonorIds": ["id1", "id2"], // array of donor IDs from the input that best match the bloodGroup, are available, and nearby
              "rankingExplanation": "string explaining how the ranking was determined"
            }
            Do not include any markdown format or ```json wrapper. Return raw JSON.
        """.trimIndent()

        val systemInstructionText = "You are an expert AI triage and sorting bot for the blood donation platform 'রক্ত দিতে প্রস্তুত আমরা' in Cumilla, Bangladesh. You always return strictly valid raw JSON without any wrapping tags."

        val requestBody = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.1f,
                responseMimeType = "application/json"
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, requestBody)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                // Parse and return the JSON via Moshi
                val cleanJson = jsonText.trim()
                val adapter = RetrofitClient.moshi.adapter(AiAnalysisResult::class.java)
                val parsed = adapter.fromJson(cleanJson)
                parsed ?: calculateLocalHeuristics(newRequest, existingRequests, availableDonors)
            } else {
                Log.e(TAG, "Empty response from Gemini API, falling back to heuristics.")
                calculateLocalHeuristics(newRequest, existingRequests, availableDonors)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API: ${e.message}", e)
            calculateLocalHeuristics(newRequest, existingRequests, availableDonors)
        }
    }
}
