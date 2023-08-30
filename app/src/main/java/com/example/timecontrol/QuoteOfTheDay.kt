package com.example.timecontrol

import android.content.SharedPreferences
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import java.time.LocalDate

data class Quote(val author: String = "", val content: String = "")
class QuoteOfTheDay(val sharedPreferences: SharedPreferences) {
    val savedDate: String = "savedDate"
    val author: String = "author"
    val content: String = "content"
    val category: String = "happiness"
    private val httpClient by lazy { HttpClient(Android) }

    fun getSavedDate(): Int {
        return sharedPreferences.getInt(savedDate, -1)
    }

    fun getAuthor(): String {
        return sharedPreferences.getString(author, "") ?: ""
    }

    fun getContent(): String {
        return sharedPreferences.getString(content, "") ?: ""
    }

//    suspend fun fetchQuote() {
//        val apiKey = BuildConfig.QUOTE_API_KEY
//        val apiUrl = "https://api.api-ninjas.com/v1/quotes?category=$category"
//        httpClient.get(apiUrl){
//            headers{
//
//            }
//        }

//    }

    fun getTodayQuote(): Quote {
        val today = LocalDate.now().dayOfYear
        if (sharedPreferences.getInt(this.savedDate, 0) == today) {
            return Quote(getAuthor(), getContent())
        }
        return Quote()
    }
}


