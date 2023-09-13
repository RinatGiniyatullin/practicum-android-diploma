package ru.practicum.android.diploma.filters.presentation.models

import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Region

sealed interface ScreenState {


    object ShowIndustriesScreen:ScreenState
    data class ShowIndustryList(val industryList:List<Industries>):ScreenState
    object ShowAreasScreen:ScreenState
    data class ShowAreasList(val areasList:List<Region>):ScreenState
    object ShowCountriesScreen:ScreenState
    data class ShowCountriesList(val countriesList:List<Areas>):ScreenState
}