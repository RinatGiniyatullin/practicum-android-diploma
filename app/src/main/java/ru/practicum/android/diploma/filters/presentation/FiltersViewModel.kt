package ru.practicum.android.diploma.filters.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filters.presentation.models.ScreenState

class FiltersViewModel():ViewModel() {


    private val screenStateLiveData = MutableLiveData<ScreenState>()
    fun getScreenStateLiveData():LiveData<ScreenState> = screenStateLiveData


    fun setScreen(numberOfScreen:Int){
        when (numberOfScreen){
            1 -> screenStateLiveData.postValue(ScreenState.showPlaceOfWorkScreen)
            2 -> screenStateLiveData.postValue(ScreenState.showIndustriesScreen)
            3 -> screenStateLiveData.postValue(ScreenState.showChooseCountryScreen)

        }
    }





}