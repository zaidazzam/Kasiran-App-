package com.bdi.kasiran.response.order



data class OrderResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<Order>
)