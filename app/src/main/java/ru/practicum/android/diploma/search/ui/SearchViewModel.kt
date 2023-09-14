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
    private val lastOptions = hashMapOf<String, String>()

    private var _viewStateLiveData = MutableLiveData<SearchState>()
    val viewStateLiveData: LiveData<SearchState> = _viewStateLiveData

    private var _searchIconStateLiveData = MutableLiveData<SearchIconState>()
    val searchIconStateLiveData: LiveData<SearchIconState> = _searchIconStateLiveData

    private var _filterIconStateLiveData = MutableLiveData<FilterIconState>()
    val filterIconStateLiveData: LiveData<FilterIconState> = _filterIconStateLiveData

    fun clearInputEditText() {
        lastSearchText = null
        vacanciesList.clear()
        options.clear()
        lastOptions.clear()
        currentPage = 0
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

    private fun showLoading(searchText: String) {
        if (searchText.isNotEmpty() && vacanciesList.isEmpty()) {
            _viewStateLiveData.postValue(SearchState.FirstLoading)
        }
        if (searchText.isNotEmpty() && vacanciesList.isNotEmpty()) {
            _viewStateLiveData.postValue(SearchState.AddLoading)
        }
    }

    fun search(searchText: String) {
        if (searchText.isEmpty()) return
        fillOptionsWithFilters(searchText)
        if (options == lastOptions) return
        else {
            vacanciesList.clear()
            showLoading(searchText)
            currentPage = 0
            getVacancies(options)
            lastSearchText = searchText
            lastOptions.clear()
            lastOptions.putAll(options)
        }
    }

    private fun fillOptionsWithFilters(text: String) {
        options["text"] = text
        options["page"] = currentPage.toString()
        options["per_page"] = NUMBER_LOAD_VACANCIES.toString()
        if (areasId != null) options["area"] = areasId!!
        if (industriesId != null) options["industry"] = industriesId!!
        if (salary != 0) options["salary"] = salary.toString()
        options["only_with_salary"] = onlyWithSalary.toString()
    }

    private fun getVacancies(options: HashMap<String, String>) {

        viewModelScope.launch {
            interactor.loadVacanciesQueryMap(options)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
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

                    if (areasId != null || industriesId != null || salary != 0 || onlyWithSalary) {
                        _filterIconStateLiveData.postValue(FilterIconState.YesFilters)
                    } else {
                        _filterIconStateLiveData.postValue(FilterIconState.NoFilters)
                    }
                }
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
                isNextPageLoading = false
            }
        }
    }

    fun onLastItemReached() {

        if (currentPage == maxPages) {
            _viewStateLiveData.postValue(SearchState.StopLoad)
        }
        if (currentPage < maxPages && isNextPageLoading == false) {
            if (!lastSearchText.isNullOrEmpty()) {
                showLoading(lastSearchText!!)
                currentPage += 1
                options["page"] = currentPage.toString()
                getVacancies(options)
                lastOptions.clear()
                lastOptions.putAll(options)
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