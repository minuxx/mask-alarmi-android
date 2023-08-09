package com.minux.mask_alarmi.data.repository

import android.content.Context

class StoreRepository private constructor(context: Context){
    companion object {
        private var INSTANCE: StoreRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = StoreRepository(context)
            }
        }

        fun get(): StoreRepository {
            return INSTANCE ?: throw IllegalStateException("StoreRepository must be initialized")
        }
    }
}