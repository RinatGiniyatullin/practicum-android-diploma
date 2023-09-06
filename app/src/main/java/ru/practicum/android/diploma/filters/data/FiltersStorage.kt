package ru.practicum.android.diploma.filters.data

import ru.practicum.android.diploma.filters.data.dto.models.FiltersDto

interface FiltersStorage {
    fun doRequest():FiltersDto
    fun doWrite(filtersDto: FiltersDto)
}