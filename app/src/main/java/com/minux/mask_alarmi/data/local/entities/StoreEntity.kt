package com.minux.mask_alarmi.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.minux.mask_alarmi.data.models.RemainState
import com.minux.mask_alarmi.data.models.Store

@Entity(tableName = "stores")
data class StoreEntity (
    @PrimaryKey val code: String,
    val lat: Double,
    val lng: Double,
    val addr: String,
    val name: String,
    val type: String,
    val remain_stat: String?,
    val stock_at: String?,
    val created_at: String?,
) {
    fun toModel(): Store = Store(
        code = code.toLong(),
        name = name,
        address = addr,
        lat = lat,
        lng = lng,
        remainState = RemainState.fromValue(remain_stat),
        stockAt = stock_at,
    )
}