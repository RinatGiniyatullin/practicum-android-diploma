package ru.practicum.android.diploma.filters.domain.models

data class Industry(
    val id:String,
    val industries:List<Industries>,
    val name:String
)
data class Industries(
    val id:String,
    val name:String,
)
