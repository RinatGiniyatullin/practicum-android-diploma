package ru.practicum.android.diploma.search.ui

sealed class FilterIconState {
    object NoFilters : FilterIconState()
    object YesFilters : FilterIconState()
}
