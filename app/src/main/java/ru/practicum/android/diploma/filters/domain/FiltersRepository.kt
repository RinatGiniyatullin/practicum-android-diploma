package ru.practicum.android.diploma.filters.domain


import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.util.Resource

interface FiltersRepository {

    suspend fun getCountries(): Flow<Resource<List<Country>>>
    suspend fun getAres(): Flow<Resource<List<Areas>>>
}