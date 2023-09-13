package ru.practicum.android.diploma.details.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.details.domain.VacancyInteractor
import ru.practicum.android.diploma.search.data.ResourceProvider
import ru.practicum.android.diploma.search.domain.SearchState
import ru.practicum.android.diploma.search.domain.models.Vacancy

class SimilarVacancyViewModel(
    private val vacancyInteractor: VacancyInteractor,
    private val resourceProvider: ResourceProvider,
): ViewModel()  {

    private val vacanciesList = mutableListOf<Vacancy>()

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    fun getSimilarVacanciesById(vacancyId: String){
        viewModelScope.launch {
            vacancyInteractor.getSimilarVacanciesById(vacancyId)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }

    private fun processResult(foundVacancies: List<Vacancy>?, errorMessage: String?) {
        if (foundVacancies != null) {
            vacanciesList.addAll(foundVacancies)
        }
        when {
            errorMessage != null ->
                _state.postValue(SearchState.Error(resourceProvider.getString(R.string.no_connection)))

            vacanciesList.isEmpty() ->
                _state.postValue(SearchState.Empty(resourceProvider.getString(R.string.no_vacancies)))

            else ->
                _state.postValue(SearchState.VacancyContent(
                    vacancies = vacanciesList,
                    foundValue = vacanciesList[0].found
                ))
        }
    }
}