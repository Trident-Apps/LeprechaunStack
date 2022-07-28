package com.taptri.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.taptri.LeprechaunApp.Companion.gadId
import com.taptri.model.UrlEntity
import com.taptri.model.database.UrlDataBase
import com.taptri.util.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class LeprechaunViewModel(app: Application, private val db: UrlDataBase) : AndroidViewModel(app) {

    var urlLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            gadId = AdvertisingIdClient.getAdvertisingIdInfo(app).id.toString()
            OneSignal.setExternalUserId(gadId)
        }
    }

    fun getDeepLink(activity: Activity) {

        AppLinkData.fetchDeferredAppLinkData(activity.applicationContext) {
            if (it?.targetUri.toString() == "null") {
                getAppsFlyer(activity)
            } else {
                urlLiveData.postValue(createUrl(it?.targetUri.toString(), null, activity))
                sendOneSignalTag(it?.targetUri.toString(), null)
            }

        }

    }

    private fun getAppsFlyer(activity: Activity) {
        AppsFlyerLib.getInstance().init(
            Const.APPS_DEV_KEY,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    sendOneSignalTag("null", p0)
                    urlLiveData.postValue(createUrl("null", p0, activity))

                }

                override fun onConversionDataFail(p0: String?) {
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                }

                override fun onAttributionFailure(p0: String?) {
                }
            }, activity
        )
        AppsFlyerLib.getInstance().start(activity)
    }

    private fun sendOneSignalTag(deepLink: String, data: MutableMap<String, Any>?) {
        OneSignal.setExternalUserId(gadId)
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

        val url = Const.BASE_URL.toUri().buildUpon().apply {
            appendQueryParameter(
                Const.SECURE_GET_PARAMETR,
                Const.SECURE_KEY
            )
            appendQueryParameter(Const.DEV_TMZ_KEY, TimeZone.getDefault().id)
            appendQueryParameter(Const.GADID_KEY, gadId)
            appendQueryParameter(Const.DEEPLINK_KEY, deepLink)
            appendQueryParameter(
                Const.SOURCE_KEY,
                data?.get("media_source").toString()
            )
            appendQueryParameter(
                Const.AF_ID_KEY,
                AppsFlyerLib.getInstance().getAppsFlyerUID(activity.applicationContext)
            )
            appendQueryParameter(
                Const.ADSET_KEY,
                data?.get("adset_id").toString()
            )
            appendQueryParameter(
                Const.CAMPAIGN_ID_KEY,
                data?.get("campaign_id").toString()
            )
            appendQueryParameter(
                Const.APP_CAMPAIGN_KEY,
                data?.get("campaign").toString()
            )
            appendQueryParameter(Const.ADSET_KEY, data?.get("adset").toString())
            appendQueryParameter(
                Const.ADGROUP_KEY,
                data?.get("adgroup").toString()
            )
            appendQueryParameter(
                Const.ORIG_CONST_KEY,
                data?.get("orig_cost").toString()
            )
            appendQueryParameter(
                Const.AF_SITE_ID_KEY,
                data?.get("af_siteid").toString()
            )

        }.toString()
        return url

    }

    fun saveUrl(urlEntity: UrlEntity) = viewModelScope.launch(Dispatchers.IO) {
        db.getDao().insertUrl(urlEntity)
    }

    fun getUrl() = db.getDao().getUrl()
}