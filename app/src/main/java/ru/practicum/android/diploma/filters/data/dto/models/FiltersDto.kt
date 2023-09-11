package ru.practicum.android.diploma.filters.data.dto.models

data class FiltersDto(
    val countryName:String?,
    val countryId:String?,
    val areasNames:String?,
    val areasId:String?,
    val industriesName:String?,
    val industriesId:String?,
    val salary:Int,
    val onlyWithSalary: Boolean
)

