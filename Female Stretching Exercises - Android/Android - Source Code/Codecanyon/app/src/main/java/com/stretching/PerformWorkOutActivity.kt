package com.stretching

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stretching.adapter.WorkoutProgressIndicatorAdapter
import com.stretching.databinding.ActivityPerformWorkoutBinding
import com.stretching.databinding.DialogQuiteWorkoutBinding
import com.stretching.interfaces.AdsCallback
import com.stretching.interfaces.DialogDismissListener
import com.stretching.objects.HomeExTableClass
import com.stretching.objects.HomePlanTableClass
import com.stretching.objects.Music
import com.stretching.utils.*
import com.utillity.db.DataHelper
import java.util.*


class PerformWorkOutActivity : BaseActivity() {

    val TAG = PerformWorkOutActivity::class.java.name + Constant.ARROW
    var binding: ActivityPerformWorkoutBinding? = null
    var workoutProgressIndicatorAdapter: WorkoutProgressIndicatorAdapter? = null
    var workoutPlanData: HomePlanTableClass? = null
    var exercisesList: ArrayList<HomeExTableClass>? = null
    private lateinit var mySoundUtil: MySoundUtil

    var currentPos = 0
    var currentExe: HomeExTableClass? = null
    var totalExTime = 0L

    private var exStartTime: Long = 0
    private var running = false
    private var currentTime: Long = 0
    private var timeCountDown = 0
    var currMusic: Music? = null
    var isPaused = false

