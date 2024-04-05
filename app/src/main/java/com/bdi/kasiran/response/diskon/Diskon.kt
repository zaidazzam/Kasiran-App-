package com.bdi.kasiran.response.diskon

import android.os.Parcelable
import com.bdi.kasiran.response.menu.Branch
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Diskon(
    val diskon: String,
    val diskon_level: String,
    val parent_uuid: String,
    val diskon_code: String,
    val percent: String?, // Assuming percent can be null based on your JSON
    val nominal: String,
    val created_by: String,
    val created_at: String,
    val updated_at: String,
    val branch: Branch
) : Parcelable