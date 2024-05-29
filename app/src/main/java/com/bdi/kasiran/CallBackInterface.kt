package com.bdi.kasiran

import com.bdi.kasiran.response.cart.Cart
import com.bdi.kasiran.response.menu.Branch

interface CallBackInterface {
    fun passResultCallback(total: String, cart: ArrayList<Cart>, branch: Branch)
}