package com.taptri

import android.app.Application
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal

class LeprechaunApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val gadId = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext).id.toString()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(getString(R.string.one_signal_id))
        OneSignal.setExternalUserId(gadId)
    }
}