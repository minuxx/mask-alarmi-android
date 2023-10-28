package com.minux.mask_alarmi.data.config


data class ErrorResponse(
    val code: ErrorCode,
    val message: String
)

enum class ErrorCode(val message: String) {
    N0000("네트워크 오류가 발생했어요"),

    S0000("상점이 존재하지 않아요"),
    S0001("상점을 찾는데 실패했어요"),

    A0000("주소가 존재하지 않아요"),
    A0001("주소 검색에 실패했어요"),
}
