package ru.practicum.android.diploma.details.domain.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VacancyDetails(
    val contacts: Contacts?,
    val description: String,
    val alternateUrl: String,
    val area: Area,
    val employer: Employer?,
    val experience: Experience?,
    val keySkills: Array<KeySkill>,
    val schedule: Schedule?,
): Parcelable

@Parcelize
data class Contacts(
    val email: String?,
    val name: String?,
    val phones: Array<Phone>?,
) : Parcelable

@Parcelize
data class Phone(
    val city: String,
    val country: String,
    val number: String,
    val comment: String?
) : Parcelable

@Parcelize
data class Area(
    val name: String
) : Parcelable

@Parcelize
data class Employer(
    @SerializedName("logo_urls")
    val logoUrls: LogoUrls?,
) : Parcelable

@Parcelize
data class LogoUrls(
    val v90: String,
    val v240: String,
    val original: String,
) : Parcelable

@Parcelize
data class Experience(
    val name: String,
) : Parcelable

@Parcelize
data class KeySkill(
    val name: String,
) : Parcelable

@Parcelize
data class Schedule(
    val name: String,
) : Parcelable