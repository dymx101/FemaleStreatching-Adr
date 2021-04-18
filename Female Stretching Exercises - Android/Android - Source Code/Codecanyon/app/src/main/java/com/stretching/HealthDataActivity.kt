package com.stretching

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.stretching.databinding.ActivityHealthDataBinding
import com.stretching.interfaces.CallbackListener
import com.stretching.interfaces.DialogDismissListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils


class HealthDataActivity : BaseActivity(), CallbackListener {

    var binding: ActivityHealthDataBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_health_data)

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
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.isBackShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.health_data)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        binding!!.tvGender.text = Utils.getPref(this, Constant.PREF_GENDER, "")
        binding!!.tvDate.text = Utils.getPref(this, Constant.PREF_DOB, "")
    }


    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
    }


    inner class ClickHandler {

        fun onGenderClick() {
            showGenderDialog()
        }

        fun onYearOfBirthClick() {
            showDOBDialog()
        }
    }

    private fun showDOBDialog() {
        showYearOfBirthDialog(this,object : DialogDismissListener {
            override fun onDialogDismiss() {
                binding!!.tvDate.text = Utils.getPref(this@HealthDataActivity, Constant.PREF_DOB, "")
            }
        })
    }

    private fun showGenderDialog() {

        val genderList = arrayOf<CharSequence>("Male", "Female")
        var checkedItem = 0
        if (Utils.getPref(this, Constant.PREF_GENDER, "").equals(Constant.FEMALE)) {
            checkedItem = 1
        }

        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setSingleChoiceItems(
            genderList,
            checkedItem,
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (which == 0) {
                        Utils.setPref(this@HealthDataActivity, Constant.PREF_GENDER, Constant.MALE)
                    } else {
                        Utils.setPref(
                            this@HealthDataActivity,
                            Constant.PREF_GENDER,
                            Constant.FEMALE
                        )
                    }
                    binding!!.tvGender.text = Utils.getPref(this@HealthDataActivity, Constant.PREF_GENDER, "")
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                }

            })

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
