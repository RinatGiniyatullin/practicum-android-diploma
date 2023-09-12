package ru.practicum.android.diploma.search.data.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.details.data.VacancyDetailsResponse
import ru.practicum.android.diploma.filters.data.dto.models.AreasDto
import ru.practicum.android.diploma.filters.data.dto.models.IndustryDto
import ru.practicum.android.diploma.search.data.dto.SearchResponse

interface Api {

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: EmployMe (gerzag96@gmail.com)"
    )
    @GET("/vacancies/{vacancy_id}")
    suspend fun getVacancyById(@Path("vacancy_id") id: String): VacancyDetailsResponse

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: EmployMe (gerzag96@gmail.com)"
    )
    @GET("/vacancies/{vacancy_id}/similar_vacancies")
    suspend fun getSimilarVacanciesById(
        @Path("vacancy_id") id: String,
    ): SearchResponse

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: EmployMe (gerzag96@gmail.com)"
    )
    @GET("/vacancies")
    suspend fun searchQueryMap(@QueryMap options: Map<String, String>): SearchResponse

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: EmployMe (gerzag96@gmail.com)"
    )
    @GET("/areas")
    suspend fun getAreas(
    ): List<AreasDto>

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: EmployMe (gerzag96@gmail.com)"
    )
    @GET("/industries")
    suspend fun getIndustries(): List<IndustryDto>

}