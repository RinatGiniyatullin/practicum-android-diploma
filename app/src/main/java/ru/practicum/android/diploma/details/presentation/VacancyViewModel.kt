package ru.practicum.android.diploma.details.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.db.data.converter.VacancyDbConverter
import ru.practicum.android.diploma.db.data.entity.VacancyEntity
import ru.practicum.android.diploma.db.domain.api.VacancyDbInteractor
import ru.practicum.android.diploma.details.domain.VacancyInteractor
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.data.ResourceProvider
import ru.practicum.android.diploma.search.domain.models.Vacancy

class VacancyViewModel(
    private val vacancyInteractor: VacancyInteractor,
    private val resourceProvider: ResourceProvider,
    private val vacancyDbInteractor: VacancyDbInteractor,
    private val converter: VacancyDbConverter
): ViewModel() {

    private val _state = MutableLiveData<VacancyState>()
    val state: LiveData<VacancyState> = _state

    private val stateFavouriteIconLiveData = MutableLiveData<Boolean>()
    fun observeStateFavouriteIcon(): LiveData<Boolean> = stateFavouriteIconLiveData

    private val _stateVacancyInfoDb = MutableLiveData<VacancyDetails?>()
    val stateVacancyInfoDb = _stateVacancyInfoDb

    fun loadVacancyDetails(vacancyId: String) {
        _state.postValue(VacancyState.Loading)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = vacancyInteractor.loadVacancyDetails(vacancyId)
                processResult(result.first, result.second)
            }
        }
    }

    private fun processResult(vacancyDetails: VacancyDetails?, errorMessage: String?) {
        when {
            errorMessage != null -> {
                _state.postValue(VacancyState.Error(resourceProvider.getString(R.string.no_connection)))
            }

            else -> {
                _state.postValue(VacancyState.Content(vacancyDetails!!))
            }
        }
    }

    fun clickOnFavoriteIcon(vacancy: Vacancy, vacancyDetails: VacancyDetails) {
        if (stateFavouriteIconLiveData.value == true) {

            stateFavouriteIconLiveData.postValue(false)

            deleteVacancy(converter.map(vacancy, vacancyDetails))

        } else {
            stateFavouriteIconLiveData.postValue(true)
            insertVacancy(vacancy, vacancyDetails)
        }
    }

    private fun insertVacancy(vacancy: Vacancy, vacancyDetails: VacancyDetails) {
        viewModelScope.launch {
            vacancyDbInteractor.insertVacancy(vacancy, vacancyDetails)
        }
    }

    fun checkFavourite(vacancy: Vacancy) {
        viewModelScope.launch {vacancyDbInteractor
            var favouriteVacancies: List<Vacancy>
            vacancyDbInteractor.getFavouriteVacancy().collect(){
                    vacanciesEntity -> favouriteVacancies =
                vacanciesEntity.map { vacancyEntity -> converter.map(vacancyEntity) }
                var isFavourite = false

                favouriteVacancies.forEach{
                    favouriteVacancy ->  if (vacancy.id == favouriteVacancy.id) isFavourite = true
                }

                stateFavouriteIconLiveData.postValue(isFavourite)
            }
        }
    }

    fun shareVacancyUrl(vacancyUrl: String){
        vacancyInteractor.shareVacancyUrl(vacancyUrl)
    }

    fun sharePhone(phone: String) {
        vacancyInteractor.sharePhone(phone)
    }

    fun shareEmail(email: String) {
        vacancyInteractor.shareEmail(email)
    }

    fun initVacancyDetailsInDb(vacancy: Vacancy) {
        viewModelScope.launch {
            vacancyDbInteractor.getFavouriteVacancyById(vacancy.id)
                .collect() { vacancyEntity ->
                    renderStateVacancyInfoDb(vacancyEntity)
                }
        }
    }

    private fun renderStateVacancyInfoDb(vacancyEntity: VacancyEntity?) {
        if (vacancyEntity == null) return _stateVacancyInfoDb.postValue(null)
        _stateVacancyInfoDb.postValue(
            converter.mapDetail(vacancyEntity)
        )
    }

    private fun deleteVacancy(vacancyEntity: VacancyEntity){
        viewModelScope.launch {
            vacancyDbInteractor.deleteVacancy(vacancyEntity)
        }
    }
}