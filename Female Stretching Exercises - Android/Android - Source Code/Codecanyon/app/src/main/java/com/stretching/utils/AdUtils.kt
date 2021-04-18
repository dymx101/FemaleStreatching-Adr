package com.stretching.utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.stretching.R
import com.stretching.interfaces.AdsCallback
import com.stretching.interfaces.CallbackListener


internal object AdUtils {


    fun loadGoogleBannerAd(context: Context, llAdview: RelativeLayout, type: String) {
        try {
            Log.e(
                "TAG",
                "loadBannerAdBackend::::::: " + Utils.isPurchased(context) + "  " + Debug.DEBUG_IS_HIDE_AD
            )
            if (Debug.DEBUG_IS_HIDE_AD.not() && Utils.isPurchased(context).not()) {
                val adViewBottom = AdView(context)
                if (type.equals(Constant.BANNER_TYPE)) {
                    adViewBottom.adSize = AdSize.BANNER
                } else if (type.equals(Constant.REC_BANNER_TYPE)) {
                    adViewBottom.adSize = AdSize.MEDIUM_RECTANGLE
                }
                adViewBottom.adUnitId = Constant.GOOGLE_BANNER
                llAdview.addView(adViewBottom)
                val adRequest = AdRequest.Builder().build()
                adViewBottom.loadAd(adRequest)
                adViewBottom.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        adViewBottom.visibility = View.VISIBLE
                        llAdview.visibility = View.VISIBLE
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError?) {
                        Log.e("TAG", "onAdFailedToLoad:Faild :::  " + p0.toString())
                        super.onAdFailedToLoad(p0)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var mInterstitialAd: InterstitialAd? = null
    fun loadGoogleFullAd(context: Context, adsCallback: AdsCallback) {
        if (Debug.DEBUG_IS_HIDE_AD.not() && Utils.isPurchased(context).not()) {
            showAdsDialog(context)
            mInterstitialAd = InterstitialAd(context)
            mInterstitialAd!!.adUnitId = Constant.GOOGLE_INTERSTITIAL
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
        } else {
            adsCallback.startNextScreenAfterAd()
        }
    }


    fun loadFacebookFullAd(context: Context, adsCallback: AdsCallback) {
        if (Debug.DEBUG_IS_HIDE_AD.not() && Utils.isPurchased(context).not()) {
            showAdsDialog(context)
            var interstitialAd = com.facebook.ads.InterstitialAd(context, Constant.FB_INTERSTITIAL)
            val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad?) {
                    Log.e("TAG", "Interstitial ad displayed.")

                }

                override fun onInterstitialDismissed(ad: Ad?) {
                    Log.e("TAG", "Interstitial ad dismissed.")
                    adsCallback.startNextScreenAfterAd()
                }

                override fun onError(ad: Ad?, adError: com.facebook.ads.AdError) {
                    if (adsDialog != null && adsDialog!!.isShowing) {
                        adsDialog!!.dismiss()
                    }
                    adsCallback.startNextScreenAfterAd()
                }

                override fun onAdLoaded(ad: Ad?) {
                    Log.e("TAG", "Interstitial ad is loaded and ready to be displayed!")
                    // Show the ad
                    if (adsDialog != null && adsDialog!!.isShowing) {
                        adsDialog!!.dismiss()
                    }
                    interstitialAd.show()
                }

                override fun onAdClicked(ad: Ad?) {
                    Log.d("TAG", "Interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad?) {
                    Log.d("TAG", "Interstitial ad impression logged!")
                }
            }

            interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build()
            )
        } else {
            adsCallback.startNextScreenAfterAd()
        }

    }

    fun loadFacebookBannerAd(context: Context, banner_container: LinearLayout) {
        if (Debug.DEBUG_IS_HIDE_AD.not() && Utils.isPurchased(context).not()) {


            Log.e("TAG", "loadFbAdFacebook::::::::::: ")
            var adView: com.facebook.ads.AdView? = null
            adView =
                com.facebook.ads.AdView(
                    context,
                    Constant.FB_BANNER,
                    com.facebook.ads.AdSize.BANNER_HEIGHT_50
                )


            banner_container.addView(adView)

            val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {
                override fun onError(ad: Ad?, adError: com.facebook.ads.AdError) {
                    // Ad error callback
                    Log.e("TAG", "onError:Fb:::: $adError")
                    banner_container.visibility = View.GONE
                }

                override fun onAdLoaded(ad: Ad?) {
                    // Ad loaded callback
                    Log.e("TAG", "onAdLoaded:::::: ")
                    banner_container.visibility = View.VISIBLE
                }

                override fun onAdClicked(ad: Ad?) {
                    // Ad clicked callback
                }

                override fun onLoggingImpression(ad: Ad?) {
                    // Ad impression logged callback
                }


            }

            adView!!.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())
        }
    }

