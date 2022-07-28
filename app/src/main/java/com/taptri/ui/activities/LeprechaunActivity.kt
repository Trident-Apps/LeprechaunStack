package com.taptri.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.taptri.LeprechaunApp.Companion.gadId
import com.taptri.R
import com.taptri.util.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeprechaunActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.leprechaun_activity)

        gadId = lifecycleScope.launch(Dispatchers.IO) {
            AdvertisingIdClient.getAdvertisingIdInfo(applicationContext).id.toString()
        }.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            OneSignal.initWithContext(applicationContext)
            OneSignal.setAppId(Const.ONE_SIGNAL_ID)
            OneSignal.setExternalUserId(gadId)
        }

        startActivity(Intent(this@LeprechaunActivity, LoadingActivity::class.java))
        finish()
    }
}