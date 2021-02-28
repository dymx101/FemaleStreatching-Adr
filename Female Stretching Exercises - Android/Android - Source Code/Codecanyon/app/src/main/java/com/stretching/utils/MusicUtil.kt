package com.stretching.utils

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.stretching.objects.Music
import com.utillity.db.DataHelper
import kotlin.random.Random

class MusicUtil(var context: Context) {

    var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
    var length = 0
    var musicList = arrayListOf<Music>()
    var pos = 0

    init {
        musicList = DataHelper(context).getMusicList()
    }

    fun playMusic(item: Music) {
        if (item.isPro ?: false && Utils.isPurchased(context).not())
            return

        if (item.isFromRaw ?: false) {
            try {
                val resID: Int = context.resources.getIdentifier(item.fileName, "raw", context.getPackageName())
                stopMusic()
                mediaPlayer = MediaPlayer.create(context, resID)
                mediaPlayer!!.setOnCompletionListener { mp ->
                    isPlaying = false
                    afterCompletion()
                }
                isPlaying = true
                mediaPlayer!!.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Utils.setPref(context, Constant.PREF_MUSIC, Gson().toJson(item))

        for (i in musicList.indices) {
            if (musicList[i].id.equals(item.id)) {
                pos = i
            }
        }
    }


    fun afterCompletion() {

        if (Utils.isPurchased(context)) {
            val isShuffle = Utils.getPref(context, Constant.PREF_IS_MUSIC_SHUFFLE, false)
            val isRepeat = Utils.getPref(context, Constant.PREF_IS_MUSIC_REPEAT, false)

            if(isRepeat)
            {
                playMusic(musicList[pos])
            }else if (isShuffle) {
                val min = 0
                val max = musicList.size - 1
                val random: Int = Random.nextInt(max - min + 1) + min
                when {
                    random != pos -> playMusic(musicList[random])
                    pos < musicList.lastIndex -> nextMusic()
                    else -> playMusic(musicList[0])
                }
            } else {
                nextMusic()
            }
        }

    }

    fun stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            isPlaying = false
            mediaPlayer = null
        }
    }

    fun pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
            length = mediaPlayer!!.getCurrentPosition()
            isPlaying = false
        }
    }

    fun resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(length);
            mediaPlayer!!.start();
            isPlaying = true
        }
    }

    fun nextMusic() {
        if (pos < musicList.lastIndex) {
            playMusic(musicList[pos+1])
        }
    }

    fun prevMusic() {
        if (pos > 0) {
            playMusic(musicList[pos-1])
        }
    }
}