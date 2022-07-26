package com.taptri.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.taptri.R
import java.util.*


class LeprechaunViewModel(app: Application) : AndroidViewModel(app) {
    private val TAG = "LeprechaunViewModel"
    lateinit var res: Resources
    var urlLiveData: MutableLiveData<String> = MutableLiveData()

    fun getDeepLink(activity: Activity) {
        Log.d(TAG, "deeplink started")
        AppLinkData.fetchDeferredAppLinkData(activity.applicationContext) {
            Log.d(TAG, it?.targetUri.toString())
            if (it?.targetUri.toString() == "null") {
                Log.d(TAG, "app started")
                getAppsFlyer(activity)
            } else {
                urlLiveData.postValue(createUrl(it?.targetUri.toString(), null, activity))
                sendOneSignalTag(it?.targetUri.toString(), null)
            }

        }

    }

    private fun getAppsFlyer(activity: Activity) {
        Log.d(TAG, "appsflyer started")
        AppsFlyerLib.getInstance().init(
            res.getString(R.string.apps_dev_key),
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    Log.d(TAG, " appsflyer success")
                    urlLiveData.postValue(createUrl("null", p0, activity))
                    sendOneSignalTag("null", p0)
                }

                override fun onConversionDataFail(p0: String?) {
                    Log.d(TAG, " appsflyer failure")
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    TODO("Not yet implemented")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(TAG, " appsflyer failure 2")
                }

            }, activity
        )
        AppsFlyerLib.getInstance().start(activity)
    }

    private fun sendOneSignalTag(deepLink: String, data: MutableMap<String, Any>?) {
        val campaign = data?.get("campaign").toString()

        if (campaign == "null" && deepLink == "null") {
            OneSignal.sendTag("key2", "organic")
        } else if (deepLink != "null") {
            OneSignal.sendTag("key2,", deepLink.replace("myapp://", "").substringBefore("/"))
        } else if (campaign != "null") {
            OneSignal.sendTag("key2", campaign.substringBefore("_"))
        }
    }

    private fun createUrl(
        deepLink: String,
        data: MutableMap<String, Any>?,
        activity: Context
    ): String {
        val gadId =
            AdvertisingIdClient.getAdvertisingIdInfo(getApplication<Application>().applicationContext).id.toString()
        val url = res.getString(R.string.base_url).toUri().buildUpon().apply {
            appendQueryParameter(
                res.getString(R.string.secure_get_parametr),
                res.getString(R.string.secure_key)
            )
            appendQueryParameter(res.getString(R.string.dev_tmz_key), TimeZone.getDefault().id)
            appendQueryParameter(res.getString(R.string.gadid_key), gadId)
            appendQueryParameter(res.getString(R.string.deeplink_key), deepLink)
            appendQueryParameter(
                res.getString(R.string.source_key),
                data?.get("media_source").toString()
            )
            appendQueryParameter(
                res.getString(R.string.af_id_key),
                AppsFlyerLib.getInstance().getAppsFlyerUID(activity.applicationContext)
            )
            appendQueryParameter(
                res.getString(R.string.adset_id_key),
                data?.get("adset_id").toString()
            )
            appendQueryParameter(
                res.getString(R.string.campaign_id_key),
                data?.get("campaign_id").toString()
            )
            appendQueryParameter(
                res.getString(R.string.app_campaign_key),
                data?.get("campaign").toString()
            )
            appendQueryParameter(res.getString(R.string.adset_key), data?.get("adset").toString())
            appendQueryParameter(
                res.getString(R.string.adgroup_key),
                data?.get("adgroup").toString()
            )
            appendQueryParameter(
                res.getString(R.string.orig_cost_key),
                data?.get("orig_cost").toString()
            )
            appendQueryParameter(
                res.getString(R.string.af_siteid_key),
                data?.get("af_siteid").toString()
            )

        }.toString()
        return url
    }
}