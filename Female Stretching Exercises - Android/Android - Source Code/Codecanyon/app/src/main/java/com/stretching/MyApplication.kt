package com.stretching

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.speech.tts.TextToSpeech
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.stretching.objects.Music
import com.stretching.utils.Constant
import com.stretching.utils.Debug
import com.stretching.utils.MusicUtil
import com.stretching.utils.Utils
import java.util.*


class MyApplication : Application() {

    lateinit var commonReciever: IntReceiver
    internal var handler = Handler()


    companion object {
        var textToSpeech: TextToSpeech? = null
        var musicUtil: MusicUtil? = null

        fun speechText(context: Context, strSpeechText: String) {
            if (checkVoiceOnOrOff(context)) {
                if (textToSpeech != null) {
                    textToSpeech!!.setSpeechRate(0.9f)
                    textToSpeech!!.speak(strSpeechText, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }

        fun checkVoiceOnOrOff(context: Context): Boolean {
            return Utils.getPref(context, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true)
        }

        fun playMusic(item: Music, context: Context) {
            if (musicUtil == null) {
                musicUtil = MusicUtil(context)
            }
            musicUtil!!.playMusic(item)
        }

        fun stopMusic() {
            if (musicUtil != null) {
                musicUtil!!.stopMusic()
                musicUtil = null
            }
        }

        fun nextMusic(){
            if (musicUtil != null) {
                musicUtil!!.nextMusic()
            }
        }

        fun prevMusic(){
            if (musicUtil != null) {
                musicUtil!!.prevMusic()
            }
        }

        fun pauseMusic(){
            if (musicUtil != null) {
                musicUtil!!.pauseMusic()
            }
        }

        fun resumeMusic(){
            if (musicUtil != null) {
                musicUtil!!.resumeMusic()
            }
        }

    }

    override fun onCreate() {
        super.onCreate()

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech!!.language = Locale.ENGLISH
//                if (boolSound) {
//                    textToSpeech!!.setSpeechRate(0.9f)
//                    textToSpeech!!.speak("Welcome to 30 day workout app", TextToSpeech.QUEUE_FLUSH, null)
//                }
            }
        })

    }


    class InternetConnectionReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (Utils.isInternetConnected(context) && Utils.isUserLoggedIn(context!!)) {
                Debug.e("InternetConnectionReceiver", "internet Connected")

                LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(Intent(Constant.CONNECTIVITY_CHANGE))

            }

        }

    }

    inner class IntReceiver(val myApplication: MyApplication) : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            if (Utils.isInternetConnected(context) && Utils.isUserLoggedIn(context!!)) {
                Debug.e("IntReceiver", "internet Connected")

            }

        }

    }


}