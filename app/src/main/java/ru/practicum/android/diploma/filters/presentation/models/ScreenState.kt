package ru.practicum.android.diploma.filters.presentation.models

import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Region

sealed interface ScreenState {

    data class showIndustriesScreen(val industriesList:List<Industries>):ScreenState
    data class showAreasScreen(val areasList:List<Region>):ScreenState
    data class showCountriesScreen(
        val countriesList:List<Areas>
    ):ScreenState
}