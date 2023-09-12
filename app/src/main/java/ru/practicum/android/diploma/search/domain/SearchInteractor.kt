package ru.practicum.android.diploma.search.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.search.domain.models.Vacancy

interface SearchInteractor {
    fun loadVacanciesQueryMap(options: HashMap<String, String>): Flow<Pair<List<Vacancy>?, String?>>

}