package ru.practicum.android.diploma.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.search.data.ResourceProvider
import ru.practicum.android.diploma.search.domain.SearchInteractor
import ru.practicum.android.diploma.search.domain.SearchState
import ru.practicum.android.diploma.search.domain.models.Vacancy

class SearchViewModel(
    private val interactor: SearchInteractor,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {

    private var lastSearchText: String? = null
    private var currentPage: Int = 0
    private var maxPages: Int = 1
    private var isNextPageLoading = true
    private val vacanciesList = mutableListOf<Vacancy>()

    private var _viewStateLiveData = MutableLiveData<SearchState>()
    val viewStateLiveData: LiveData<SearchState> = _viewStateLiveData

    private var _iconStateLiveData = MutableLiveData<IconState>()
    val iconStateLiveData: LiveData<IconState> = _iconStateLiveData

    fun clearInputEditText() {
        lastSearchText = null
        vacanciesList.clear()
    }

    fun setOnFocus(editText: String?, hasFocus: Boolean) {
        if (hasFocus && editText.isNullOrEmpty()) _iconStateLiveData.postValue(IconState.SearchIcon)
        if (hasFocus && editText!!.isNotEmpty()) _iconStateLiveData.postValue(IconState.CloseIcon)
        if (!hasFocus && editText!!.isNotEmpty()) _iconStateLiveData.postValue(IconState.SearchIcon)
        if (!hasFocus && editText.isNullOrEmpty()) _iconStateLiveData.postValue(IconState.SearchIcon)
    }

    fun search(searchText: String) {
        if (lastSearchText == searchText) {
            return
        } else {
            vacanciesList.clear()
            getVacancies(searchText)
        }
    }

    fun getVacancies(searchText: String) {

        if (searchText.isEmpty()) return
        if (searchText.isNotEmpty() && vacanciesList.isEmpty()) {
            _viewStateLiveData.postValue(SearchState.FirstLoading)
        }
        if (searchText.isNotEmpty() && vacanciesList.isNotEmpty()) {
            _viewStateLiveData.postValue(SearchState.AddLoading)
        }

        viewModelScope.launch {
            interactor.loadVacanciesBig(searchText, currentPage, NUMBER_LOAD_VACANCIES)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
            lastSearchText = searchText
        }
    }

    private fun processResult(foundVacancies: List<Vacancy>?, errorMessage: String?) {
        if (foundVacancies != null) {
            vacanciesList.addAll(foundVacancies)
        }
        when {
            errorMessage != null -> {
                _viewStateLiveData.postValue(
                    SearchState.Error(
                        errorMessage = errorMessage
                    )
                )
            }

            vacanciesList.isEmpty() -> {
                _viewStateLiveData.postValue(
                    SearchState.Empty(
                        message = resourceProvider.getString(R.string.no_vacancies)
                    )
                )
            }

            else -> {
                _viewStateLiveData.postValue(
                    SearchState.VacancyContent(
                        vacancies = vacanciesList,
                        foundValue = vacanciesList[0].found
                    )
                )
                maxPages = vacanciesList[0].pages
                currentPage += 1
                isNextPageLoading = false
            }
        }
    }

    fun onLastItemReached() {
        if (currentPage < maxPages && isNextPageLoading == false) {
            getVacancies(lastSearchText ?: "")
        }
    }

    companion object {
        private const val NUMBER_LOAD_VACANCIES = 20
    }
}
/* Форма для фильтрации
        val options: HashMap<String, Any> = HashMap()

        options["text"] = searchText
        options["page"] = currentPage
        options["per_page"] = 20
        if (area.isNotEmpty()) options["area"] = area
        if (industry.isNotEmpty()) options["industry"] = industry
        if (salary.isNotEmpty()) options["salary"] = salary
        if (onlyWithSalary.isNotEmpty()) options["only_with_salary"] = onlyWithSalary*/