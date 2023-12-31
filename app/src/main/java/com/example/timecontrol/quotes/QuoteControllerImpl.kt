package com.example.timecontrol.quotes

import com.example.timecontrol.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            val author = getAuthor()
            val quote = getContent()
            if ((author + quote).isNotEmpty())
                return Quote(
                    quote = quote,
                    author = author,
                    category = category
                )
        }
        val newQuote = fetchQuote()
        saveNewQuote(newQuote)
        return newQuote
    }

    private fun saveNewQuote(newQuote: Quote) {
        CoroutineScope(Dispatchers.IO).launch {
            sharedPreferences.saveString(author, newQuote.author)
            sharedPreferences.saveString(content, newQuote.quote)
            sharedPreferences.saveString(category, newQuote.category)
            sharedPreferences.saveInt(savedDate, getDayOfYear())
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
        if (response.isEmpty())
            return Quote()
        return response.first()
    }


}


