package ru.practicum.android.diploma.details.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.SearchRepository
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.Resource

class VacancyInteractorImpl(
    private val repository: SearchRepository,
    private val navigator: ExternalNavigator
): VacancyInteractor {

    override suspend fun loadVacancyDetails(vacancyId: String): Pair<VacancyDetails?, String?> {
        return when(val result = repository.loadVacancyDetails(vacancyId)){
            is Resource.Success -> {
                Pair(result.data, null)
            }

            is Resource.Error -> {
                Pair(null, result.message)
            }
        }
    }

    override fun getSimilarVacanciesById(vacancyId: String): Flow<Pair<List<Vacancy>?, String?>> {
        return repository.getSimilarVacanciesById(vacancyId).map { result ->
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

    override fun shareVacancyUrl(vacancyUrl: String){
        navigator.shareVacancyUrl(vacancyUrl)
    }

    override fun sharePhone(phone: String){
        navigator.sharePhone(phone)
    }

    override fun shareEmail(email: String) {
        navigator.shareEmail(email)
    }
}