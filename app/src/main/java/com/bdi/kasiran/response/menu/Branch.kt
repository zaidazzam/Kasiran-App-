package com.bdi.kasiran.response.menu

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Branch(
    val branch_uuid: String,
    val owner_uuid: String,
    val branch_name: String,
    val branch_address: String,
    val branch_phone: String,
    val branch_email: String,
    val status: String,
    val created_at: String,
    val updated_at: String
) : Parcelable
