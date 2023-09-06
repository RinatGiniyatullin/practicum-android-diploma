package ru.practicum.android.diploma.filters.data.dto.models

data class FiltersDto(
    var countryName:String?,
    var countryId:String?,
    var areasNames:String?,
    var areasId:String?,
    var industriesName:String?,
    var industriesId:String?,
    var salary:Int,
    var onlyWithSalary: Boolean
)

