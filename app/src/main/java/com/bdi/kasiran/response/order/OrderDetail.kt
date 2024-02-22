package com.bdi.kasiran.response.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderDetail(
    val order_list_uuid: String,
    val order_uuid: String,
    val menu_uuid: String,
    val menu_name: String,
    val menu_price: Int,
    val menu_qty: String,
    val menu_type: String,
    val menu_image: String,
    val menu_desc: String,
    val created_at: String,
    val updated_at: String
) : Parcelable
