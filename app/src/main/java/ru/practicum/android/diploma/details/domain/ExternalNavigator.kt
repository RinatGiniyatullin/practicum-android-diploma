package ru.practicum.android.diploma.details.domain

interface ExternalNavigator {
    fun shareVacancyUrl(vacancyUrl: String)
    fun sharePhone(phone: String)
    fun shareEmail(email: String)
}