package com.bdi.kasiran.response.diskon

data class DiskonResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<Diskon>
)