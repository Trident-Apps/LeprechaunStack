package com.taptri

import android.app.Application
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.taptri.util.Const

class LeprechaunApp : Application() {
    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(applicationContext)
        OneSignal.setAppId(Const.ONE_SIGNAL_ID)
    }
    companion object {
        lateinit var gadId: String
    }
}