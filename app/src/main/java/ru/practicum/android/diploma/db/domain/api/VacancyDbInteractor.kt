package ru.practicum.android.diploma.db.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.db.data.entity.VacancyEntity
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.models.Vacancy


interface VacancyDbInteractor {
    suspend fun insertVacancy(vacancy: Vacancy, vacancyDetails: VacancyDetails)

    suspend fun deleteVacancy(vacancyEntity: VacancyEntity)

    suspend fun getFavouriteVacancy(): Flow<List<VacancyEntity>>

    suspend fun getFavouriteVacancyById(vacancyId: String): Flow<VacancyEntity>

    suspend fun deleteVacancyById(vacancyId: String)
}