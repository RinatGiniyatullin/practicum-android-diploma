package ru.practicum.android.diploma.filters.presentation.models

sealed interface ScreenState {
    object showPlaceOfWorkScreen:ScreenState
    object showIndustriesScreen:ScreenState
}