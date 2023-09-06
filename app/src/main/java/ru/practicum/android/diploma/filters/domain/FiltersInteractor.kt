package ru.practicum.android.diploma.filters.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Filters


interface FiltersInteractor {
    suspend fun getCountries(): Flow<Pair<List<Country>?, String?>>
    suspend fun getAreas(): Flow<Pair<List<Areas>?, String?>>

    suspend fun getFilters():Flow<Filters>?

    suspend fun writeFilters(filters: Filters?)
}