package com.taptri.ui.activities

import android.annotation.TargetApi
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.taptri.databinding.LoadingActivityBinding
import com.taptri.model.database.UrlDataBase
import com.taptri.util.Checkers
import com.taptri.ui.viewmodel.LeprechaunViewModel
import com.taptri.ui.viewmodel.LeprechaunViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class LoadingActivity : AppCompatActivity() {

    private val TAG = "Leprechaun ViewModel"
    private var _binding: LoadingActivityBinding? = null
    private val binding get() = _binding!!
    private val checker = Checkers(this)
    private lateinit var leprechaunViewModel: LeprechaunViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = LoadingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory =
            LeprechaunViewModelFactory(application, UrlDataBase(applicationContext))
        leprechaunViewModel =
            ViewModelProvider(this, viewModelFactory)[LeprechaunViewModel::class.java]

        if (!checker.isDeviceSecured(this)) {
            Log.d(TAG, "passed secure check")
            leprechaunViewModel.getUrl().observe(this) { urlEntitry ->
                urlEntitry?.let {
                    Log.d(TAG, "checking entity ${urlEntitry}")
                    if ((urlEntitry.url == "null") && !urlEntitry.flag) {
                        Log.d(TAG, "entity is empty")
                        lifecycleScope.launch(Dispatchers.IO) {
                            leprechaunViewModel.getDeepLink(this@LoadingActivity)
                            Log.d(TAG, "started deeplink")
                            lifecycleScope.launch(Dispatchers.Main) {
                                leprechaunViewModel.urlLiveData.observe(this@LoadingActivity) {
                                    startWebView(it)
                                    Log.d(TAG, "webView started with new url")
                                }
                            }
                        }
                    } else {
                        leprechaunViewModel.getUrl().observe(this) {
                            startWebView(it!!.url)
                        }
                    }
                }
            }
        } else {
            startActivity(Intent(this, GameActivity::class.java))
        }

//        when {
//            !checker.isDeviceSecured(this) -> {
//
//                leprechaunViewModel.getUrl().observe(this) {
//                    if (it?.url == "null" && it.flag == false) {
//
//                        lifecycleScope.launch(Dispatchers.IO) {
//                            leprechaunViewModel.getDeepLink(this@LoadingActivity)
//
//                            lifecycleScope.launch(Dispatchers.Main) {
//                                leprechaunViewModel.urlLiveData.observe(this@LoadingActivity) {
//                                    startWebView(it)
//                                }
//                            }
//                        }
//                    } else {
//                        leprechaunViewModel.getUrl().observe(this) {
//                            if (it != null) {
//                                startWebView(it.url)
//                            }
//                        }
//                    }
//                }
//
//            }
//            else -> {
//                startActivity(Intent(this, GameActivity::class.java))
//            }
//        }
    }

    private fun startWebView(url: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}