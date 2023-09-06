package ru.practicum.android.diploma.filters.data.dto.models

import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Region

data class AreasDto(
    val id:String,
    val name:String,
    val areas:List<RegionDto>
)
data class RegionDto(
    val id:String,
    val name:String,
    val parent_id:String
)
