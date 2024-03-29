package com.stretching

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stretching.databinding.ActivityExerciesVideoBinding
import com.stretching.databinding.ActivityPauseBeforeStartBinding
import com.stretching.databinding.ActivityRestBinding
import com.stretching.objects.HomeExTableClass
import com.stretching.utils.*
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Debug
import com.stretching.utils.Utils
import java.util.*


class RestActivity : BaseActivity() {

    var binding: ActivityRestBinding? = null
    var timer: CountDownTimerWithPause? = null
    var nextExercise: HomeExTableClass? = null
    var nextPos = 0
    var totalEx = 0

    var timeCountDown = 0L
    var restTime = 20L // in second


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rest)

        AdUtils.loadBannerAd(binding!!.adView,this)
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("nextEx")) {
                    val str = intent.getStringExtra("nextEx")
                    nextExercise = Gson().fromJson(str, object :
                        TypeToken<HomeExTableClass>() {}.type)!!
                }
                if (intent.extras!!.containsKey("nextPos")) {
                    nextPos = intent.getIntExtra("nextPos", 2)
                }
                if (intent.extras!!.containsKey("totalEx")) {
                    totalEx = intent.getIntExtra("totalEx", 0)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()

        restTime = Utils.getPref(this,Constant.PREF_REST_TIME,Constant.DEFAULT_REST_TIME)

        binding!!.tvName.text = nextExercise!!.exName
        binding!!.tvTotalEx.text = "$nextPos / $totalEx"
        if (nextExercise!!.exUnit.equals(Constant.workout_type_step)) {
            binding!!.tvTime.text = "X ${nextExercise!!.exTime}"
        } else {
            binding!!.tvTime.text =
                Utils.secToString(nextExercise!!.exTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
        }

        binding!!.progressBarWorkOutProcess.max = totalEx
        binding!!.progressBarWorkOutProcess.progress = nextPos-1

        binding!!.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
            Utils.ReplaceSpacialCharacters(nextExercise!!.exPath!!)
                ?.let { Utils.getAssetItems(this, it) }

        if (listImg != null) {
            for (i in 0 until listImg.size) {
                val imgview = ImageView(this)
                //            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(this).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                binding!!.viewFlipper.addView(imgview)
            }
        }

        binding!!.viewFlipper.isAutoStart = true
        binding!!.viewFlipper.setFlipInterval(resources.getInteger(R.integer.viewfliper_animation))
        binding!!.viewFlipper.startFlipping()

        if (!nextExercise!!.exUnit.equals(Constant.workout_type_step)) {
            MyApplication.speechText(
                this,
                "Take a rest"
            )
            Handler().postDelayed(Runnable {
                MyApplication.speechText(
                    this,
                    "Next ${nextExercise!!.exTime} seconds ${nextExercise!!.exName}"
                )
            }, 1000)

        } else {
            MyApplication.speechText(
                this,
                "Take a rest"
            )
            Handler().postDelayed(Runnable {
                MyApplication.speechText(
                    this,
                    "Next ${nextExercise!!.exTime} times ${nextExercise!!.exName}"
                )
            }, 1000)

        }
        countDownRest()

    }

    private fun countDownRest() {
        binding!!.tvSecond.text = Utils.secToString(restTime.toInt(), "mm:ss")

        timer = object : CountDownTimerWithPause(restTime * 1000L, 1000, true) {
            override fun onFinish() {
                val i = Intent()
                i.putExtra("restTime",timeCountDown)
                setResult(Activity.RESULT_OK,i)

                timer!!.cancel()
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
                timeCountDown++
                binding!!.tvSecond.text =
                    Utils.secToString(((millisUntilFinished / 1000)).toInt(), "mm:ss")
                /* Debug.e("timeCountDown = " ,""+timeCountDown)
                 Debug.e("millisUntilFinished time = " ,""+(millisUntilFinished/1000))
                 Debug.e("Rest time = " ,""+((millisUntilFinished/1000)))*/
                if (((millisUntilFinished / 1000)) < 4) {
                    //Debug.e("Rest time = " ,""+((millisUntilFinished/1000) - timeCountDown))
                    MyApplication.speechText(
                        this@RestActivity,
                        ((millisUntilFinished / 1000)).toString()
                    )
                }
            }

        }

        Handler().postDelayed(Runnable {
            timer!!.start()
        }, 500)

    }


    inner class ClickHandler {

        fun onSkipClick() {
            val i = Intent()
            i.putExtra("restTime",timeCountDown)
            i.putExtra("isRestSkip",true)
            setResult(Activity.RESULT_OK,i)
            finish()
        }

        fun onPlusTimeClick() {
            Debug.e("old seconds = ", restTime.toString())
            Debug.e("time completed seconds = ", timeCountDown.toString())
            restTime = (restTime - timeCountDown) + 20
            Debug.e("New seconds = ", restTime.toString())
            timer?.cancel()
            countDownRest()
        }

        fun onExerciseInfoClick() {
            pauseTimer()
            val i = Intent(this@RestActivity, ExerciseVideoActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(nextExercise))
            startActivity(i)
        }
    }

    override fun onResume() {
        super.onResume()
        resumeTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }

    fun resumeTimer() {
        if (timer != null && timer!!.isPaused) {
            timer!!.resume()
        }
    }

    fun pauseTimer() {
        if (timer != null && timer!!.isRunning) {
            timer!!.pause()
        }
    }

    override fun onBackPressed() {
        val i = Intent()
        i.putExtra("restTime",timeCountDown)
        i.putExtra("isRestSkip",true)
        setResult(Activity.RESULT_OK,i)
        super.onBackPressed()

    }
}
