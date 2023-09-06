package ru.practicum.android.diploma.search.data
import ru.practicum.android.diploma.search.data.dto.Response

interface
NetworkClient {
    suspend fun doRequest(dto: Any): Response
    suspend fun getAres(dto:Any):Response
    suspend fun getVacancyById(dto: Any): Response
    suspend fun getIndustries(dto: Any):Response
    suspend fun getSimilarVacanciesById(dto: Any): Response

}