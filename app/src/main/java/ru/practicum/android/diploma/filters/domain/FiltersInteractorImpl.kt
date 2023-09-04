package ru.practicum.android.diploma.filters.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.util.Resource

class FiltersInteractorImpl(val filtersRepository: FiltersRepository):FiltersInteractor {
    override suspend fun getCountries(): Flow<Pair<List<Country>?, String?>> {
        return filtersRepository.getCountries().map { result->
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
}