package com.minux.mask_alarmi.core.util

sealed class UiEvent {
    data class ShowToast(val message: String): UiEvent()
    data class Navigate(val flag: String): UiEvent()
}