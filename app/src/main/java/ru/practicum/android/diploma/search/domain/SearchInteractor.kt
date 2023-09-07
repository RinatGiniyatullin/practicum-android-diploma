package ru.practicum.android.diploma.search.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.search.domain.models.Vacancy

interface SearchInteractor {
    fun loadVacancies(query: String): Flow<Pair<List<Vacancy>?, String?>>
    fun loadVacanciesQueryMap(options: HashMap<String, Any>): Flow<Pair<List<Vacancy>?, String?>>
    fun loadVacanciesBig(
        searchText: String,
        currentPage: Int,
        per_page: Int,
    ): Flow<Pair<List<Vacancy>?, String?>>
}