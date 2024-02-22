package com.bdi.kasiran

import androidx.fragment.app.Fragment
import com.bdi.kasiran.network.BaseRetrofit

class PaymentFragmen : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }

}