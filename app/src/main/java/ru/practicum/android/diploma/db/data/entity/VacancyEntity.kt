package ru.practicum.android.diploma.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacancy_table")
data class VacancyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val city: String,
    val employerName: String,
    val employerLogoUrl: String?,
    val salaryCurrency: String?,
    val salaryFrom: Int?,
    val salaryTo: Int?,
    val currentDate: String,

    val contactEmail: String?,
    val contactName: String?,
    val contactPhones: String?,
    val contactComment: String?,
    val description: String,
    val alternateUrl: String,
    val area: String,
    val originalLogo: String?,
    val experience: String?,
    val keySkillsList: String,
    val schedule: String?
)