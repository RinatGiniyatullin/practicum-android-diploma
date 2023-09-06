package ru.practicum.android.diploma.filters.domain


import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.util.Resource

interface FiltersRepository {


    suspend fun getAres(): Flow<Resource<List<Areas>>>

    suspend fun getFilters():Flow<Filters>
    suspend fun writeFilters(filters: Filters)
}