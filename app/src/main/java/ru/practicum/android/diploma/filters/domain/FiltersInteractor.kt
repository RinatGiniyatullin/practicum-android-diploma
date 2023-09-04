package ru.practicum.android.diploma.filters.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas


interface FiltersInteractor {
    suspend fun getCountries(): Flow<Pair<List<Country>?, String?>>
    suspend fun getAreas(): Flow<Pair<List<Areas>?, String?>>
}