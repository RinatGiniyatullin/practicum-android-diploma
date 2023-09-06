package ru.practicum.android.diploma.search.data.dto

import ru.practicum.android.diploma.filters.data.dto.models.AreasDto
import ru.practicum.android.diploma.filters.data.dto.models.CountryDto

open class Response {
    var resultCode = 0
    var resultAreas = listOf<AreasDto>()
    var resultCountries = listOf<CountryDto>()

}