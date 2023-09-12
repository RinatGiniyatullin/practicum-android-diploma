package ru.practicum.android.diploma.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.filters.domain.FiltersInteractor
import ru.practicum.android.diploma.search.data.ResourceProvider
import ru.practicum.android.diploma.search.domain.SearchInteractor
import ru.practicum.android.diploma.search.domain.SearchState
import ru.practicum.android.diploma.search.domain.models.Vacancy

class SearchViewModel(
    private val interactor: SearchInteractor,
    private val resourceProvider: ResourceProvider,
    private val filtersInteractor: FiltersInteractor,
) : ViewModel() {

    private var lastSearchText: String? = null
    private var currentPage: Int = 0
    private var maxPages: Int = 1
    private var isNextPageLoading = true
    private val vacanciesList = mutableListOf<Vacancy>()
    private var areasId: String? = null
    private var industriesId: String? = null
    private var salary: Int = 0
    private var onlyWithSalary: Boolean = false
    private val options = hashMapOf<String, String>()

    private var _viewStateLiveData = MutableLiveData<SearchState>()
    val viewStateLiveData: LiveData<SearchState> = _viewStateLiveData

    private var _searchIconStateLiveData = MutableLiveData<SearchIconState>()
    val searchIconStateLiveData: LiveData<SearchIconState> = _searchIconStateLiveData

    private var _filterIconStateLiveData = MutableLiveData<FilterIconState>()
    val filterIconStateLiveData: LiveData<FilterIconState> = _filterIconStateLiveData

    fun clearInputEditText() {
        lastSearchText = null
        vacanciesList.clear()
    }

    fun setOnFocus(editText: String?, hasFocus: Boolean) {
        if (hasFocus && editText.isNullOrEmpty()) _searchIconStateLiveData.postValue(
            SearchIconState.SearchSearchIcon
        )
        if (hasFocus && editText!!.isNotEmpty()) _searchIconStateLiveData.postValue(
            SearchIconState.CloseSearchIcon
        )
        if (!hasFocus && editText!!.isNotEmpty()) _searchIconStateLiveData.postValue(
            SearchIconState.SearchSearchIcon
        )
        if (!hasFocus && editText.isNullOrEmpty()) _searchIconStateLiveData.postValue(
            SearchIconState.SearchSearchIcon
        )
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

        options["text"] = searchText
        options["page"] = currentPage.toString()
        options["per_page"] = NUMBER_LOAD_VACANCIES.toString()

        viewModelScope.launch {
            /* interactor.loadVacanciesBig(
                 searchText,
                 currentPage,
                 NUMBER_LOAD_VACANCIES,
                 areasId,
                 industriesId,
                 salary,
                 onlyWithSalary
             )*/
            interactor.loadVacanciesQueryMap(options)
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

        if (currentPage == maxPages) {
            _viewStateLiveData.postValue(SearchState.StopLoad)
        }
        if (currentPage < maxPages && isNextPageLoading == false) {
            getVacancies(lastSearchText ?: "")
        }
    }

    private fun getFilters() {
        viewModelScope.launch {
            filtersInteractor.getFilters()
                .collect { filters ->
                    areasId = filters.areasId
                    industriesId = filters.industriesId
                    salary = filters.salary
                    onlyWithSalary = filters.onlyWithSalary

                    options.clear()
                    if (areasId != null) options["area"] = areasId!!
                    if (industriesId != null) options["industry"] = industriesId!!
                    if (salary != 0) options["salary"] = salary.toString()
                    options["only_with_salary"] = onlyWithSalary.toString()

                    if (areasId != null || industriesId != null || salary != 0 || onlyWithSalary) {
                        _filterIconStateLiveData.postValue(FilterIconState.YesFilters)
                        if (!lastSearchText.isNullOrEmpty()) {
                            vacanciesList.clear()
                            getVacancies(lastSearchText!!)
                        }
                    } else {
                        _filterIconStateLiveData.postValue(FilterIconState.NoFilters)
                        if (!lastSearchText.isNullOrEmpty()) {
                            vacanciesList.clear()
                            getVacancies(lastSearchText!!)
                        }

                    }
                }
        }
    }

    fun showFilters() {
        getFilters()
    }

    companion object {
        private const val NUMBER_LOAD_VACANCIES = 20
    }
}