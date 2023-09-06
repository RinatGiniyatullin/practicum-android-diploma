package ru.practicum.android.diploma.filters.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filters.domain.FiltersInteractor
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Region
import ru.practicum.android.diploma.filters.presentation.models.FiltersDataState
import ru.practicum.android.diploma.filters.presentation.models.ScreenState
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.COUNTRIES
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.INDUSTRIES
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.REGION

class FiltersViewModel(val filtersInteractor: FiltersInteractor) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<ScreenState>()
    private val filtersDataStateLiveData = MutableLiveData<FiltersDataState>()
    private var getAreasJob: Job? = null
    private var getFiltersJob: Job? = null
    private var getIndustriesJob:Job? = null
    private var showFiltersData:Job? = null
    private var writeFiltersJob:Job? = null
    private var countries = mutableListOf<Areas>()
    private var newIndustriesList = mutableListOf<Industries>()
    private var region = mutableListOf<Region>()
    private var parentId: String? = null
    private var filtersNew: Filters =
        Filters(countryName = null, countryId = null, areasNames = null, areasId = null, industriesName = null, industriesId = null, salary = 0, onlyWithSalary = false)

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData
    fun getFiltersStateLiveData():LiveData<FiltersDataState> = filtersDataStateLiveData

    init {
        getFilters()
        Log.d("Filters","$filtersNew" )
    }

    fun setScreen(nameOfScreen: String) {
        when (nameOfScreen) {
            COUNTRIES -> setScreenCountries()
            REGION -> setScreenRegion()
            INDUSTRIES -> setScreenIndustries()
        }
    }

    private fun setScreenCountries() {
        getAreasJob = viewModelScope.launch {
            getAreas()
            screenStateLiveData.postValue(ScreenState.showCountriesScreen(countries))
        }
    }
    private fun setScreenIndustries(){
        getIndustriesJob = viewModelScope.launch {
            getIndustries()
            screenStateLiveData.postValue(ScreenState.showIndustriesScreen(newIndustriesList))
        }
    }

    private fun setScreenRegion() {
        if (filtersNew.countryId.isNullOrEmpty()) {
            getAreasJob = viewModelScope.launch {
                getAreas()
                screenStateLiveData.postValue(ScreenState.showAreasScreen(region))
            }
        } else {
            getAreasJob = viewModelScope.launch {
                getAreas()
                region.clear()
                countries.map { if (it.id.equals(filtersNew.countryId)) region.addAll(it.areas) }
                screenStateLiveData.postValue(ScreenState.showAreasScreen(region))
            }
        }
    }
    suspend fun getAreas() {
        filtersInteractor.getAreas()
            .collect { pair ->
                val areasList = mutableListOf<Areas>()
                val regionList = mutableListOf<Region>()
                if (pair.first != null) {
                    areasList.addAll(pair.first!!)
                    areasList.map { it.areas.map { regionList.add(it) } }
                }
                when {
                    pair.second != null -> {
                        Log.d("myLog", pair.second.toString())
                    }

                    areasList.isEmpty() -> {
                        Log.d("myLog", "Пустой список")
                    }

                    else -> {
                        countries.addAll(areasList)
                        region.addAll(regionList)
                    }
                }
            }
    }
    suspend fun getIndustries(){
        filtersInteractor.getIndustries()
            .collect{pair ->
                val industriesList = mutableListOf<Industries>()
                if(pair.first!=null){
                    industriesList.addAll(pair.first!!)
                }
                when{
                    pair.second !=null ->{

                    }
                    industriesList.isEmpty() -> {

                    }
                    else ->{
                        newIndustriesList.addAll(industriesList)
                    }
                }
            }
    }

    fun addCountry(country: Areas) {
        filtersNew.countryName = country.name
        parentId = country.id
        filtersNew.countryId = parentId
        writeFilters()
        Log.d("filters", "$region")
    }

    fun addArea(RegionList: List<Region>) {
        filtersNew.areasNames = ""
        filtersNew.areasId = ""
        RegionList.map {
            filtersNew.areasId += "${it.id} "
            filtersNew.areasNames += "${it.name} "
            filtersNew.countryId = it.parent_id
        }
        countries.map { if(it.id.equals(filtersNew.countryId)) addCountry(it)  }
        writeFilters()
        Log.d("Region", "${filtersNew.areasId}")

    }
    fun addIndustries(industries: List<Industries>){
        filtersNew.industriesName  = ""
        filtersNew.industriesId = ""
        industries.map {
            filtersNew.industriesId+= "${it.id} "
            filtersNew.industriesName+="${it.name} "
        }
        writeFilters()
    }

    private fun getFilters() {
        getFiltersJob = viewModelScope.launch {
            filtersInteractor.getFilters()
                ?.collect { filters ->
                    filtersNew.countryName = filters.countryName
                    filtersNew.areasId = filters.areasId
                    filtersNew.areasNames = filters.areasNames
                    filtersNew.countryId = filters.countryId
                    filtersNew.industriesName = filters.industriesName
                    filtersNew.salary = filters.salary
                    filtersNew.onlyWithSalary = filters.onlyWithSalary
                }
        }
    }
    fun writeFilters() {
        writeFiltersJob = viewModelScope.launch {
            filtersInteractor.writeFilters(filtersNew)
        }
    }
    fun showFiltersData(){
        showFiltersData = viewModelScope.launch {
            getFilters()
            filtersDataStateLiveData.postValue(FiltersDataState.filtersData(filtersNew))
        }
    }
    fun clearCountry(){
        filtersNew.countryName = null
        filtersNew.countryId = null
        writeFilters()
    }
    fun clearRegion(){
        filtersNew.areasNames = null
        filtersNew.areasId = null
        writeFilters()
    }
    fun clearIndustries(){
        filtersNew.industriesName = null
        filtersNew.industriesId = null
        writeFilters()
    }

}