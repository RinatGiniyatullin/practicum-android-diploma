package ru.practicum.android.diploma.details.data

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.details.domain.models.Area
import ru.practicum.android.diploma.details.domain.models.Contacts
import ru.practicum.android.diploma.details.domain.models.Employer
import ru.practicum.android.diploma.details.domain.models.Experience
import ru.practicum.android.diploma.details.domain.models.KeySkill
import ru.practicum.android.diploma.details.domain.models.Schedule
import ru.practicum.android.diploma.search.data.dto.Response

data class VacancyDetailsResponse (
    val contacts: Contacts?,
    val description: String,
    @SerializedName("alternate_url")
    val alternateUrl: String,
    val area: Area,
    val employer: Employer?,
    val experience: Experience?,
    @SerializedName("key_skills")
    val keySkills: Array<KeySkill>,
    val schedule: Schedule?,
): Response()