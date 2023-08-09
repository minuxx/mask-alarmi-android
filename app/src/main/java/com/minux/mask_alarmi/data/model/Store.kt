package com.minux.mask_alarmi.data.model

data class Store (
    val code: String,
    val lat: Long,
    val lng: Long,
    val addr: String,
    val name: String,
    val type: String,
    val remain_stat: String,
    val stock_at: String,
    val created_at: String,
)