package com.example.timecontrol.quotes

import kotlinx.serialization.Serializable

@Serializable
data class Quote(val quote: String = "", val author: String = "", val category: String = "")
