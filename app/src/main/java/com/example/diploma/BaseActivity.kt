package com.example.diploma

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

abstract class BaseActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_base)

        authManager = AuthManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigation(navView)
    }

    override fun onResume() {
        super.onResume()
        updateNavMenu()
    }

    private fun setupNavigation(navView: NavigationView) {
        navView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    if (this !is ProfileActivity) startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.nav_organization -> {
                    if (this !is OrganizationActivity) startActivity(Intent(this, OrganizationActivity::class.java))
                }
                R.id.nav_trades -> {
                    if (this !is TradesActivity) startActivity(Intent(this, TradesActivity::class.java))
                }
                R.id.nav_notifications -> {
                    if (this !is NotificationsActivity) startActivity(Intent(this, NotificationsActivity::class.java))
                }
                R.id.nav_login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.nav_logout -> {
                    authManager.clearAuthData()
                    updateNavMenu()
                    // Перезапускаем MainActivity, чтобы гарантированно обновить UI
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        updateNavMenu()
    }

    private fun updateNavMenu() {
        val navView: NavigationView? = findViewById(R.id.nav_view)
        val menu = navView?.menu ?: return
        val isLoggedIn = authManager.isLoggedIn()

        menu.findItem(R.id.nav_login)?.isVisible = !isLoggedIn
        menu.findItem(R.id.nav_logout)?.isVisible = isLoggedIn

        val header = navView.getHeaderView(0)
        if (header != null) {
            val titleView = header.findViewById<TextView>(R.id.nav_header_title)
            val subtitleView = header.findViewById<TextView>(R.id.nav_header_subtitle)

            if (isLoggedIn) {
                val login = authManager.getLogin() ?: "User"
                titleView?.text = login
                subtitleView?.text = "Вы авторизованы"
            } else {
                titleView?.text = "SynApp"
                subtitleView?.text = "Добро пожаловать"
            }
        }
    }

    override fun setContentView(layoutResID: Int) {
        val contentFrame: FrameLayout? = findViewById(R.id.content_frame)
        if (contentFrame != null) {
            contentFrame.removeAllViews()
            LayoutInflater.from(this).inflate(layoutResID, contentFrame, true)
        } else {
            super.setContentView(layoutResID)
        }
    }

    override fun setContentView(view: View) {
        val contentFrame: FrameLayout? = findViewById(R.id.content_frame)
        if (contentFrame != null) {
            contentFrame.removeAllViews()
            contentFrame.addView(view)
        } else {
            super.setContentView(view)
        }
    }
}
