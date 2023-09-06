package ru.practicum.android.diploma.search.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.Resource

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override fun loadVacancies(query: String): Flow<Pair<List<Vacancy>?, String?>> {
        return repository.searchVacancies(query).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun loadVacanciesQueryMap(options: HashMap<String, Any>): Flow<Pair<List<Vacancy>?, String?>> {
        return repository.loadVacanciesQueryMap(options).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun loadVacanciesBig(
        searchText: String,
        currentPage: Int,
        per_page: Int,
    ): Flow<Pair<List<Vacancy>?, String?>> {
        return repository.loadVacanciesBig(searchText, currentPage, per_page).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}