    var timer: CountDownTimerWithPause? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_perform_workout)

        initIntentParam()
        init()
        initReadyToGo()

    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("workoutPlanData")) {
                    val str = intent.getStringExtra("workoutPlanData")
                    workoutPlanData = Gson().fromJson(str, object :
                        TypeToken<HomePlanTableClass>() {}.type)!!
                }

                if (intent.extras!!.containsKey("ExcList")) {
                    val str = intent.getStringExtra("ExcList")
                    exercisesList = Gson().fromJson(
                        str,
                        object : TypeToken<ArrayList<HomeExTableClass>>() {}.type
                    )!!
                    binding!!.progressBarTop.max = exercisesList!!.size
                }

                if (intent.extras!!.containsKey("currentPos")) {
                    currentPos = intent.getIntExtra("ExcList", 0)
                    if (exercisesList.isNullOrEmpty().not()) {
                        currentExe = exercisesList!!.get(currentPos)
                    }
                } else {
                    if (exercisesList.isNullOrEmpty().not()) {
                        currentExe = exercisesList!!.get(0)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()

        mySoundUtil = MySoundUtil(this)

        workoutProgressIndicatorAdapter = WorkoutProgressIndicatorAdapter(this)
        binding!!.rcyWorkoutStatus.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding!!.rcyWorkoutStatus.setAdapter(workoutProgressIndicatorAdapter)
        workoutProgressIndicatorAdapter!!.setTotalExercise(exercisesList!!.size)

        initMusic(true)
    }

    private fun initMusic(isPlayMusic: Boolean) {

        if (Utils.getPref(this, Constant.PREF_IS_MUSIC_SELECTED, false)) {
            val str = Utils.getPref(this, Constant.PREF_MUSIC, "")
            if (str.isNullOrEmpty().not()) {
                currMusic = Gson().fromJson<Music>(str, object : TypeToken<Music>() {}.type)
                if (Utils.getPref(this@PerformWorkOutActivity, Constant.PREF_IS_MUSIC_MUTE, false)
                        .not()
                ) {
                    if (isPlayMusic)
                        playMusic()
                }
            }
        }

        setPlayPauseView()
    }

    private fun loadWorkoutImage() {

        binding!!.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
            Utils.ReplaceSpacialCharacters(currentExe!!.exPath!!)
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
    }

    private fun initReadyToGo() {

        loadWorkoutImage()
        binding!!.llReadyToGo.visibility = View.VISIBLE
        binding!!.llAfterStartWithTime.visibility = View.GONE
        binding!!.llAfterStartWithSteps.visibility = View.GONE
        binding!!.tvExcNameReadyToGo.text = currentExe!!.exName

        countDownReadyToGo()

        val readyToGoText = "Ready to go start with ${currentExe!!.exName}"

        MyApplication.speechText(this, readyToGoText)
    }

    private fun countDownReadyToGo() {

        var timeCountDown = 0

        val readyToGoTime =
            Utils.getPref(this, Constant.PREF_READY_TO_GO_TIME, Constant.DEFAULT_READY_TO_GO_TIME)
        binding!!.progressBarReadyToGo.max = readyToGoTime.toInt()

        timer = object : CountDownTimerWithPause(readyToGoTime * 1000L, 1000, true) {
            override fun onFinish() {
                binding!!.tvCountDownReadyToGO.text = "0"
                binding!!.progressBarReadyToGo.progress = readyToGoTime.toInt()
                exStartTime = System.currentTimeMillis()
                startPerformExercise(false)
            }

            override fun onTick(millisUntilFinished: Long) {
                timeCountDown++
                if (readyToGoTime - timeCountDown >= 0) {
                    binding!!.tvCountDownReadyToGO.text = (readyToGoTime - timeCountDown).toString()
                    binding!!.progressBarReadyToGo.progress = timeCountDown

                    if (timeCountDown == readyToGoTime.toInt() / 2) {

                        val readyToGoText = "Please do that on a mat"

                        MyApplication.speechText(this@PerformWorkOutActivity, readyToGoText)

                    } else if ((readyToGoTime - timeCountDown) < 4) {
                        MyApplication.speechText(
                            this@PerformWorkOutActivity,
                            (readyToGoTime - timeCountDown).toString()
                        )
                    }
                } else {
                    timer!!.onFinish()
                    timer!!.cancel()
                }
            }

        }

        Handler().postDelayed(Runnable {
            timer!!.start()
        }, 1000)

    }

    fun startPerformExercise(isNeedDelay: Boolean) {

        workoutProgressIndicatorAdapter!!.setCompletedExercise(currentPos)
        if (currentPos > 0) {
            binding!!.imgPrevWorkout.visibility = View.VISIBLE
        } else {
            binding!!.imgPrevWorkout.visibility = View.GONE
        }

        if (currentExe!!.exUnit.equals(Constant.workout_type_step)) {

            binding!!.tvExcName.text = currentExe!!.exName
            binding!!.tvExcNameStep.text = currentExe!!.exName
            binding!!.tvTotalStep.text = currentExe!!.exTime!!
            binding!!.llReadyToGo.visibility = View.GONE
            binding!!.llAfterStartWithTime.visibility = View.GONE
            binding!!.llAfterStartWithSteps.visibility = View.VISIBLE

        } else {
            binding!!.tvExcName.text = currentExe!!.exName
            binding!!.llReadyToGo.visibility = View.GONE
            binding!!.llAfterStartWithTime.visibility = View.VISIBLE
            binding!!.llAfterStartWithSteps.visibility = View.GONE

            binding!!.progressBarWorkOut.max = currentExe!!.exTime!!.toInt()
            binding!!.progressBarWorkOut.progress = 0
            binding!!.tvCompletedSec.text = "${currentExe!!.exTime!!}\""
            binding!!.tvTotalSec.text = "/ ${currentExe!!.exTime!!}\""
        }

        if (isNeedDelay) {
            val scaleAnimation: Animation = ScaleAnimation(
                1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
            scaleAnimation!!.setDuration(1000)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding!!.tvAnimation.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {

                }

            })
            timer = object : CountDownTimerWithPause(4000, 1000, true) {
                override fun onFinish() {
                    timer?.cancel()
                    timer = null
                    binding!!.tvAnimation.visibility = View.GONE
                    if (currentExe!!.exUnit.equals(Constant.workout_type_step)) {
                        startExerciseWithStep()
                    } else {
                        startExerciseWithTime()
                    }
                    this@PerformWorkOutActivity.start()
                }

                override fun onTick(millisUntilFinished: Long) {
                    if ((millisUntilFinished / 1000) > 0) {
                        MyApplication.speechText(
                            this@PerformWorkOutActivity,
                            (millisUntilFinished / 1000).toString()
                        )
                        Debug.e("321 Anim", (millisUntilFinished / 1000).toString())
                        binding!!.tvAnimation!!.setText((millisUntilFinished / 1000).toString())
                        binding!!.tvAnimation.visibility = View.VISIBLE
                        binding!!.tvAnimation!!.startAnimation(scaleAnimation)
                    }
                }

            }
            timer!!.start()
        } else {
            start()
            if (currentExe!!.exUnit.equals(Constant.workout_type_step)) {
                startExerciseWithStep()
            } else {
                startExerciseWithTime()
            }
        }


    }

    private fun start() {
        /*val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 1000)*/
        exStartTime = System.currentTimeMillis()
        running = true
    }

    private fun startExerciseWithStep() {
        if (timer != null) {
            timer!!.cancel()
        }
        mySoundUtil.playSound(0)

    }

    private fun startExerciseWithTime() {
        if (timer != null) {
            timer!!.cancel()
        }
        mySoundUtil.playSound(0)

        try {
            countExercise()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val excTime = currentExe!!.exTime!!.toInt()
        val readyToGoText = "Start $excTime seconds ${currentExe!!.exName}"

        MyApplication.speechText(this, readyToGoText)
    }

    private fun countExercise() {

        var timeCountDown = 0

        val exerciseTime = currentExe!!.exTime!!.toInt()
        val halfTime = exerciseTime / 2

        timer =
            object : CountDownTimerWithPause(currentExe!!.exTime!!.toInt() * 1000L, 1000, true) {
                override fun onFinish() {
                    binding!!.tvCompletedSec.text = "0"
                    binding!!.progressBarWorkOut.progress = exerciseTime
                    onWorkoutTimeOver()
                }

                override fun onTick(millisUntilFinished: Long) {
                    timeCountDown++
                    if ((exerciseTime - timeCountDown) >= 0) {
                        binding!!.tvCompletedSec.text = (exerciseTime - timeCountDown).toString()
                        binding!!.progressBarWorkOut.progress = timeCountDown

                        if (timeCountDown == halfTime) {
                            MyApplication.speechText(this@PerformWorkOutActivity, "Half time")
                        } else if ((exerciseTime - timeCountDown) < 4) {
                            MyApplication.speechText(
                                this@PerformWorkOutActivity,
                                (exerciseTime - timeCountDown).toString()
                            )
                        }
                    } else {
                        timer!!.onFinish()
                        timer!!.cancel()
                    }
                }

            }

        Handler().postDelayed(Runnable {
            if (timer != null)
                timer!!.start()
        }, 1000)


    }

    private fun onWorkoutTimeOver() {
        stopTimer()

        DataHelper(this).updateCompleteHomeExByDayExId(currentExe!!.exId!!)
        if (currentPos == exercisesList!!.lastIndex) {
            // Go to Complete Screen
            MyApplication.speechText(this, "Congratulation")
            goToCompleteScreen()
        } else {
            goToRestScreen()
        }
    }

    private fun goToRestScreen() {
        mySoundUtil.playSound(mySoundUtil.SOUND_DING)
        val i = Intent(this, RestActivity::class.java)
        i.putExtra("nextEx", Gson().toJson(exercisesList!!.get(currentPos + 1)))
        i.putExtra("nextPos", currentPos + 2)
        i.putExtra("totalEx", exercisesList!!.size)
        startActivityForResult(i, 7029)
    }

    private fun goToCompleteScreen() {

        AdUtils.initFullAdd(this, object : AdsCallback {
            override fun startNextScreenAfterAd() {
                var i = Intent(this@PerformWorkOutActivity, CompletedActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(workoutPlanData))
                i.putExtra("ExcList", Gson().toJson(exercisesList))
                i.putExtra("duration", totalExTime)
                startActivity(i)
                finish()
            }

        })

    }

    override fun onResume() {
        super.onResume()
        initMusic(false)
        resumeTimer()
    }


    inner class ClickHandler {

        fun onWorkOutInfoClick() {
            //pauseTimer()
            val i = Intent(this@PerformWorkOutActivity, ExerciseVideoActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(currentExe))
            startActivity(i)
        }

        fun onVideoClick() {
            val i = Intent(this@PerformWorkOutActivity, ExerciseVideoActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(currentExe))
            startActivity(i)
        }

        fun onSoundClick() {
            pauseTimer()
            showSoundOptionDialog(this@PerformWorkOutActivity, object : DialogDismissListener {
                override fun onDialogDismiss() {
                    resumeTimer()
                }
            })
        }

        fun onReadyToGoClick() {
            // pauseTimer()
            val i = Intent(this@PerformWorkOutActivity, PauseBeforeStartActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(currentExe))
            i.putExtra("nextPos", currentPos + 1)
            i.putExtra("totalEx", exercisesList!!.size)
            startActivity(i)
        }

        fun onMusicClick() {
            if (binding!!.llMusic.visibility == View.VISIBLE) {
                binding!!.llMusic.visibility = View.INVISIBLE
            } else {
                binding!!.llMusic.visibility = View.VISIBLE
            }
        }

        fun onPauseMusicClick() {
            if (currMusic != null)
                playMusic()
            else {
                val i = Intent(this@PerformWorkOutActivity, MusicListActivity::class.java)
                startActivity(i)
            }
        }


        fun onPrevMusicClick() {
            if (!Utils.isPurchased(this@PerformWorkOutActivity)) {
                val i = Intent(this@PerformWorkOutActivity, MusicListActivity::class.java)
                startActivity(i)
            } else {
                MyApplication.prevMusic()
            }
        }

        fun onNextMusicClick() {
            if (!Utils.isPurchased(this@PerformWorkOutActivity)) {
                val i = Intent(this@PerformWorkOutActivity, MusicListActivity::class.java)
                startActivity(i)
            } else {
                MyApplication.nextMusic()
            }
        }

        fun onSelectMusicClick() {
            pauseTimer()
            val i = Intent(this@PerformWorkOutActivity, MusicListActivity::class.java)
            startActivity(i)
        }

        fun onSkipReadyToGoClick() {
            startPerformExercise(false)
        }

        fun onBackClick() {
            pauseTimer()
            showQuitDialog()
        }

        fun onNextExerciseClick() {
            stopTimer()
            onWorkoutTimeOver()
        }

        fun onPrevExerciseClick() {
            stopTimer()
            if (currentPos >= 1) {
                val i = Intent(this@PerformWorkOutActivity, RestActivity::class.java)
                i.putExtra("nextEx", Gson().toJson(exercisesList!!.get(currentPos - 1)))
                i.putExtra("nextPos", currentPos - 1)
                i.putExtra("totalEx", exercisesList!!.size)

                currentPos -= 2

                startActivityForResult(i, 7029)
            } else {
                totalExTime = 0
                currentPos = 0
                exStartTime = System.currentTimeMillis()
                currentTime = 0
                running = false
                initReadyToGo()
            }
        }

    }

    private fun playMusic() {
        if (currMusic != null) {
            if (MyApplication.musicUtil == null || MyApplication.musicUtil!!.isPlaying.not()) {
                binding!!.imgPlayMusic.visibility = View.GONE
                binding!!.imgPauseMusic.visibility = View.VISIBLE
                binding!!.imgMusic.setImageResource(R.drawable.ic_music)
                MyApplication.playMusic(currMusic!!, this@PerformWorkOutActivity)
                Utils.setPref(this@PerformWorkOutActivity, Constant.PREF_IS_MUSIC_MUTE, false)
            } else {
                binding!!.imgPlayMusic.visibility = View.VISIBLE
                binding!!.imgPauseMusic.visibility = View.GONE
                binding!!.imgMusic.setImageResource(R.drawable.ic_music_off)
                Utils.setPref(this@PerformWorkOutActivity, Constant.PREF_IS_MUSIC_MUTE, true)
                MyApplication.stopMusic()
            }
        }
    }

    fun setPlayPauseView() {
        if (MyApplication.musicUtil == null || MyApplication.musicUtil!!.isPlaying.not()) {
            binding!!.imgPlayMusic.visibility = View.VISIBLE
            binding!!.imgPauseMusic.visibility = View.GONE
            binding!!.imgMusic.setImageResource(R.drawable.ic_music_off)
        } else {
            binding!!.imgPlayMusic.visibility = View.GONE
            binding!!.imgPauseMusic.visibility = View.VISIBLE
            binding!!.imgMusic.setImageResource(R.drawable.ic_music)
        }
    }

    override fun onPause() {
        super.onPause()
        pauseTimer()
    }

    override fun onBackPressed() {
        pauseTimer()
        showQuitDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.stopMusic()
        stopTimer()
    }

    fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
            com.stretching.utils.Debug.e(TAG, "OnTimer cancel")
        }
    }

    fun resumeTimer() {
        if (timer != null && timer!!.isPaused) {
            timer!!.resume()
            com.stretching.utils.Debug.e(TAG, "OnTimer resume")
        }
        if (running.not()) {
            running = true
            exStartTime = System.currentTimeMillis()
            Debug.e("resumeTimer exStartTime", exStartTime.toString())
        }

    }

    fun pauseTimer() {
        if (running) {
            running = false
            currentTime = System.currentTimeMillis() - exStartTime
            totalExTime += currentTime / 1000
            Debug.e("pauseTimer currentTime", currentTime.toString())
            Debug.e("pauseTimer totalExTime", totalExTime.toString())
        }

        if (timer != null && timer!!.isRunning) {
            timer!!.pause()
            com.stretching.utils.Debug.e(TAG, "OnTimer pause")
        }
    }

    lateinit var quiteDialog: Dialog
    fun showQuitDialog() {
        MyApplication.speechText(this, getString(R.string.quite_exercise_msg))
        quiteDialog = Dialog(getActivity())
        quiteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        quiteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var dialogQuiteWorkoutBinding =
            DataBindingUtil.inflate<DialogQuiteWorkoutBinding>(
                getLayoutInflater(),
                R.layout.dialog_quite_workout, null, false
            )

        quiteDialog.setContentView(dialogQuiteWorkoutBinding.root)

        dialogQuiteWorkoutBinding!!.imgClose.setOnClickListener {
            resumeTimer()
            quiteDialog.dismiss()
        }

        dialogQuiteWorkoutBinding!!.btnContinue.setOnClickListener {
            resumeTimer()
            quiteDialog.dismiss()
        }

        dialogQuiteWorkoutBinding!!.btnQuit.setOnClickListener {
            quiteDialog.dismiss()
            saveData()
        }

        dialogQuiteWorkoutBinding!!.tvComeback.setOnClickListener {
            //Utils.setComBackIn30Min(this)
            quiteDialog.dismiss()
            finish()
        }

        quiteDialog.show()
    }


    private fun saveData() {

        try {
            val calValue = Constant.SEC_DURATION_CAL * totalExTime

            dbHelper.addHistory(
                exercisesList!![0].planId!!,
                dbHelper.getPlanNameByPlanId(exercisesList!![0].planId!!),
                Utils.parseTime(Date().time, Constant.DATE_TIME_24_FORMAT),
                totalExTime.toString(),
                calValue.toString(),
                exercisesList!!.size.toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString(),
                Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toString(),
                "0",
                dbHelper.getPlanDayNameByDayId(exercisesList!![0].dayId!!),
                exercisesList!![0].dayId!!
            )

            //LocalDB.setLastUnCompletedExPos(this, arrDayExTableClass[0].planId.toInt(), arrDayExTableClass[0].dayId, viewPagerWorkout.currentItem)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 7029 && resultCode == Activity.RESULT_OK) {
            if (data!!.hasExtra("restTime")) {
                totalExTime += data.getLongExtra("restTime", 0)
                Debug.e("onActivityResult totalExTime", totalExTime.toString())
            }

            currentPos++
            binding!!.progressBarTop.progress = currentPos
            currentExe = exercisesList!!.get(currentPos)
            loadWorkoutImage()
            if (data!!.hasExtra("isRestSkip")) {
                startPerformExercise(data!!.getBooleanExtra("isRestSkip", false))
            } else {
                startPerformExercise(false)
            }

        }
    }
}
