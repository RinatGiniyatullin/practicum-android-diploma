package ru.practicum.android.diploma.filters.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.domain.models.Industry
import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.util.Results

class FiltersInteractorImpl(val filtersRepository: FiltersRepository) : FiltersInteractor {
    override suspend fun getAreas(): Flow<Results<List<Areas>?, String?>> {
        return filtersRepository.getAreas().map { result ->
            when (result) {
                is Resource.Success -> {
                    Results(result.data, null)
                }

                is Resource.Error -> {
                    Results(null, result.message)
                }
            }
        }
    }

    override suspend fun getFilters(): Flow<Filters> {
        return filtersRepository.getFilters()
    }

    override suspend fun writeFilters(filters: Filters) {
        filtersRepository.writeFilters(filters)
    }

    override suspend fun getIndustries(): Flow<Results<List<Industry>?, String?>> {
        return filtersRepository.getIndustries().map { result ->
            when (result) {
                is Resource.Success -> {
                    Results(result.data, null)
                }

                is Resource.Error -> {
                    Results(null, result.message)
                }
            }
        }
    }


}