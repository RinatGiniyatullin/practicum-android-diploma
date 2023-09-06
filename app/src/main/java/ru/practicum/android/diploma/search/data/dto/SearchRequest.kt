package ru.practicum.android.diploma.search.data.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchRequest(val expression: String) : Parcelable

@Parcelize
data class SearchRequestOptions(val options: HashMap<String, String>) : Parcelable

@Parcelize
object AreaSearchRequest:Parcelable

@Parcelize
data class SearchRequestDetails(val vacancyId: String) : Parcelable

@Parcelize
object IndustriesSearchRequest:Parcelable

@Parcelize
data class SearchRequestSimilarVacancies(val vacancyId: String) : Parcelable



