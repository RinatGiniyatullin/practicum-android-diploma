package ru.practicum.android.diploma.db.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.db.data.converter.VacancyDbConverter
import ru.practicum.android.diploma.db.data.entity.VacancyEntity
import ru.practicum.android.diploma.db.domain.api.VacancyDbInteractor
import ru.practicum.android.diploma.db.domain.api.VacancyDbRepository
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.models.Vacancy

class VacancyDbInteractorImpl(
    private val vacancyDbRepository: VacancyDbRepository,
    private val vacancyDbConverter: VacancyDbConverter,
) :
    VacancyDbInteractor {
    override suspend fun insertVacancy(vacancy: Vacancy, vacancyDetails: VacancyDetails) {
        vacancyDbRepository.insertVacancy(vacancyDbConverter.map(vacancy, vacancyDetails))
    }

    override suspend fun deleteVacancy(vacancyEntity: VacancyEntity) {
        vacancyDbRepository.deleteVacancy(vacancyEntity)
    }

    override suspend fun getFavouriteVacancy(): Flow<List<VacancyEntity>> {
        return vacancyDbRepository.getFavouriteVacancy()
    }

    override suspend fun getFavouriteVacancyById(vacancyId: String): Flow<VacancyEntity> {
        return vacancyDbRepository.getFavouriteVacancyById(vacancyId)
    }

    override suspend fun deleteVacancyById(vacancyId: String){
        vacancyDbRepository.deleteVacancyById(vacancyId)
    }
}