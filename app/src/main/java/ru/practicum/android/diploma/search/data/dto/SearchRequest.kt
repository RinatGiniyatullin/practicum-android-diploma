package ru.practicum.android.diploma.search.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchRequest(val expression: String) : Parcelable


data class SearchRequestOptions(val options: HashMap<String, Any>)

@Parcelize
data class SearchRequestBig(val searchRequest: String, val page: Int, val per_page: Int) :
    Parcelable


