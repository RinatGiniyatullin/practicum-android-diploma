package ru.practicum.android.diploma.search.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.search.data.dto.SearchRequest
import ru.practicum.android.diploma.search.data.dto.SearchRequestBig
import ru.practicum.android.diploma.search.data.dto.SearchRequestOptions
import ru.practicum.android.diploma.search.data.dto.SearchResponse
import ru.practicum.android.diploma.search.data.dto.models.VacancyDto
import ru.practicum.android.diploma.search.domain.SearchRepository
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.util.createValue


class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val resourceProvider: ResourceProvider,
) : SearchRepository {
    override fun searchVacancies(query: String): Flow<Resource<List<Vacancy>>> = flow {
        val response = networkClient.doRequest(SearchRequest(query))
        when (response.resultCode) {
            ERROR -> {
                emit(Resource.Error(resourceProvider.getString(R.string.check_connection)))
            }

            SUCCESS -> {
                with(response as SearchResponse) {
                    val vacanciesList = items.map { mapVacancyFromDto(it, found, pages) }
                    emit(Resource.Success(vacanciesList))
                }

            }

            else -> {
                emit(Resource.Error(resourceProvider.getString(R.string.server_error)))
            }
        }

    }

    override fun loadVacanciesQueryMap(options: HashMap<String, Any>): Flow<Resource<List<Vacancy>>> =
        flow {
            val response = networkClient.doRequest(SearchRequestOptions(options))
            when (response.resultCode) {
                ERROR -> {
                    emit(Resource.Error(resourceProvider.getString(R.string.check_connection)))
                }

                SUCCESS -> {
                    with(response as SearchResponse) {
                        val vacanciesList = items.map { mapVacancyFromDto(it, found, pages) }
                        emit(Resource.Success(vacanciesList))
                    }

                }

                else -> {
                    emit(Resource.Error(resourceProvider.getString(R.string.server_error)))
                }
            }
        }

    override fun loadVacanciesBig(
        searchText: String,
        currentPage: Int,
        perPage: Int,
    ): Flow<Resource<List<Vacancy>>> = flow {
        val response = networkClient.doRequest(SearchRequestBig(searchText, currentPage, perPage))
        when (response.resultCode) {
            ERROR -> {
                emit(Resource.Error(resourceProvider.getString(R.string.check_connection)))
            }

            SUCCESS -> {
                with(response as SearchResponse) {
                    val vacanciesList = items.map { mapVacancyFromDto(it, found, pages) }
                    emit(Resource.Success(vacanciesList))
                }

            }

            else -> {
                emit(Resource.Error(resourceProvider.getString(R.string.server_error)))
            }
        }
    }

    private fun mapVacancyFromDto(vacancyDto: VacancyDto, foundValue: Int, pages: Int): Vacancy {
        return Vacancy(
            vacancyDto.id,
            vacancyDto.name,
            vacancyDto.area.name,
            vacancyDto.employer.name,
            found = foundValue,
            vacancyDto.employer.logo_urls?.original,
            getSymbol(vacancyDto.salary?.currency),
            createValue(vacancyDto.salary?.from),
            createValue(vacancyDto.salary?.to),
            pages = pages
        )
    }


    private fun getSymbol(currency: String?): String? {

        return when (currency) {
            "AZN" -> "₼"
            "BYR" -> "Br"
            "EUR" -> "€"
            "GEL" -> "₾"
            "KGS" -> "с"
            "KZT" -> "₸"
            "RUR" -> "₽"
            "UAH" -> "₴"
            "USD" -> "$"
            "UZS" -> "UZS"
            else -> null
        }
    }

    companion object {
        const val ERROR = -1
        const val SUCCESS = 200
    }

}