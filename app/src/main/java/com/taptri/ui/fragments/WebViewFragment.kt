package com.taptri.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.taptri.R
import com.taptri.databinding.WebViewFragmentBinding

class WebViewFragment : Fragment() {
    lateinit var webView: WebView
    private var _binding: WebViewFragmentBinding? = null
    private val binding get() = _binding
    private var messageAb: ValueCallback<Array<Uri>>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WebViewFragmentBinding.inflate(inflater, container, false)
        webView = binding!!.webView

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.loadUrl(arguments!!.getString("url", null))
        webView.webViewClient = LocalClient()
        webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
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
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(intent, "Image Chooser"), 1
                )

                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {

                val newWebView = WebView(context!!.applicationContext)
                newWebView.settings.javaScriptEnabled = true
                newWebView.webChromeClient = this
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true

            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        webView.canGoBack() -> {
                            webView.goBack()
                        }
                        else -> {
                            isEnabled = false
                        }
                    }
                }
            }
        )

    }

    private inner class LocalClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)

        }

//        override fun onPageFinished(view: WebView?, url: String?) {
//            super.onPageFinished(view, url)
//            url?.let {
//                when{
//                    it == "https://leprechaunstack.live/"->{
//                        findNavController().navigate(R.id.gameFragment)
//                    }else->{
//
//                    }
//                }
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}