package com.stretching.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.stretching.R
import com.stretching.interfaces.AdsCallback

internal object AdUtils {

    fun loadBannerAd(adViewBottom: AdView,context: Context) {
        try {
            if(Debug.DEBUG_IS_HIDE_AD.not() && Utils.isPurchased(context).not()) {
                val adRequest = AdRequest.Builder().build()
//                adViewBottom.adUnitId = context.getString(R.string.BANNER)
                adViewBottom.loadAd(adRequest)
                adViewBottom.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        adViewBottom.videoController
                        adViewBottom.visibility = View.VISIBLE
                    }

                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        adViewBottom.visibility = View.GONE
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadNativeAd(adViewBottom: AdView) {
        try {
            val adRequest = AdRequest.Builder().build()
            adViewBottom.loadAd(adRequest)
            adViewBottom.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    adViewBottom.visibility = View.VISIBLE
                }

                override fun onAdFailedToLoad(p0: Int) {
                    super.onAdFailedToLoad(p0)
                    adViewBottom.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var mInterstitialAd: InterstitialAd? = null
    fun initFullAdd(context: Context, adsCallback: AdsCallback) {
        if(Debug.DEBUG_IS_HIDE_AD.not() && Utils.isPurchased(context).not()) {
            showAdsDialog(context)
            mInterstitialAd = InterstitialAd(context)
            mInterstitialAd!!.adUnitId = context.getString(R.string.INTERSTITIAL)
            val adRequest = AdRequest.Builder().build()
            mInterstitialAd!!.loadAd(adRequest)
            mInterstitialAd!!.adListener = object : AdListener() {

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    if (adsDialog != null && adsDialog!!.isShowing) {
                        adsDialog!!.dismiss()
                    }
                    mInterstitialAd!!.show()
                }

                override fun onAdFailedToLoad(p0: Int) {
                    super.onAdFailedToLoad(p0)
                    if (adsDialog != null && adsDialog!!.isShowing) {
                        adsDialog!!.dismiss()
                    }
                    adsCallback.startNextScreenAfterAd()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    adsCallback.startNextScreenAfterAd()
                }
            }
        }else{
            adsCallback.startNextScreenAfterAd()
        }
    }

    internal var adsDialog: Dialog? = null
    private fun showAdsDialog(context: Context) {
        adsDialog = Dialog(context)
        adsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        adsDialog!!.setContentView(R.layout.dialog_full_screen_ad)
        adsDialog!!.setCancelable(false)
        adsDialog!!.show()
    }

}