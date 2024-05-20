package com.bdi.kasiran

import SessionManager
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bdi.kasiran.ui.auth.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // Pastikan Anda memanggil ini jika menggunakan custom toolbar
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_fragment)

        // Tentukan semua top-level destination IDs di sini
        val topLevelDestinations = setOf(R.id.diskonFragment, R.id.laporanFragment, R.id.menuFragment, R.id.menuOrderFragment)
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        // Setup action bar dan bottom navigation dengan navController dan appBarConfiguration
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                onLogoutClicked()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onLogoutClicked() {
        AlertDialog.Builder(this)
            .setTitle("Peringatan")
            .setMessage("Apa kamu yakin untuk Logout?")
            .setPositiveButton("Ya") { _, _ ->
                sessionManager.clearSession()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                Toast.makeText(this, "Berhasil logout!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.create().show()
    }
}
