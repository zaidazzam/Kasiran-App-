package com.bdi.kasiran.response.logout

import com.bdi.kasiran.response.login.Data

data class LogoutResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: Data
)