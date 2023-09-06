package ru.practicum.android.diploma.search.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.Resource

interface SearchRepository {
    fun searchVacancies(query: String): Flow<Resource<List<Vacancy>>>
    fun loadVacanciesQueryMap(options: HashMap<String, Any>): Flow<Resource<List<Vacancy>>>
    fun loadVacanciesBig(
        searchText: String,
        currentPage: Int,
        perPage: Int,
    ): Flow<Resource<List<Vacancy>>>
}
