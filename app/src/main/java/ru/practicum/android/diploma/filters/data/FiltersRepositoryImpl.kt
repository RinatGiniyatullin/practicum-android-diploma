package ru.practicum.android.diploma.filters.data


import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.filters.data.dto.models.CountryDto
import ru.practicum.android.diploma.filters.data.dto.models.AreasDto
import ru.practicum.android.diploma.filters.data.dto.responcse.AreasResponse
import ru.practicum.android.diploma.filters.data.dto.responcse.CountriesResponse
import ru.practicum.android.diploma.filters.domain.FiltersRepository
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.search.data.NetworkClient
import ru.practicum.android.diploma.search.data.ResourceProvider
import ru.practicum.android.diploma.search.data.SearchRepositoryImpl.Companion.ERROR
import ru.practicum.android.diploma.search.data.SearchRepositoryImpl.Companion.SUCCESS
import ru.practicum.android.diploma.search.data.dto.AreaSearchRequest
import ru.practicum.android.diploma.search.data.dto.CountriesSearchRequest

class FiltersRepositoryImpl(private val networkClient: NetworkClient,
                            private val resourceProvider: ResourceProvider,
):FiltersRepository {
    override suspend fun getCountries(): Flow<ru.practicum.android.diploma.util.Resource<List<Country>>> = flow {
        val response = networkClient.getCountries(CountriesSearchRequest)
        when(response.resultCode){
            ERROR  -> {
                emit(ru.practicum.android.diploma.util.Resource.Error(resourceProvider.getString(R.string.check_connection)))
            }
            SUCCESS -> {
                with(response as CountriesResponse){
                    val countriesList = items.map {mapVacancyFromDto(it)  }
                    emit(ru.practicum.android.diploma.util.Resource.Success(countriesList))
                }
            }
            else->{
                emit(ru.practicum.android.diploma.util.Resource.Error(resourceProvider.getString(R.string.server_error)))
            }
        }

    }

    override suspend fun getAres(): Flow<Resource<List<Areas>>> = flow {
        val response = networkClient.getAres(AreaSearchRequest)
        when(response.resultCode){
            ERROR -> {
                emit(Resource.Error(resourceProvider.getString(R.string.check_connection)))
            }
            SUCCESS -> {
                with(response as AreasResponse){
                    val regionList = items.map {mapAresFromDto(it)  }
                    emit(ru.practicum.android.diploma.util.Resource.Success(regionList))
                }
            }
            else->{
                Log.d("myLog", response.resultCode.toString())
                emit(ru.practicum.android.diploma.util.Resource.Error(resourceProvider.getString(R.string.server_error)))
            }
        }
    }

    private fun mapVacancyFromDto(countryDto: CountryDto): Country {
        return Country(
            countryDto.url,
            countryDto.id,
            countryDto.name

        )
    }
    private fun mapAresFromDto(areasDto: AreasDto):Areas{
        return Areas(
                areasDto.id,
                areasDto.parent_id,
                areasDto.name
        )
    }

}