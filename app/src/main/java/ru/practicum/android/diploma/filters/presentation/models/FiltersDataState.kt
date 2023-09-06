package ru.practicum.android.diploma.filters.presentation.models

import ru.practicum.android.diploma.filters.domain.models.Filters

sealed interface FiltersDataState{

    data class filtersData(val filters:Filters):FiltersDataState
}