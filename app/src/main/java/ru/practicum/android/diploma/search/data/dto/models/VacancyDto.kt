package ru.practicum.android.diploma.search.data.dto.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VacancyDto(
    @SerializedName("accept_incomplete_resumes")
    val acceptIncompleteResumes: Boolean,
    @SerializedName("alternate_url")
    val alternateUrl: String,
    @SerializedName("apply_alternate_url")
    val applyAlternateUrl: String,
    val area: Area,
    val department: Department?,
    val employer: Employer,
    @SerializedName("has_test")
    val hasTest: Boolean,
    val id: String,
    val name: String,
    @SerializedName("professional_roles")
    val professionalRoles: List<ProfessionalRoles>,
    @SerializedName("published_at")
    val publishedAt: String,
    val relations: Array<String>?,
    @SerializedName("response_letter_required")
    val responseLetterRequired: Boolean,
    val salary: Salary?,
    val type: Type,
    val url: String,
    val snippet: Snippet,
    val found: Int,
    val page: Int,
    val pages: Int,
    @SerializedName("per_page")
    val perPage: Int,
) : Parcelable

@Parcelize
data class Area(
    val id: String,
    val name: String,
    val url: String,
) : Parcelable

@Parcelize
data class Department(
    val id: String,
    val name: String,
) : Parcelable

@Parcelize
data class Employer(
    @SerializedName("accredited_it_employer")
    val accreditedItEmployer: Boolean,
    @SerializedName("alternate_url")
    val alternateUrl: String?,
    val id: String?,
    @SerializedName("logo_urls")
    val logoUrls: LogoUrls?,
    val name: String,
    val trusted: Boolean,
    val url: String?,
    @SerializedName("vacancies_url")
    val vacanciesUrl: String?,
) : Parcelable

@Parcelize
data class ProfessionalRoles(
    val id: String,
    val name: String,
) : Parcelable

@Parcelize
data class Salary(
    val currency: String?,
    val from: Int?,
    val gross: Boolean?,
    val to: Int?,
) : Parcelable

@Parcelize
data class Type(
    val id: String,
    val name: String,
) : Parcelable

@Parcelize
data class Snippet(
    val requirement: String?,
    val responsibility: String?,
) : Parcelable

@Parcelize
data class LogoUrls(
    val v90: String,
    val v240: String,
    val original: String,
) : Parcelable