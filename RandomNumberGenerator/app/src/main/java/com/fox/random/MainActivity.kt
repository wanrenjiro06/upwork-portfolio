package com.fox.random

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fox.random.databinding.ActivityMainBinding

/**
 * Activity 1 — WebView host.
 *
 *  - Loads [BASE_URL] in an embedded WebView.
 *  - Real navigation links open in the external mobile browser.
 *  - Buttons inside the page open Activity 2 (the random number generator)
 *    via the [WebAppBridge] JavaScript interface.
 *  - Checks the internet connection on launch and asks the user to exit if offline.
 *  - Shows the RateUs dialog following the 2nd-launch-onward logic in [RateManager].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        /** Page loaded inside the WebView. */
        const val BASE_URL = "https://fox888.gb.net/guess/"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // No connection -> ask the user to retry or exit.
        if (!isOnline()) {
            showNoInternetDialog()
            return
        }

        setupWebView()
        binding.webView.loadUrl(BASE_URL)

        // RateUs: decides internally whether this launch should show the dialog.
        RateManager.onAppLaunched(this)
        if (RateManager.shouldShow(this)) {
            RateManager.showRateDialog(this)
        }

        // Let the WebView handle Back navigation when possible.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupWebView() {
        val web = binding.webView
        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = false
        }

        web.addJavascriptInterface(WebAppBridge(), "Android")

        web.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()

                // Non-web schemes (mailto:, tel:, intent:, etc.) -> let the system handle.
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    openExternally(url)
                    return true
                }

                // Keep the guess page (and its in-page navigation) inside the WebView.
                if (url.startsWith(BASE_URL)) {
                    return false
                }

                // Any other link -> open in the external mobile browser.
                openExternally(url)
                return true
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = WebView.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = WebView.GONE
                // Bind page buttons so a tap opens Activity 2 (the generator).
                view.evaluateJavascript(BUTTON_BRIDGE_JS, null)
            }
        }
    }

    /** Opens a URL in the device's default browser / handler app. */
    private fun openExternally(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            // No app can handle this URL — ignore gracefully.
        }
    }

    /** Returns true when the device currently has an internet-capable network. */
    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.no_internet_title)
            .setMessage(R.string.no_internet_message)
            .setCancelable(false)
            .setPositiveButton(R.string.retry) { dialog, _ ->
                dialog.dismiss()
                recreate()
            }
            .setNegativeButton(R.string.exit) { _, _ -> finishAffinity() }
            .show()
    }

    /**
     * JavaScript bridge. The injected script calls [openGenerator] when the user
     * taps a button inside the web page, which opens Activity 2.
     */
    inner class WebAppBridge {
        @JavascriptInterface
        fun openGenerator() {
            runOnUiThread {
                startActivity(Intent(this@MainActivity, RandomActivity::class.java))
            }
        }
    }
}

/**
 * Injected after each page load. Routes button taps (and button-like anchors)
 * to the native generator via the `Android` bridge, while real links keep their
 * normal navigation (handled by shouldOverrideUrlLoading -> external browser).
 */
private const val BUTTON_BRIDGE_JS = """
(function() {
  function openNative(e) {
    try {
      if (window.Android && window.Android.openGenerator) {
        window.Android.openGenerator();
        if (e) { e.preventDefault(); e.stopPropagation(); }
      }
    } catch (err) {}
  }
  var buttons = document.querySelectorAll('button, input[type=button], input[type=submit], [role=button]');
  for (var i = 0; i < buttons.length; i++) {
    buttons[i].addEventListener('click', openNative, true);
  }
  // Anchors that act as buttons (no real navigation target).
  var links = document.querySelectorAll('a');
  for (var j = 0; j < links.length; j++) {
    var href = links[j].getAttribute('href');
    if (!href || href === '#' || href.indexOf('javascript:') === 0) {
      links[j].addEventListener('click', openNative, true);
    }
  }
})();
"""
