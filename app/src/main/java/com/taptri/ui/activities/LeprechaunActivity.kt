package com.taptri.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.taptri.R
import com.taptri.util.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeprechaunActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.leprechaun_activity)

        lifecycleScope.launch(Dispatchers.IO) {
            val gadId = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext).id.toString()
            OneSignal.initWithContext(applicationContext)
            OneSignal.setAppId(Const.ONE_SIGNAL_ID)
            OneSignal.setExternalUserId(gadId)
        }

        startActivity(Intent(this@LeprechaunActivity, LoadingActivity::class.java))
    }
}