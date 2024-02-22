package com.bdi.kasiran.response.menu

data class MenuResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<Menu>
)