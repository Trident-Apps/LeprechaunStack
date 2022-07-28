package com.taptri.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.taptri.databinding.WebViewActivityBinding
import com.taptri.model.UrlEntity
import com.taptri.model.database.UrlDataBase
import com.taptri.viewmodel.LeprechaunViewModel
import com.taptri.viewmodel.LeprechaunViewModelFactory

class WebViewActivity : AppCompatActivity() {
    private val TAG = "LeprechaunViewModel"

    private var _binding: WebViewActivityBinding? = null
    private val binding get() = _binding!!
    lateinit var webView: WebView
    private var messageAb: ValueCallback<Array<Uri>>? = null
    lateinit var leprechaunViewModel: LeprechaunViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = WebViewActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory =
            LeprechaunViewModelFactory(application, UrlDataBase(applicationContext))
        leprechaunViewModel =
            ViewModelProvider(this, viewModelFactory)[LeprechaunViewModel::class.java]

        webView = binding.webViewActivity
        val intentUrl = intent.getStringExtra("url")
        Log.d(TAG, intentUrl.toString())
        if (intentUrl != null) {
            webView.loadUrl(intentUrl)
        }
        webView.webViewClient = LocalClient()
        webView.settings.javaScriptEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                messageAb = filePathCallback

                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)

            }
        }

    }

    private inner class LocalClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (url == "https://leprechaunstack.live/") {
                startActivity(Intent(this@WebViewActivity, GameActivity::class.java))
            } else {
                leprechaunViewModel.getUrl().observe(this@WebViewActivity) {
                    if (it == null) {
                        leprechaunViewModel.saveUrl(UrlEntity(url = url!!))
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}