package ru.practicum.android.diploma.favourite.presentation.viewvodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.db.data.converter.VacancyDbConverter
import ru.practicum.android.diploma.db.data.entity.VacancyEntity
import ru.practicum.android.diploma.db.domain.api.VacancyDbInteractor
import ru.practicum.android.diploma.favourite.presentation.models.FavoriteStateInterface
import ru.practicum.android.diploma.favourite.presentation.models.GetFavouriteVacancyInfoState
import ru.practicum.android.diploma.search.domain.models.Vacancy

class FavouriteViewModel(
    private val favouriteVacancyDbInteractor: VacancyDbInteractor,
    private val converter: VacancyDbConverter,
) : ViewModel() {

    private val stateLiveDataFavourite = MutableLiveData<FavoriteStateInterface?>()
    private val stateLiveDataGetVacancyInfo = MutableLiveData<GetFavouriteVacancyInfoState?>()

    fun observeStateFavourite(): LiveData<FavoriteStateInterface?> = stateLiveDataFavourite
    fun observeStateGetVacancyInfo(): MutableLiveData<GetFavouriteVacancyInfoState?> =
        stateLiveDataGetVacancyInfo

    private fun renderStateFavourite(state: FavoriteStateInterface) {
        stateLiveDataFavourite.postValue(state)
    }

    fun showFavouriteVacancies() {

        var favouriteVacancies: List<Vacancy>

        viewModelScope.launch {
            favouriteVacancyDbInteractor.getFavouriteVacancy().collect() { vacanciesEntity ->
                favouriteVacancies =
                    vacanciesEntity.map { vacancyEntity -> converter.map(vacancyEntity) }

                if (favouriteVacancies.isEmpty())
                    renderStateFavourite(FavoriteStateInterface.FavoriteVacanciesIsEmpty)
                else renderStateFavourite(
                    FavoriteStateInterface.FavoriteVacancies(favouriteVacancies)
                )
            }
        }
    }

    fun getFavouriteVacancyInfo(vacancyId: String) {

        if (vacancyId.isNullOrEmpty()) return

        viewModelScope.launch {
            favouriteVacancyDbInteractor.getFavouriteVacancyById(vacancyId)
                .collect() { vacancyEntity ->
                    stateGetVacancyInfo(vacancyEntity)
                }
        }
    }

    private fun stateGetVacancyInfo(vacancyEntity: VacancyEntity?) {
        if (vacancyEntity == null) stateLiveDataGetVacancyInfo.postValue(
            GetFavouriteVacancyInfoState.FavoriteVacanciesInfoIsEmpty
        )
        else {
            stateLiveDataGetVacancyInfo.postValue(
                GetFavouriteVacancyInfoState.FavoriteVacanciesInfo(
                    vacancy = converter.map(vacancyEntity),
                    vacancyDetails = converter.mapDetail(vacancyEntity)
                )
            )
        }
    }

    fun pause(){
        stateLiveDataGetVacancyInfo.postValue(null)
        stateLiveDataFavourite.postValue(null)
    }

    fun deleteVacancy(vacancy: Vacancy) {
        viewModelScope.launch {
            favouriteVacancyDbInteractor.getFavouriteVacancyById(vacancy.id).collect() {
                deleteVacancyEntity(it)
            }
        }
    }

    private fun deleteVacancyEntity(vacancyEntity: VacancyEntity?) {
        if (vacancyEntity == null) return
        viewModelScope.launch {
            favouriteVacancyDbInteractor.deleteVacancy(vacancyEntity)
        }
    }
}