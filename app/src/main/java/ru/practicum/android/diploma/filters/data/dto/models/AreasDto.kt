package ru.practicum.android.diploma.filters.data.dto.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class AreasDto(
    val id:String,
    val name:String,
    val areas:List<RegionDto>
)

data class RegionDto(
    val id:String,
    val name:String,
    @SerializedName("parent_id")
    val parentId:String
)
