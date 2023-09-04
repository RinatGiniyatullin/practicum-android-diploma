package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.practicum.android.diploma.search.domain.SearchRepository
import ru.practicum.android.diploma.search.data.SearchRepositoryImpl
import ru.practicum.android.diploma.db.data.converter.VacancyDbConverter
import ru.practicum.android.diploma.db.data.impl.VacancyDbRepositoryImpl
import ru.practicum.android.diploma.db.domain.api.VacancyDbRepository
import ru.practicum.android.diploma.filters.data.FiltersRepositoryImpl
import ru.practicum.android.diploma.filters.domain.FiltersInteractor
import ru.practicum.android.diploma.filters.domain.FiltersInteractorImpl
import ru.practicum.android.diploma.filters.domain.FiltersRepository

val repositoryModule = module {
    singleOf(::SearchRepositoryImpl).bind<SearchRepository>()
    factory { VacancyDbConverter() }

    single<VacancyDbRepository> {
        VacancyDbRepositoryImpl(appDataBase = get())
    }
    single<FiltersRepository> { FiltersRepositoryImpl(get(), get())  }
}