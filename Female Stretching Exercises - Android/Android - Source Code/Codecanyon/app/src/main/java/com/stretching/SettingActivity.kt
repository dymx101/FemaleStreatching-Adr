package com.stretching

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.stretching.databinding.*
import com.stretching.interfaces.CallbackListener
import com.stretching.interfaces.DialogDismissListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils


class SettingActivity : BaseActivity(), CallbackListener {

    var binding: ActivitySettingBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

//        AdUtils.loadBannerAd(binding!!.adView,this)
//        AdUtils.loadBannerGoogleAd(this,binding!!.llAdView,Constant.BANNER_TYPE)

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

        initDrawerMenu(true)
        init()
    }

    private fun initIntentParam() {
        try {


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.isMenuShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.menu_setting)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        binding!!.SwitchKeepTheScreenOn.setOnCheckedChangeListener { buttonView, isChecked ->
            Utils.setPref(this,Constant.PREF_IS_KEEP_SCREEN_ON,isChecked)
        }

        fillData()

    }


    private fun fillData() {
        binding!!.tvRestTime.text = Utils.getPref(this,Constant.PREF_REST_TIME,Constant.DEFAULT_REST_TIME).toString().plus(" secs")
        binding!!.tvCountDownTime.text = Utils.getPref(this,Constant.PREF_READY_TO_GO_TIME,Constant.DEFAULT_READY_TO_GO_TIME).toString().plus(" secs")

        binding!!.SwitchKeepTheScreenOn.isChecked =  Utils.getPref(this,Constant.PREF_IS_KEEP_SCREEN_ON,false)

        if(Utils.isPurchased(this))
        {
            binding!!.llGoPremium.visibility = View.GONE
        }else
            binding!!.llGoPremium.visibility = View.VISIBLE
    }

    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
        changeSelection(6)
    }


    inner class ClickHandler {

        fun onSetDurationClick() {
            showTrainingRestDialog()
        }

        fun onCountDownClick() {
            showSetCountDownTimeDialog()
        }

        fun onSoundOptionClick() {
            showSoundOptionDialog(this@SettingActivity,object :DialogDismissListener{
                override fun onDialogDismiss() {

                }

            })
        }

        fun onTestVoiceClick(){
            MyApplication.speechText(this@SettingActivity, getString(R.string.did_you_hear_test_voice))
            Thread.sleep(1000)
            showVoiceConfirmationDialog()
        }

        fun onSelectTTsEngineClick(){
            showSelectTTSEngineDialog()
        }

        fun onDownloadTTsEngineClick(){
            Utils.DownloadTTSEngine(this@SettingActivity)
        }

        fun onDeviceTTSSettingClick(){
            val intent = Intent("com.android.settings.TTS_SETTINGS")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        fun onRemindMeTomorrow(){
            val intent = Intent(this@SettingActivity,ReminderActivity::class.java)
            intent.putExtra("from",Constant.FROM_SETTING)
            startActivity(intent)
        }

        fun onHealthDataClick(){
            val intent = Intent(this@SettingActivity,HealthDataActivity::class.java)
            startActivity(intent)
        }

        fun onMetricImperialsClick(){
            val intent = Intent(this@SettingActivity,MetricImperialUnitsActivity::class.java)
            startActivity(intent)
        }

        fun onGoPremiumClick(){
            val intent = Intent(this@SettingActivity,AccessAllFeaturesActivity::class.java)
            startActivity(intent)
        }

        fun onRatUsClick(){
            Utils.rateUs(this@SettingActivity)
        }

        fun onFeedBackClick(){
            Utils.contactUs(this@SettingActivity)
        }

        fun onPrivacyPolicyClick()
        {
            Utils.openUrl(this@SettingActivity, getString(R.string.privacy_policy_link))
        }

        fun onShareWithFriendClick()
        {
            val link = "https://play.google.com/store/apps/details?id=$packageName"
            val strSubject = ""
            val strText = "I'm stretching with ${getString(R.string.app_name)}, it's so useful! All kinds of stretching and warm-up routines here for workout, running, pain relief, etc. It must have one you need, try it for free!" +
                        "\n\n" +
                        "Download the app:$link"

            Utils.shareStringLink(this@SettingActivity, strSubject, strText)
        }
    }

    private fun showVoiceConfirmationDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        var dialog :Dialog
        builder.setMessage(R.string.did_you_hear_test_voice)
        builder.setPositiveButton(R.string.yes) { dialog, which ->
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no, { dialog, which ->
            dialog.dismiss()
            showTestVoiceFailDialog()
        })
        dialog =  builder.create()
        dialog.show()
    }

    private fun showTestVoiceFailDialog() {

        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        var dialog :Dialog? = null
        val v: View = (this).getLayoutInflater()
            .inflate(R.layout.dialog_test_voice_fail, null)
        val dialogBinding: DialogTestVoiceFailBinding? = DataBindingUtil.bind(v)
        builder.setView(v)

        dialogBinding!!.tvDownloadTTSEngine.setOnClickListener {
            Utils.DownloadTTSEngine(this)
            dialog!!.dismiss()
        }

        dialogBinding.tvSelectTTSEngine.setOnClickListener {
            showSelectTTSEngineDialog()
            dialog!!.dismiss()
        }

        builder.setView(v)
        dialog =  builder.create()
        dialog.show()
    }

    private fun showSelectTTSEngineDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        var dialog :Dialog? = null
        val v: View = (this).getLayoutInflater()
            .inflate(R.layout.dialog_select_tts_engine, null)
        builder.setView(v)
        builder.setTitle(getString(R.string.please_choose_guide_voice_engine))
        val dialogBinding: DialogSelectTtsEngineBinding? = DataBindingUtil.bind(v)

        dialogBinding!!.llContainer.setOnClickListener {
            dialog!!.dismiss()
        }

        builder.setView(v)
        dialog =  builder.create()
        dialog.show()

    }

    private fun showTrainingRestDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setTitle("Set Duration (5 ~ 180 secs)")
        val v: View = (this).getLayoutInflater()
            .inflate(R.layout.dialog_set_duration, null)
        val dialogBinding: DialogSetDurationBinding? = DataBindingUtil.bind(v)
        builder.setView(v)

        dialogBinding!!.tvSeconds.text = Utils.getPref(this,Constant.PREF_REST_TIME,Constant.DEFAULT_REST_TIME).toString()

        dialogBinding!!.imgNext.setOnClickListener {
            var sec = dialogBinding!!.tvSeconds.text.toString().toInt()
            if (sec < 180)
                dialogBinding!!.tvSeconds.text = (sec+1).toString()
        }

        dialogBinding!!.imgPrev.setOnClickListener {
            var sec = dialogBinding!!.tvSeconds.text.toString().toInt()
            if (sec > 5)
                dialogBinding.tvSeconds.text = (sec-1).toString()
        }

        builder.setPositiveButton(R.string.save) { dialog, which ->
            Utils.setPref(this,Constant.PREF_REST_TIME,dialogBinding.tvSeconds.text.toString().toLong())
            fillData()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> dialog.dismiss() })
        builder.create().show()
    }


    private fun showSetCountDownTimeDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setTitle("Set Duration (10 ~ 15 secs)")
        val v: View = (this).getLayoutInflater()
            .inflate(R.layout.dialog_set_duration, null)
        val dialogBinding: DialogSetDurationBinding? = DataBindingUtil.bind(v)
        builder.setView(v)

        dialogBinding!!.tvSeconds.text = Utils.getPref(this,Constant.PREF_READY_TO_GO_TIME,Constant.DEFAULT_READY_TO_GO_TIME).toString()

        dialogBinding.imgNext.setOnClickListener {
            var sec = dialogBinding.tvSeconds.text.toString().toInt()
            if (sec < 15)
                dialogBinding.tvSeconds.text = (sec+1).toString()
        }

        dialogBinding.imgPrev.setOnClickListener {
            var sec = dialogBinding.tvSeconds.text.toString().toInt()
            if (sec > 10)
                dialogBinding.tvSeconds.text = (sec-1).toString()
        }

        builder.setPositiveButton(R.string.save) { dialog, which ->
            Utils.setPref(this,Constant.PREF_READY_TO_GO_TIME,dialogBinding.tvSeconds.text.toString().toLong())
            fillData()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> dialog.dismiss() })
        builder.create().show()
    }

    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
                finish()
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
