package com.stretching

import android.os.Bundle
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
import com.stretching.objects.HomePlanTableClass
import com.stretching.utils.AdUtils
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

        if (workoutPlanData!!.exVideo.isNullOrEmpty().not()) {
            binding!!.youtubeView.initialize(getString(R.string.youtube_api_key), this)
            binding!!.adView.visibility =View.GONE
        } else {
            binding!!.youtubeView.visibility = View.GONE
            binding!!.cardVideo.visibility = View.GONE
            binding!!.cardAnimation.visibility = View.GONE
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


        fillData()
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
        }

        fun onAnimationClick() {
//            binding!!.adView.visibility =View.VISIBLE
            binding!!.youtubeView.visibility = View.GONE
            binding!!.cardVideo.visibility = View.VISIBLE
            binding!!.cardAnimation.visibility = View.GONE
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

    }


}
