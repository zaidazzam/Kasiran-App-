package com.bdi.kasiran.response.menu

data class MenuResponsePost(
    val success: Boolean,
    val code: Int,
    val message: String,
    val `data`: Menu

    )
