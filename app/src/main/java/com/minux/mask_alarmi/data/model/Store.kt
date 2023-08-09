package com.minux.mask_alarmi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Store (
    @PrimaryKey val code: String,
    val lat: Long,
    val lng: Long,
    val addr: String,
    val name: String,
    val type: String,
    val remain_stat: String,
    val stock_at: String,
    val created_at: String,
)