package com.minux.mask_alarmi.data.models

data class Store(
    val code: Long,
    val lat: Double,
    val lng: Double,
    val address: String,
    val name: String,
    val remainState: RemainState,
    val stockAt: String?,
)

enum class RemainState(val value: String) {
    EMPTY("empty"),
    FEW("few"),
    SOME("some"),
    PLENTY("plenty");

    companion object {
        fun fromValue(value: String?): RemainState = values().firstOrNull { it.value == value } ?: EMPTY
    }
}