package ru.practicum.android.diploma.filters.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filters.domain.FiltersInteractor
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.presentation.models.ScreenState
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.COUNTRIES
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.INDUSTRIES
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.REGION

class FiltersViewModel(val filtersInteractor: FiltersInteractor):ViewModel() {


    private val screenStateLiveData = MutableLiveData<ScreenState>()
    private var getCountriesJob: Job? = null
    private var getAreasJob:Job? = null
    fun getScreenStateLiveData():LiveData<ScreenState> = screenStateLiveData


    fun setScreen(nameOfScreen:String){
        when (nameOfScreen){
            COUNTRIES -> setCountriesScreen()
            REGION -> setAreasScreen()
            INDUSTRIES -> screenStateLiveData.postValue(ScreenState.showIndustriesScreen)
        }
    }
    private fun setCountriesScreen(){
        getCountriesJob = viewModelScope.launch {
            filtersInteractor.getCountries()
                .collect{pair ->
                    val countriesList = mutableListOf<Country>()
                    if(pair.first!=null){
                        countriesList.addAll(pair.first!!)
                    }
                    when{
                        pair.second!=null ->{
                            Log.d("myLog", pair.second.toString())
                        }
                        countriesList.isEmpty() ->{
                            Log.d("myLog", "Пустой список")
                        }
                        else ->{
                            screenStateLiveData.postValue(ScreenState.showCountriesScreen(countriesList))
                        }
                    }
                }

        }
    }
    private fun setAreasScreen(){
        getAreasJob = viewModelScope.launch {
            filtersInteractor.getAreas()
                .collect{pair ->
                    val areasList = mutableListOf<Areas>()
                    if(pair.first!=null){
                        areasList.addAll(pair.first!!)
                    }
                    when{
                        pair.second!=null ->{
                            Log.d("myLog", pair.second.toString())
                        }
                        areasList.isEmpty() ->{
                            Log.d("myLog", "Пустой список")
                        }
                        else ->{
                            screenStateLiveData.postValue(ScreenState.showAreasScreen(areasList))
                        }
                    }

                }

        }
    }

}