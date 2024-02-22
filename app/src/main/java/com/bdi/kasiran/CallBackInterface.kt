package com.bdi.kasiran

import com.bdi.kasiran.response.cart.Cart

interface CallBackInterface {
    fun passResultCallback(total: String, cart: ArrayList<Cart>)
}