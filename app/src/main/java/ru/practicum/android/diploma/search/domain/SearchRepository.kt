package ru.practicum.android.diploma.search.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.Resource

interface SearchRepository {
    suspend fun loadVacancyDetails(vacancyId: String): Resource<VacancyDetails>
    fun getSimilarVacanciesById(vacancyId: String): Flow<Resource<List<Vacancy>>>
    fun loadVacanciesQueryMap(options: HashMap<String, String>): Flow<Resource<List<Vacancy>>>

}
