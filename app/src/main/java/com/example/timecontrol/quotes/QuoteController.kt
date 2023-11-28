package com.example.timecontrol.quotes

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

interface QuoteController {
    suspend fun getTodayQuote(): Quote

    companion object {
        fun create(sharedPreferences: MyPreferences): QuoteController {
            return QuoteControllerImpl(
                sharedPreferences = sharedPreferences,
                client = HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                })
        }
    }
}


