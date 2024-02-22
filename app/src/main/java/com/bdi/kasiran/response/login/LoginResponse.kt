package com.bdi.kasiran.response.login

data class LoginResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: Data
)

