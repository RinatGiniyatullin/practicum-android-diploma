package ru.practicum.android.diploma.filters.domain.models

data class Areas(
    val id:String,
    val name:String,
    val areas:List<Region>

)
data class Region(
    val id:String,
    val parentId:String,
    val name:String,
    var isChecked:Boolean = false
)
