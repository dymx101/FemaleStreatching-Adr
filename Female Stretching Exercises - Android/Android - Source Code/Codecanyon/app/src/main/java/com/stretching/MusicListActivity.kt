package com.stretching

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stretching.adapter.MusicListAdapter
import com.stretching.databinding.ActivityMusicListBinding
import com.stretching.interfaces.CallbackListener
import com.stretching.objects.Music
import com.stretching.utils.Constant
import com.stretching.utils.Utils


class MusicListActivity : BaseActivity(), CallbackListener {

    var binding: ActivityMusicListBinding? = null
    var adapter: MusicListAdapter? = null
    var musicList: ArrayList<Music>? = null
    var currMusic: Music? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_list)

        initIntentParam()
        init()
    }

    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
    }
    private fun initIntentParam() {
        try {
            /*if (intent.extras != null) {
                if (intent.extras!!.containsKey("workoutPlanData")) {
                    val str = intent.getStringExtra("workoutPlanData")
                    workoutPlanData = Gson().fromJson(str, object :
                        TypeToken<HomeTrainingPlans.Plan>() {}.type)!!
                }
            }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()
        binding!!.rvMusic.layoutManager = LinearLayoutManager(this)
        adapter = MusicListAdapter(this)
        binding!!.rvMusic.adapter = adapter
        musicList = dbHelper.getMusicList()
        adapter!!.addAll(musicList!!)

        adapter!!.setEventListener(object : MusicListAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = adapter!!.getItem(position)

                if (item.isPro == true && !Utils.isPurchased(this@MusicListActivity)) {
                    val i = Intent(this@MusicListActivity, AccessAllFeaturesActivity::class.java)
                    startActivity(i)
                } else {
                    currMusic = item
                    binding!!.tvMusicName.text = item.name
                    binding!!.imgPlayMusic.setImageResource(R.drawable.ic_play_bar_btn_pause)
                    MyApplication.playMusic(item, this@MusicListActivity)
                    Utils.setPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_SELECTED, true)
                    Utils.setPref(this@MusicListActivity, Constant.PREF_MUSIC, Gson().toJson(item))
                }
            }

        })

        if (Utils.getPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_SELECTED, false)) {
            val str = Utils.getPref(this@MusicListActivity, Constant.PREF_MUSIC, "")
            if (str.isNullOrEmpty().not()) {
                currMusic = Gson().fromJson<Music>(str, object : TypeToken<Music>() {}.type)
                binding!!.tvMusicName.text = currMusic!!.name
                if (currMusic!!.isPro == true && Utils.isPurchased(this).not()) {
                    setDefaultMusic()
                }
            } else {
                setDefaultMusic()
            }
        } else {
            setDefaultMusic()
        }

        if (MyApplication.musicUtil != null && MyApplication.musicUtil!!.isPlaying) {
            binding!!.imgPlayMusic.setImageResource(R.drawable.ic_play_bar_btn_pause)
        }

        val isShuffle = Utils.getPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_SHUFFLE, false)
        val isRepeat = Utils.getPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_REPEAT, false)

        if (isRepeat) {
            binding!!.imgRepeat.setColorFilter(
                    ContextCompat.getColor(this, R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            binding!!.imgRepeat.setColorFilter(
                    ContextCompat.getColor(this, R.color.gray_light_),
                    android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        if (isShuffle) {
            binding!!.imgSuffle.setColorFilter(
                    ContextCompat.getColor(this, R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            binding!!.imgSuffle.setColorFilter(
                    ContextCompat.getColor(this, R.color.gray_light_),
                    android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    fun setDefaultMusic() {
        for (item in musicList!!) {
            if (item.isPro == true) {
                if (Utils.isPurchased(this)) {
                    currMusic = item
                    binding!!.tvMusicName.text = item.name
                    return
                }
            } else {
                currMusic = item
                binding!!.tvMusicName.text = item.name
                return
            }
        }
    }

    inner class ClickHandler {

        fun onCloseClick() {
            finish()
        }

        fun onPauseClick() {
            if (MyApplication.musicUtil == null || MyApplication.musicUtil!!.isPlaying.not()) {
                binding!!.imgPlayMusic.setImageResource(R.drawable.ic_play_bar_btn_pause)
                MyApplication.playMusic(currMusic!!, this@MusicListActivity)
                Utils.setPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_MUTE, false)
                Utils.setPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_SELECTED, true)
                Utils.setPref(this@MusicListActivity, Constant.PREF_MUSIC, Gson().toJson(currMusic))
            } else {
                binding!!.imgPlayMusic.setImageResource(R.drawable.ic_music_play)
                Utils.setPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_MUTE, true)
                MyApplication.stopMusic()
            }
        }

        fun onSuffleClick() {
            if (!Utils.isPurchased(this@MusicListActivity)) {
                val i = Intent(this@MusicListActivity, AccessAllFeaturesActivity::class.java)
                startActivity(i)
            } else {
                val isShuffle =
                        Utils.getPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_SHUFFLE, false)
                if (isShuffle) {
                    Utils.setPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_SHUFFLE, false)
                    binding!!.imgSuffle.setColorFilter(
                            ContextCompat.getColor(
                                    this@MusicListActivity,
                                    R.color.gray_light_
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                } else {
                    binding!!.imgSuffle.setColorFilter(
                            ContextCompat.getColor(
                                    this@MusicListActivity,
                                    R.color.white
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    Utils.setPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_SHUFFLE, true)
                }
            }
        }

        fun onRepeatClick() {
            if (!Utils.isPurchased(this@MusicListActivity)) {
                val i = Intent(this@MusicListActivity, AccessAllFeaturesActivity::class.java)
                startActivity(i)
            } else {
                val isRepeat =
                        Utils.getPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_REPEAT, false)
                if (isRepeat) {
                    Utils.setPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_REPEAT, false)
                    binding!!.imgRepeat.setColorFilter(
                            ContextCompat.getColor(
                                    this@MusicListActivity,
                                    R.color.gray_light_
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                } else {
                    binding!!.imgRepeat.setColorFilter(
                            ContextCompat.getColor(
                                    this@MusicListActivity,
                                    R.color.white
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    Utils.setPref(this@MusicListActivity, Constant.PREF_IS_MUSIC_REPEAT, true)
                }
            }
        }

        fun onNextClick() {
            if (!Utils.isPurchased(this@MusicListActivity)) {
                val i = Intent(this@MusicListActivity, AccessAllFeaturesActivity::class.java)
                startActivity(i)
            } else {
                for (i in musicList!!.indices) {
                    Log.e("TAG", "onNextClick::::: " + musicList!![i].name + "  " + musicList!![i].fileName + "  " + musicList!!.size)
                }
                MyApplication.nextMusic()
            }
        }

        fun onPrevClick() {
            if (!Utils.isPurchased(this@MusicListActivity)) {
                val i = Intent(this@MusicListActivity, AccessAllFeaturesActivity::class.java)
                startActivity(i)
            } else {
                for (i in musicList!!.indices) {
                    Log.e("TAG", "onPrevClick::::: " + musicList!![i].name + "  " + musicList!![i].fileName + "  " + musicList!!.size)
                }
                MyApplication.prevMusic()
            }
        }
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

    }

}
