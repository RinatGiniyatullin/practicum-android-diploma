package ru.practicum.android.diploma.filters.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.util.Resource

class FiltersInteractorImpl(val filtersRepository: FiltersRepository):FiltersInteractor {
    override suspend fun getAreas(): Flow<Pair<List<Areas>?, String?>> {
        return filtersRepository.getAres().map{result ->
            when(result){
                is Resource.Success ->{
                    Pair(result.data, null)
                }
                is Resource.Error ->{
                    Pair(null, result.message)
                }
            }
        }
    }

    override suspend fun getFilters(): Flow<Filters>? {
        return filtersRepository.getFilters()
    }

    override suspend fun writeFilters(filters: Filters) {
        filtersRepository.writeFilters(filters)
    }
}