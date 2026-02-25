package com.dereva.smart.data.remote

import com.dereva.smart.domain.model.Language
import com.dereva.smart.domain.model.TutorResponse
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import java.util.*

class GeminiService(private val apiKey: String) {
    
    private val model = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = apiKey
    )
    
    private val systemPromptEn = """
        You are an AI tutor for the NTSA (National Transport and Safety Authority) 
        driving theory test in Kenya. Your role is to:
        
        1. Answer questions about Kenyan traffic rules and road safety
        2. Explain road signs and their meanings
        3. Provide study recommendations based on weak areas
        4. Use simple, clear language
        5. Reference official NTSA guidelines
        
        Keep responses concise and educational.
    """.trimIndent()
    
    private val systemPromptSw = """
        Wewe ni mwalimu wa AI kwa mtihani wa nadharia ya udereva wa NTSA 
        (Mamlaka ya Usafiri na Usalama wa Taifa) nchini Kenya. Jukumu lako ni:
        
        1. Kujibu maswali kuhusu sheria za trafiki na usalama barabarani Kenya
        2. Kueleza alama za barabara na maana zake
        3. Kutoa mapendekezo ya masomo kulingana na maeneo dhaifu
        4. Tumia lugha rahisi na wazi
        5. Rejelea miongozo rasmi ya NTSA
        
        Weka majibu mafupi na ya kielimu.
    """.trimIndent()
    
    suspend fun askQuestion(
        question: String,
        language: Language,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): TutorResponse {
        val systemPrompt = when (language) {
            Language.ENGLISH -> systemPromptEn
            Language.SWAHILI -> systemPromptSw
        }
        
        val chat = model.startChat(
            history = conversationHistory.map { (userMsg, assistantMsg) ->
                listOf(
                    content("user") { text(userMsg) },
                    content("model") { text(assistantMsg) }
                )
            }.flatten()
        )
        
        val prompt = "$systemPrompt\n\nUser question: $question"
        val response = chat.sendMessage(prompt)
        val answer = response.text ?: "I couldn't generate a response."
        
        return TutorResponse(
            id = UUID.randomUUID().toString(),
            question = question,
            answer = answer,
            language = language,
            timestamp = Date(),
            recommendations = emptyList()
        )
    }
    
    fun detectLanguage(text: String): Language {
        val swahiliKeywords = listOf(
            "nini", "vipi", "namna", "jinsi", "sababu", "kwa", "na", "au",
            "barabara", "gari", "dereva", "alama", "sheria"
        )
        
        val lowerText = text.lowercase()
        val swahiliCount = swahiliKeywords.count { lowerText.contains(it) }
        
        return if (swahiliCount >= 2) Language.SWAHILI else Language.ENGLISH
    }
}
