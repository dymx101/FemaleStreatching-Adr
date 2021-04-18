package com.stretching

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stretching.databinding.ActivityExerciesVideoBinding
import com.stretching.objects.HomeExTableClass
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import java.util.*


class ExerciseVideoActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    var binding: ActivityExerciesVideoBinding? = null
    var player: YouTubePlayer? = null
    var workoutPlanData: HomeExTableClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercies_video)

//        AdUtils.loadBannerAd(binding!!.adView,this)
//        AdUtils.loadBannerGoogleAd(this,binding!!.llAdView, Constant.REC_BANNER_TYPE)
            
        if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_GOOGLE) {
            AdUtils.loadGoogleBannerAd(this, binding!!.llAdView, Constant.BANNER_TYPE)
            binding!!.llAdViewFacebook.visibility=View.GONE
        }else if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_FACEBOOK) {
            AdUtils.loadFacebookBannerAd(this,binding!!.llAdViewFacebook)

        }else{
            binding!!.llAdViewFacebook.visibility=View.GONE
        }


        if (Utils.isPurchased(this)) {
            binding!!.llAdViewFacebook.visibility = View.GONE
        }

        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("workoutPlanData")) {
                    val str = intent.getStringExtra("workoutPlanData")
                    workoutPlanData = Gson().fromJson(str, object :
                        TypeToken<HomeExTableClass>() {}.type)!!
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()
        YouTubeInitializationResult.SERVICE_MISSING

        try {
            if (workoutPlanData!!.exVideo.isNullOrEmpty().not()) {
                binding!!.youtubeView.initialize(getString(R.string.youtube_api_key), this)
                binding!!.adView.visibility =View.GONE
            } else {
                binding!!.youtubeView.visibility = View.GONE
                binding!!.cardVideo.visibility = View.GONE
                binding!!.cardAnimation.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding!!.adView.adListener = object :AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                if (binding!!.youtubeView.visibility != View.VISIBLE && Utils.isPurchased(this@ExerciseVideoActivity).not())
                {
                    binding!!.adView.visibility = View.VISIBLE
                }else{
                    binding!!.adView.visibility = View.GONE
                }
            }
        }


        try {
            fillData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun fillData() {

        binding!!.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
            Utils.ReplaceSpacialCharacters(workoutPlanData!!.exPath!!)
                ?.let { Utils.getAssetItems(this, it) }

        if (listImg != null) {
            for (i in 0 until listImg.size) {
                val imgview = ImageView(this)
                //            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(this).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                binding!!.viewFlipper.addView(imgview)
            }
        }

        binding!!.viewFlipper.isAutoStart = true
        binding!!.viewFlipper.setFlipInterval(resources.getInteger(R.integer.viewfliper_animation))
        binding!!.viewFlipper.startFlipping()


        binding!!.tvTitle.text = workoutPlanData!!.exName
        binding!!.tvDes.text = workoutPlanData!!.exDescription

    }


    inner class ClickHandler {

        fun onBackClick() {
            finish()
        }

        fun onVideoClick() {
//            binding!!.adView.visibility =View.GONE
            binding!!.youtubeView.visibility = View.VISIBLE
            binding!!.cardVideo.visibility = View.GONE
            binding!!.cardAnimation.visibility = View.VISIBLE
            binding!!.llAdViewFacebook.visibility=View.GONE
        }

        fun onAnimationClick() {
//            binding!!.adView.visibility =View.VISIBLE
            binding!!.youtubeView.visibility = View.GONE
            binding!!.cardVideo.visibility = View.VISIBLE
            binding!!.cardAnimation.visibility = View.GONE
            binding!!.llAdViewFacebook.visibility=View.VISIBLE
            player?.pause()
        }
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        player: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            player?.cueVideo(workoutPlanData!!.exVideo!!.substringAfter("v="))
            this.player = player
        }
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        Log.e("TAG", "onInitializationFailure:::Fail Youtuber:::  "+p0.toString()+"  "+p1.toString() )
    }


    override fun onResume() {

        super.onResume()
    }
}
