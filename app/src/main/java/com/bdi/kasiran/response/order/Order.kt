package com.bdi.kasiran.response.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class Order(
    val order_uuid: String,
    val branch_uuid: String,
    val order_no: String,
    val order_note: String?,
    val payment_type: String,
    val total_diskon: Int?,
    val status: String,
    val created_by: String,
    val created_at: String,
    val updated_at: String,
    val total_transaksi: Int,
    val order_list: List<OrderDetail>
) : Parcelable