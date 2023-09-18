package ru.practicum.android.diploma.db.data.converter

import android.annotation.SuppressLint
import ru.practicum.android.diploma.db.data.entity.VacancyEntity
import ru.practicum.android.diploma.details.domain.models.Area
import ru.practicum.android.diploma.details.domain.models.Contacts
import ru.practicum.android.diploma.details.domain.models.Employer
import ru.practicum.android.diploma.details.domain.models.Experience
import ru.practicum.android.diploma.details.domain.models.KeySkill
import ru.practicum.android.diploma.details.domain.models.LogoUrls
import ru.practicum.android.diploma.details.domain.models.Phone
import ru.practicum.android.diploma.details.domain.models.Schedule
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.createValue
import java.text.SimpleDateFormat
import java.util.Date

class VacancyDbConverter {
    fun map(vacancyEntity: VacancyEntity): Vacancy {
        return Vacancy(
            id = vacancyEntity.id,
            name = vacancyEntity.name,
            city = vacancyEntity.city,
            employerName = vacancyEntity.employerName,
            employerLogoUrl = vacancyEntity.employerLogoUrl,
            salaryCurrency = vacancyEntity.salaryCurrency,
            salaryFrom = createValue(vacancyEntity.salaryFrom),
            salaryTo = createValue(vacancyEntity.salaryTo),
            found = 0,
            pages = 0
        )
    }

    private fun getContacts(vacancyEntity: VacancyEntity): Contacts? {
        if (vacancyEntity.contactEmail.isNullOrEmpty()
            && vacancyEntity.contactName.isNullOrEmpty()
            && getPhonesList(vacancyEntity).isNullOrEmpty()) return null

        return Contacts(
            vacancyEntity.contactEmail,
            vacancyEntity.contactName,
            getPhonesList(vacancyEntity)
        )
    }

    fun mapDetail(vacancyEntity: VacancyEntity): VacancyDetails {
        return VacancyDetails(
            contacts = getContacts(vacancyEntity),
            description = vacancyEntity.description,
            alternateUrl = vacancyEntity.alternateUrl,
            area = Area(name = vacancyEntity.area),
            employer = getEmployer(vacancyEntity),
            experience = getExperience(vacancyEntity),
            keySkills = getKeySkillsList(vacancyEntity),
            schedule = getSchedule(vacancyEntity)
        )
    }

    fun map(vacancy: Vacancy, vacancyDetails: VacancyDetails): VacancyEntity {
        return VacancyEntity(
            id = vacancy.id,
            name = vacancy.name,
            city = vacancy.city,
            employerName = vacancy.employerName,
            employerLogoUrl = vacancy.employerLogoUrl,
            salaryCurrency = vacancy.salaryCurrency,
            salaryFrom = vacancy.salaryFrom?.filterNot { it.isWhitespace() }?.toInt(),
            salaryTo = vacancy.salaryTo?.filterNot { it.isWhitespace() }?.toInt(),
            getCurrentDate(),

            contactEmail = vacancyDetails.contacts?.email,
            contactName = vacancyDetails.contacts?.name,
            contactPhones = getContactPhone(vacancyDetails.contacts?.phones),
            contactComment = getContactComment(vacancyDetails.contacts?.phones),
            description = vacancyDetails.description,
            alternateUrl = vacancyDetails.alternateUrl,
            area = vacancyDetails.area.name,
            originalLogo = vacancyDetails.employer?.logoUrls?.original ?: "",
            experience = vacancyDetails.experience?.name,
            keySkillsList = getKeySkillsStr(vacancyDetails.keySkills),
            schedule = vacancyDetails.schedule?.name
        )
    }

    private fun getContactPhone(phones: Array<Phone>?): String {
        if (phones.isNullOrEmpty()) return ""
        val firstPhoneContact = phones[0]
        val phone = "+${firstPhoneContact.country}" +
                "(${firstPhoneContact.city})" +
                firstPhoneContact.number.dropLast(4) +
                "-${firstPhoneContact.number.drop(3).dropLast(2)}" +
                "-${firstPhoneContact.number.drop(5)}"
        return phone
    }

    private fun getContactComment(phones: Array<Phone>?): String? {
        if (phones.isNullOrEmpty()) return ""
        return phones[0].comment
    }

    private fun getKeySkillsStr(keySkills: Array<KeySkill>): String {
        var keySkillStr = ""
        keySkills.forEach { keySkill ->
            keySkillStr += keySkill.name + " ,"
        }
        return keySkillStr
    }

    private fun getPhonesList(vacancyEntity: VacancyEntity): Array<Phone>? {
        if (vacancyEntity.contactPhones.isNullOrEmpty()) return null
        val phone = vacancyEntity.contactPhones
        val country = phone[1].toString()
        val city = phone.substring(3, 6)
        val number = phone.substring(7, 10) + phone.substring(11, 13) + phone.substring(14, 16)
        val comment = vacancyEntity.contactComment
        val phonesList = arrayListOf<Phone>()
        phonesList.add(Phone(city, country, number, comment))
        return phonesList.toTypedArray()
    }

    private fun getSchedule(vacancyEntity: VacancyEntity): Schedule? {
        return if(vacancyEntity.schedule.isNullOrEmpty()) null
        else return Schedule(name = vacancyEntity.schedule)
    }

    private fun getKeySkillsList(vacancyEntity: VacancyEntity): Array<KeySkill> {
        if (vacancyEntity.keySkillsList.isNullOrEmpty()) return arrayOf<KeySkill>()
        val keySkills = arrayListOf<KeySkill>()
        val keySkillsNameList = vacancyEntity.keySkillsList.split(",").toList()
        keySkillsNameList.forEach {keySkillsName ->
            keySkills.add(KeySkill(name = keySkillsName))
        }
        return keySkills.toTypedArray()
    }

    private fun getExperience(vacancyEntity: VacancyEntity): Experience? {
        return if (vacancyEntity.experience.isNullOrEmpty()) null
        else Experience(name = vacancyEntity.experience)
    }

    private fun getEmployer(vacancyEntity: VacancyEntity): Employer? {
        return if (vacancyEntity.originalLogo.isNullOrEmpty()) null
        else Employer(
            LogoUrls(
                v90 = "",
                v240 = "",
                original = vacancyEntity.originalLogo
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        return sdf.format(Date())
    }
}