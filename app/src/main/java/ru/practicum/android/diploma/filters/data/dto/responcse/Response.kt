package ru.practicum.android.diploma.filters.data.dto.responcse

import ru.practicum.android.diploma.filters.data.dto.models.CountryDto
import ru.practicum.android.diploma.filters.data.dto.models.AreasDto
import ru.practicum.android.diploma.search.data.dto.Response

data class CountriesResponse(
    val items:List<CountryDto>
):Response()

data class AreasResponse(
    val items:List<AreasDto>
):Response()

