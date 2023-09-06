package ru.practicum.android.diploma.filters.domain
import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.domain.models.Industries

interface FiltersInteractor {
    suspend fun getAreas(): Flow<Pair<List<Areas>?, String?>>

    suspend fun getFilters():Flow<Filters>?

    suspend fun writeFilters(filters: Filters)
    suspend fun getIndustries():Flow<Pair<List<Industries>?, String?>>
}