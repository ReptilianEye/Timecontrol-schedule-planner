package com.example.timecontrol.preferences

import com.example.timecontrol.BuildConfig
import com.example.timecontrol.preferences.dto.Quote
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate


class QuoteControllerImpl(val sharedPreferences: MyPreferences, val client: HttpClient) :
    QuoteController {
    private val savedDate: String = "savedDate"
    private val author: String = "author"
    private val content: String = "content"
    private val category: String = "happiness"
    private val apiKey = BuildConfig.QUOTE_API_KEY

    override suspend fun getTodayQuote(): Quote {
        if (getSavedDate() == getDayOfYear()) {
            return Quote(
                quote = getContent(),
                author = getAuthor(),
                category = category
            )
        }
        val newQuote: Quote = fetchQuote()
        saveNewQuote(newQuote)
        return newQuote
    }

    private fun saveNewQuote(newQuote: Quote) {
        CoroutineScope(Dispatchers.IO).launch {
            async { sharedPreferences.saveString(author, newQuote.author) }
            async { sharedPreferences.saveString(content, newQuote.quote) }
            async { sharedPreferences.saveString(category, newQuote.category) }
            async { sharedPreferences.saveInt(savedDate, getDayOfYear()) }
        }
    }

    private fun getDayOfYear(): Int {
        return LocalDate.now().dayOfYear
    }

    private fun getSavedDate(): Int {
        return sharedPreferences.getInt(savedDate, -1)
    }

    private fun getAuthor(): String {
        return sharedPreferences.getString(author, "")
    }

    private fun getContent(): String {
        return sharedPreferences.getString(content, "")
    }

    private suspend fun fetchQuote(): Quote {
        val apiUrl = "https://api.api-ninjas.com/v1/quotes?category=$category"
        val response: List<Quote> = try {
            client.get(apiUrl) {
                headers { append("X-Api-Key", apiKey) }
            }
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            emptyList()
        } catch (e: Exception) {
            println(e.message)
            emptyList()
        }
        return response.first()
    }


}


