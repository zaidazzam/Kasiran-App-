package com.bdi.kasiran.ui.auth

import SessionManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bdi.kasiran.MainActivity
import com.bdi.kasiran.R
import com.bdi.kasiran.databinding.ActivityLoginBinding
import com.bdi.kasiran.network.BaseRetrofit
import com.bdi.kasiran.response.login.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val api by lazy { BaseRetrofit().endpoint }
    companion object {
        lateinit var sessionManager: SessionManager
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val loginStatus = sessionManager.getBoolean("LOGIN_STATUS")

        if (loginStatus) {
            val moveIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
        val textViewDaftar: TextView = findViewById(R.id.text_link_daftar)
        textViewDaftar.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6288980077538"))
            startActivity(intent)
        }


        val btnLogin: Button = binding.btnLogin
        val txtEmail: EditText = binding.edtEmail
        val txtPassword: EditText = binding.edtPassword

        btnLogin.setOnClickListener {
            api.login(txtEmail.text.toString(), txtPassword.text.toString()).enqueue(object :
                Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val loginResponse = response.body()

                    if (loginResponse != null && loginResponse.success) {
                        val token = loginResponse.data.token
                        sessionManager.saveString("TOKEN", "Bearer $token")
                        sessionManager.saveBoolean("LOGIN_STATUS", true)

                        // Check if the 'user' property is present in the response
                        val user = loginResponse.data.user
                        if (user != null) {
                            sessionManager.saveString("ADMIN_ID", user.id.toString())
                        }

                        Toast.makeText(
                            applicationContext,
                            "Password Benar",
                            Toast.LENGTH_LONG
                        ).show()

                        val moveIntent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(moveIntent)
                        finish()

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Password salah",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginError", t.toString())
                }
            })
        }
    }
}
