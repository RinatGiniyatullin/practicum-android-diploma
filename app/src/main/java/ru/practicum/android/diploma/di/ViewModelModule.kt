package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import ru.practicum.android.diploma.details.presentation.SimilarVacancyViewModel
import ru.practicum.android.diploma.details.presentation.VacancyViewModel
import ru.practicum.android.diploma.favourite.presentation.viewvodel.FavouriteViewModel
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.search.ui.SearchViewModel

val viewModelModule = module {

    viewModelOf(::SearchViewModel)
    viewModel {
        FiltersViewModel(get())
    }

    viewModelOf(::FavouriteViewModel)

    viewModelOf(::VacancyViewModel)

    viewModelOf(::SimilarVacancyViewModel)
}