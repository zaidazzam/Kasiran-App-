package com.bdi.kasiran.response.order

data class OrderStore(
//    val order_note: String,
    val payment_type: String,
    val diskon_code: String?,
    val order_list: List<OrderItem>
)

data class OrderItem(
    val menu_id: String,
    val qty: Int
)
