package com.bdi.kasiran.response.login

data class Data(
    val token: String,
    val token_type: String,
    val expires_at_in_minute: Int,
    val user: User
)
