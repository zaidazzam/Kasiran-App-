package com.bdi.kasiran.response.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val id: String,
    val nama: String,
    var harga: Double,
    var qty: Int
):Parcelable
