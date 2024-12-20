package com.example.act_mobile_application

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import androidx.appcompat.app.ActionBarDrawerToggle

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerMenuAdapter: DrawerMenuAdapter
    private var isUserLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        drawerLayout = findViewById(R.id.drawer_layout)
        webView = findViewById(R.id.webview)
        recyclerView = findViewById(R.id.nav_view)

        val webSettings: WebSettings = webView.settings
        webView.loadUrl("file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Homepage.html")

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val allMenuItems = getAllMenuItems()
        drawerMenuAdapter = DrawerMenuAdapter(getVisibleMenuItems(allMenuItems)) { item ->
            webView.loadUrl(item.link)
            drawerLayout.closeDrawers()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = drawerMenuAdapter

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                hideHorizontalMenu()
                checkLoginState() // Check the login state when the page is loaded
            }
        })

        setupUI()
    }

    private fun setupUI() {
        updateMenuItems()
    }

    private fun hideHorizontalMenu() {
        webView.evaluateJavascript(
            """
            (function() {
                var menu = document.getElementById('nav-links');
                if (menu) menu.style.display = 'none';
            })();
            """,
            null
        )
    }

    private fun checkLoginState() {
        webView.evaluateJavascript(
            """
        (function() {
            // Check login state by validating the presence of username, email, and password fields
            var usernameField = document.getElementById('username');
            var emailField = document.getElementById('email');
            var passwordField = document.getElementById('password');
            // Return false (not logged in) if any of the fields are present
            return (usernameField || emailField || passwordField) ? false : true;
        })();
        """
        ) { result ->
            isUserLoggedIn = result.toBoolean()
            updateMenuItems()
        }
    }


    private fun updateMenuItems() {
        val allMenuItems = getAllMenuItems()
        drawerMenuAdapter.updateMenuItems(getVisibleMenuItems(allMenuItems))
    }

    private fun getVisibleMenuItems(allMenuItems: List<MenuItem>): List<MenuItem> {
        return if (isUserLoggedIn) {
            allMenuItems
        } else {
            allMenuItems.filter {
                it.title in listOf("Home", "AI Chatbot", "Support", "Review", "Login", "Register")
            }
        }
    }

    private fun getAllMenuItems(): List<MenuItem> {
        return listOf(
            MenuItem("Home", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Homepage.html"),
            MenuItem("Login", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Login.html"),
            MenuItem("Register", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Register.html"),
            MenuItem("Trade Ratings", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Trade-Ratings.html"),
            MenuItem("Buy/Sell", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Purchase.html"),
            MenuItem("News", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Yahoo-News.html"),
            MenuItem("Price Alert System", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Price-System.html"),
            MenuItem("AI Predictions", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-AI-Predictions.html"),
            MenuItem("Review", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Reviews.html"),
            MenuItem("Support", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-Support.html"),
            MenuItem("AI Chatbot", "file:///android_asset/Project2024/ACT-HTML-JavaScript/ACT-AI-Chatbot.html")
        )
    }
}
