package com.minux.mask_alarmi.domain.model


data class MAError(
    val code: ECode,
    val message: String
)

enum class ECode(val message: String) {
    N0000("네트워크 오류가 발생했어요"),

    A0000("주소가 존재하지 않아요"),
    A0001("주소 검색에 실패했어요")
}
