package ru.practicum.android.diploma.db.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.db.data.entity.VacancyEntity
import ru.practicum.android.diploma.search.domain.models.Vacancy


interface VacancyDbInteractor {
    suspend fun insertVacancy(vacancy: Vacancy)

    suspend fun deleteVacancy(vacancy: Vacancy)

    suspend fun getFavouriteVacancy(): Flow<List<VacancyEntity>>
}