    fun loadFacebookMediumRectangleAd(context: Context, banner_container: LinearLayout) {
        if (Debug.DEBUG_IS_HIDE_AD.not() && Utils.isPurchased(context).not()) {
            Log.e("TAG", "loadFbAdFacebook::::::::::: ")
            var adView: com.facebook.ads.AdView? = null

            adView =
                com.facebook.ads.AdView(
                    context,
                    Constant.FB_BANNER_MEDIUM_RECTANGLE,
                    com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250
                )


            banner_container.addView(adView)

            val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {
                override fun onError(ad: Ad?, adError: AdError) {
                    // Ad error callback
                    Log.e("TAG", "onError:Fb:::: $adError")
                    banner_container.visibility = View.GONE
                }

                override fun onAdLoaded(ad: Ad?) {
                    // Ad loaded callback
                    Log.e("TAG", "onAdLoaded:::::: ")
                    banner_container.visibility = View.VISIBLE
                }

                override fun onAdClicked(ad: Ad?) {
                    // Ad clicked callback
                }

                override fun onLoggingImpression(ad: Ad?) {
                    // Ad impression logged callback
                }


            }

            adView!!.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())
        }
    }

    private var fbRewardedVideoAd: RewardedVideoAd? = null

    fun loadFacebookVideoAd(context: Context, adsCallback: CallbackListener) {

//        fbRewardedVideoAd = RewardedVideoAd(context, Constant.FB_REWARDED_VIDEO)
        fbRewardedVideoAd = RewardedVideoAd(context, "471216954245533_471245650909330")

        val rewardedVideoAdListener: RewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoClosed() {
                Log.e("TAG", "onRewardedVideoClosed::::: " )
                adsCallback.onCancel()
            }

            override fun onAdClicked(p0: Ad?) {
                Log.e("TAG", "onAdClicked:::: "+p0.toString() )
            }

            override fun onRewardedVideoCompleted() {
                Log.e("TAG", "onRewardedVideoCompleted:::::: " )
                adsCallback.onSuccess()
            }

            override fun onError(p0: Ad?, p1: AdError?) {
                Log.e("TAG", "onError:::::: "+p1.toString() )

                adsCallback.onSuccess()
            }

            override fun onAdLoaded(p0: Ad?) {
                Log.e("TAG", "onAdLoaded:::::: " +p0!!.isAdInvalidated+"  "+p0!!.placementId)
            }

            override fun onLoggingImpression(p0: Ad?) {
                Log.e("TAG", "onLoggingImpression::::::: ")
            }

        }

        fbRewardedVideoAd!!.loadAd(
            fbRewardedVideoAd!!.buildLoadAdConfig()
                .withAdListener(rewardedVideoAdListener)
                .build()
        )

        /*if (fbRewardedVideoAd!!.isAdLoaded) {
            // showing Video Ad
            fbRewardedVideoAd!!.show();
        }
        else {
            // Loading Video Ad If it  is Not Loaded
            fbRewardedVideoAd!!.loadAd();
        }*/

        // RewardedVideoAd AdListener

        // RewardedVideoAd AdListener


        // loading Ad

        // loading Ad
//        fbRewardedVideoAd!!.loadAd()
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