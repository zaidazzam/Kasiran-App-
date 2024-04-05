package com.bdi.kasiran.response.menu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class Menu(
    val menu_uuid: String,
    val branch_uuid: String,
    val menu_name: String,
    val menu_price: String,
    var menu_qty: String,
    val menu_type: String,
    val menu_image: String,
    val menu_desc: String,
    val menu_status: String,
    val created_by: String,
    val created_at: String,
    val updated_at: String,
    val branch: Branch
): Parcelable
