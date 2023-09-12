package ru.practicum.android.diploma.search.ui

sealed class SearchIconState {
    object SearchSearchIcon : SearchIconState()
    object CloseSearchIcon : SearchIconState()

}
