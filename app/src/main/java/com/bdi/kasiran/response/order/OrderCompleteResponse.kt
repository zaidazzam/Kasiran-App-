package com.bdi.kasiran.response.order

data class OrderCompleteResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: Any? // Use 'Any?' to indicate that the data can be of any type and is nullable
)
