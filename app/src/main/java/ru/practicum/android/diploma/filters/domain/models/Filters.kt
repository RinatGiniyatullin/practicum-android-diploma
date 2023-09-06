package ru.practicum.android.diploma.filters.domain.models

data class Filters(
    var countryName:String?,
    var countryId:String?,
    var areasNames:String?,
    var areasId:String?,
    var industry:String?,
    var salary:Int,
    var onlyWithSalary: Boolean
)
