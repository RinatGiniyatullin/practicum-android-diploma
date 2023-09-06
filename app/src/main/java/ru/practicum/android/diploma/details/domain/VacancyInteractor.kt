package ru.practicum.android.diploma.details.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.models.Vacancy

interface VacancyInteractor {
    suspend fun loadVacancyDetails(vacancyId: String): Pair<VacancyDetails?, String?>
    fun getSimilarVacanciesById (vacancyId: String): Flow<Pair<List<Vacancy>?, String?>>
}