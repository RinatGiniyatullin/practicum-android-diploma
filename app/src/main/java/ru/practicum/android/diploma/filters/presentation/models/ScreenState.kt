package ru.practicum.android.diploma.filters.presentation.models

import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas

sealed interface ScreenState {

    object showIndustriesScreen:ScreenState
    data class showAreasScreen(val areasList:List<Areas>):ScreenState
    data class showCountriesScreen(
        val countriesList:List<Country>
    ):ScreenState
}