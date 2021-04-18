package com.stretching

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.*
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.admision.apputils.permission.PermissionHelper
import com.bumptech.glide.Glide
import com.common.view.CEditTextView
import com.common.view.CTextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.stretching.adapter.SideMenuAdapter
import com.stretching.databinding.*
import com.stretching.interfaces.AdsCallback
import com.stretching.interfaces.CallbackListener
import com.stretching.interfaces.DateEventListener
import com.stretching.interfaces.DialogDismissListener
import com.stretching.objects.HomeExTableClass
import com.stretching.objects.SideMenuItem
import com.stretching.utils.*
import com.stretching.utils.permission.ManagePermissionsImp
import com.stretching.db.DataHelper
import com.stretching.utils.Utils.isOnline
import kotlinx.android.synthetic.main.dialog_logout.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.side_menu.view.*
import kotlinx.android.synthetic.main.topbar.*
import java.util.*
import kotlin.math.roundToInt


open class BaseActivity() : AppCompatActivity() {

    val TAG_BASE = BaseActivity::class.java.name + Constant.ARROW
    internal lateinit var commonReciever: MyEventServiceReciever
    var topBarBinding: TopbarBinding? = null
    lateinit var dbHelper: DataHelper
    protected var mWakeLock: PowerManager.WakeLock? = null
    private var mRewardedVideoAd: RewardedVideoAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constant.FINISH_ACTIVITY)
        commonReciever = MyEventServiceReciever()
        LocalBroadcastManager.getInstance(this).registerReceiver(
                commonReciever, intentFilter
        )
        val isKeepOn = Utils.getPref(this, Constant.PREF_IS_KEEP_SCREEN_ON, false)
        if (isKeepOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        dbHelper = DataHelper(this)
        MobileAds.initialize(this) {}

    }

    fun initTopBar(topbar: TopbarBinding) {
        topBarBinding = topbar
    }


    open fun getActivity(): BaseActivity {
        return this
    }


    fun initBack() {
        imgBack_!!.visibility = View.VISIBLE
        imgBack_!!.setOnClickListener {
            finishActivity()
        }
    }


    internal lateinit var toast: Toast

    fun showToast(text: String, duration: Int) {
        runOnUiThread {
            toast.setText(text)
            toast.duration = duration
            toast.show()
        }
    }

    fun showToast(text: String) {
        runOnUiThread {
            toast.setText(text)
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }
    }

    fun showToast(strResId: Int) {
        runOnUiThread {
            toast.setText(getString(strResId))
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }
    }

    fun setScreenTitle(text: String) {
        tvTitleText_!!.setText(text)
    }


    var result: Drawer? = null
    lateinit var customSideMenu: ViewGroup
    lateinit var navHeader: View
    var menuAdapter: SideMenuAdapter? = null

    fun initDrawerMenu(b: Boolean) {

        if (b) {
            customSideMenu = layoutInflater.inflate(R.layout.side_menu, null, false) as ViewGroup
            navHeader = customSideMenu.navHeader
            var mRecyclerView = customSideMenu.mRecyclerView
            var exitTxt = customSideMenu.llExit
            navHeader.llUserDetail.setOnClickListener {

//                val intent = Intent(getActivity(), ProfileActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//                startActivityForResult(intent,1564)
//                finishActivity()
//                hideMenu(true)

            }


            exitTxt.setOnClickListener {
                openExitConfirm()
            }


            var layoutManager = LinearLayoutManager(this)
            mRecyclerView.layoutManager = layoutManager

            menuAdapter = SideMenuAdapter(this)
            mRecyclerView.adapter = menuAdapter

            var data = ArrayList<SideMenuItem>()

            data.add(
                    SideMenuItem(
                            "1",
                            R.drawable.ic_menu_training_plan,
                            getString(R.string.menu_training_plans)
                    )
            )
            data.add(
                    SideMenuItem(
                            "2",
                            R.drawable.ic_menu_library,
                            getString(R.string.menu_discover)
                    )
            )
            data.add(
                    SideMenuItem(
                            "3",
                            R.drawable.ic_menu_my_training,
                            getString(R.string.menu_my_training)
                    )
            )
            data.add(SideMenuItem("4", R.drawable.ic_menu_report, getString(R.string.menu_report)))
            data.add(
                    SideMenuItem(
                            "5",
                            R.drawable.ic_menu_remind,
                            getString(R.string.menu_reminder)
                    )
            )
            // data.add(SideMenuItem("6", R.drawable.ic_language, getString(R.string.menu_language)))
            data.add(
                    SideMenuItem(
                            "7",
                            R.drawable.ic_menu_setting,
                            getString(R.string.menu_setting)
                    )
            )


            menuAdapter!!.addAll(data)

            menuAdapter!!.setEventListener(object : SideMenuAdapter.EventListener {
                override fun onItemClick(pos: Int) {

                    var id = menuAdapter!!.getItem(pos).ID

                    if (id.contains("1")) {
                        menuAdapter!!.changeSelection(pos)
                        if (getActivity() is HomeActivity) {
                            hideMenu(true)
                        } else {
                            val intent = Intent(getActivity(), HomeActivity::class.java)
                            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finishActivity()
                            hideMenu(true)
//                            finish()
                        }
                    }
                    if (id.contains("2")) {
                        menuAdapter!!.changeSelection(pos)
                        if (getActivity() is DiscoverActivity) {
                            hideMenu(true)
                        } else {
                            val intent = Intent(getActivity(), DiscoverActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finishActivity()
                            hideMenu(true)
//                            finish()
                        }

                    }
                    if (id.contains("3")) {
                        menuAdapter!!.changeSelection(pos)
                        if (getActivity() is MyTrainingActivity) {
                            hideMenu(true)
                        } else {
                            val intent = Intent(getActivity(), MyTrainingActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finishActivity()
                            hideMenu(true)
//                            finish()
                        }

                    }
                    if (id.contains("4")) {
                        menuAdapter!!.changeSelection(pos)
                        if (getActivity() is ReportActivity) {
                            hideMenu(true)
                        } else {
                            val intent = Intent(getActivity(), ReportActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finishActivity()
                            hideMenu(true)
//                            finish()
                        }

                    }
                    if (id.contains("5")) {
                        menuAdapter!!.changeSelection(pos)
                        if (getActivity() is ReminderActivity) {
                            hideMenu(true)
                        } else {
                            val intent = Intent(getActivity(), ReminderActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finishActivity()
                            hideMenu(true)
//                            finish()
                        }
                    }
                    if (id.contains("7")) {
                        menuAdapter!!.changeSelection(pos)
                        if (getActivity() is SettingActivity) {
                            hideMenu(true)
                        } else {
                            val intent = Intent(getActivity(), SettingActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finishActivity()
                            hideMenu(true)
//                            finish()
                        }
                    }
                }

            })

            result = DrawerBuilder()
                    .withActivity(this)
                    .withCloseOnClick(true)
                    .withSelectedItemByPosition(-1)
                    .withCustomView(customSideMenu)
                    .withDrawerWidthDp(280)
                    .build()


            imgMenu.visibility = View.VISIBLE
            imgMenu.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (result!!.isDrawerOpen) {
                        result!!.closeDrawer()
                    } else {
                        result!!.openDrawer()
                    }
                }
            })
        } else {
            imgMenu.visibility = View.GONE
        }
    }

    private lateinit var alertDialogExit: AlertDialog.Builder

    private fun openExitConfirm() {
        result!!.closeDrawer()
        alertDialogExit = AlertDialog.Builder(this)
        alertDialogExit.setTitle(resources.getString(R.string.app_name))
        alertDialogExit.setMessage("Are you sure to exit ?")
        alertDialogExit.setCancelable(false)
        alertDialogExit.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogExit.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
//            finish()
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        }
        val alert = alertDialogExit.create()
        alert.show()
        val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        nbutton.setTextColor(Color.BLACK)
        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        pbutton.setTextColor(Color.BLACK)
    }


    open fun changeSelection(index: Int) {
        if (result != null && menuAdapter != null) {
            menuAdapter!!.changeSelection(index)
        }
    }

    lateinit var dialog: Dialog
    fun confirmLogout() {
        dialog = Dialog(getActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.btnLogout.setOnClickListener {
            showToast("Logged Out", Toast.LENGTH_SHORT)
            logout()
            dialog.dismiss()

        }
        dialog.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        hideMenu(true)
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show()

    }

    fun logout() {

        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(Intent(Constant.FINISH_ACTIVITY))

    }


    fun hideMenu(b: Boolean) {
        try {
            if (b)
                result!!.closeDrawer()
        } catch (e: Exception) {
        }
    }

    fun finishActivity() {
        if (getActivity() is HomeActivity) {
        } else {
            getActivity().finish()
        }
    }


    interface TokenInterface {
        fun onSuccess(token: String?)
        fun onFailure()
    }

    internal var ad: RetrofitProgressDialog? = null

    fun showDialog(msg: String) {
        try {
            if (ad != null && ad!!.isShowing) {
                return
            }
            if (ad == null) {
                ad = RetrofitProgressDialog.getInstant(getActivity())
            }
            ad!!.show(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissDialog() {
        try {
            if (ad != null) {
                ad!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal inner class MyEventServiceReciever : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.action!!.equals(Constant.FINISH_ACTIVITY, ignoreCase = true)) {
                    finishActivity()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startSyncing() {
        loadAnimations()
    }

    var rotation: Animation? = null

    private fun loadAnimations() {
        AnimationUtils()
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate)

        try {
            if (topBarBinding != null) {
//                topBarBinding!!.imgSync.visibility = View.VISIBLE
//                topBarBinding!!.imgSync.startAnimation(rotation)
                topBarBinding!!.isInterNetAvailable = true
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun stopSyncing() {
        try {
            if (rotation != null) {
                rotation!!.cancel()
                if (topBarBinding != null) {
//                    topBarBinding!!.imgSync.visibility = View.GONE
//                    topBarBinding!!.imgSync.setImageResource(0)
                    topBarBinding!!.isInterNetAvailableShow = false
                    topBarBinding!!.isInterNetAvailable = true
                    topBarBinding!!.isInterNetAvailableShow = true
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }


    fun fillDrawer() {
        try {
            /*val userModel = Utils.getUserData(getActivity())
            if (userModel != null) {
                navHeader.tvUserSortName.setText(userModel.name)
                navHeader.tvUserMobile.setText(userModel.mobno)
                if (!userModel!!.pic.isNullOrBlank()) {
//                    run {
                    Glide.with(getActivity())
                        .load(userModel.pic)
                        .into(navHeader.imgUserProfile)
//                            .onLoadFailed(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_avatar))
//                    }
                } else {
                    navHeader.imgUserProfile.setImageResource(R.mipmap.ic_avatar)
                }

            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveUserData() {
        /*Utils.setPref(getActivity(), Constant.LOGIN_INFO, Gson().toJson(userData))
        Utils.setPref(getActivity(), RequestParamsUtils.UID, userData.userId.toString())
        Utils.setPref(
            getActivity(),
            RequestParamsUtils.FACILITYCODE,
            userData.facilityName.toString()
        )*/

    }


    override fun onResume() {

        /* if (dialogPermission == null)
             checkPermissions(getActivity())
         else if (dialogPermission != null && dialogPermission!!.isShowing.not())
             checkPermissions(getActivity())*/

        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
//        (application as MyApplication).stopTimer()
    }

    fun checkPermissions(activity: AppCompatActivity) {
        // Initialize a list of required permissions to request runtime
        val list = listOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                "com.android.vending.BILLING")

        PermissionHelper.checkPermissions(
                activity,
                list,
                object : ManagePermissionsImp.IPermission {
                    override fun onPermissionDenied() {
//                        showAlert()
                        if (activity is SplashScreenActivity) {
                            activity.startapp(1000)
                        }
                    }

                    override fun onPermissionGranted() {
                        if (activity is SplashScreenActivity) {
                            activity.startapp(1000)
                            Log.e("TAG", "onPermissionGranted:::: " )
                        }
                    }
                })
    }

    var dialogPermission: AlertDialog? = null
    private fun showAlert() {
        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle(getString(R.string.need_permission_title))
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.err_need_permission_msg))
        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
            startActivity(
                    Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
            )
            finish()
        }
        builder.setNeutralButton(R.string.btn_cancel, { dialog, which -> finish() })
        dialogPermission = builder.create()
        dialogPermission!!.show()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {

        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun isInternetAvailable(context: Context, callbackListener: CallbackListener) {
        if (Utils.isInternetConnected(context)) {
            callbackListener.onSuccess()
            return
        }
        AlertDialogHelper.showNoInternetDialog(context, callbackListener)
    }

    fun isInternetAvailable_(context: Context, callbackListener: CallbackListener) {
        if (Utils.isInternetConnected(context)) {
            callbackListener.onSuccess()
            return
        }
    }

    fun showHttpError() {
        AlertDialogHelper.showHttpExceptionDialog(getActivity())
    }


    private var logoutPopupWindow: PopupWindow? = null
    private var popupLogoutBinding: PopupLogoutBinding? = null


    fun showLogoutPopup(view: View) {
        if (logoutPopupWindow == null) {
            popupLogoutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(getActivity()),
                    R.layout.popup_logout,
                    null,
                    false
            )
            logoutPopupWindow = PopupWindow(
                    popupLogoutBinding!!.root,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            logoutPopupWindow!!.isOutsideTouchable = true
            logoutPopupWindow!!.isFocusable = true

            popupLogoutBinding!!.llContainer.setOnClickListener {

                confirmLogout()
                logoutPopupWindow!!.dismiss()

            }
        }

        logoutPopupWindow!!.showAsDropDown(view)

//        binder.content.searchLayout.viewClickHandler = ViewClickHandler()
    }

    lateinit var setWorkOutTimeDialog: Dialog
    fun showChangeTimeDialog(
            item: HomeExTableClass,
            listener: DialogInterface
    ) {
        var second = item.exTime!!.toInt()

        setWorkOutTimeDialog = Dialog(getActivity())
        setWorkOutTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        var dialogChangeWorkoutTimeBinding =
                DataBindingUtil.inflate<DialogChangeWorkoutTimeBinding>(
                        getLayoutInflater(),
                        R.layout.dialog_change_workout_time, null, false
                )

        setWorkOutTimeDialog.setContentView(dialogChangeWorkoutTimeBinding.root)

        dialogChangeWorkoutTimeBinding.tvTitle.text = item.exName
        dialogChangeWorkoutTimeBinding.tvDes.text = item.exDescription

        if (item.exUnit.equals(Constant.workout_type_step)) {
            dialogChangeWorkoutTimeBinding.tvTime.text = "X ${item.exTime}"
        } else {
            dialogChangeWorkoutTimeBinding.tvTime.text =
                    Utils.secToString(item.exTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
        }

        if (item.exVideo.isNullOrEmpty()) {
            dialogChangeWorkoutTimeBinding!!.imgVideo.visibility = View.GONE
        } else {
            dialogChangeWorkoutTimeBinding!!.imgVideo.visibility = View.VISIBLE
        }

        dialogChangeWorkoutTimeBinding.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
                Utils.ReplaceSpacialCharacters(item.exPath!!)?.let { Utils.getAssetItems(this, it) }

        if (listImg != null) {
            for (i in 0 until listImg.size) {
                val imgview = ImageView(this)
                //            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(this).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
                dialogChangeWorkoutTimeBinding.viewFlipper.addView(imgview)
            }
        }

        dialogChangeWorkoutTimeBinding.viewFlipper.isAutoStart = true
        dialogChangeWorkoutTimeBinding.viewFlipper.setFlipInterval(resources.getInteger(R.integer.viewfliper_animation))
        dialogChangeWorkoutTimeBinding.viewFlipper.startFlipping()


        dialogChangeWorkoutTimeBinding!!.imgMinus.setOnClickListener {
            second--
            if (item.exUnit.equals(Constant.workout_type_step)) {
                dialogChangeWorkoutTimeBinding.tvTime.text = "X ${second}"
            } else {
                dialogChangeWorkoutTimeBinding.tvTime.text =
                        Utils.secToString(second, Constant.WORKOUT_TIME_FORMAT)
            }
        }

        dialogChangeWorkoutTimeBinding!!.imgPlus.setOnClickListener {
            second++
            if (item.exUnit.equals(Constant.workout_type_step)) {
                dialogChangeWorkoutTimeBinding.tvTime.text = "X ${second}"
            } else {
                dialogChangeWorkoutTimeBinding.tvTime.text =
                        Utils.secToString(second, Constant.WORKOUT_TIME_FORMAT)
            }
        }

        dialogChangeWorkoutTimeBinding!!.imgClose.setOnClickListener {
            listener.cancel()
            setWorkOutTimeDialog.dismiss()
        }

        dialogChangeWorkoutTimeBinding!!.tvSave.setOnClickListener {
            dbHelper.updatePlanExTime(item.dayExId!!.toInt(), second.toString())
            listener.dismiss()
            setWorkOutTimeDialog.dismiss()
        }

        dialogChangeWorkoutTimeBinding!!.tvReset.setOnClickListener {
            second = dbHelper.getOriginalPlanExTime(item.dayExId!!.toInt())!!.toInt()
            if (item.exUnit.equals(Constant.workout_type_step)) {
                dialogChangeWorkoutTimeBinding.tvTime.text = "X ${second}"
            } else {
                dialogChangeWorkoutTimeBinding.tvTime.text =
                        Utils.secToString(second!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
            }
            listener.dismiss()
        }

        dialogChangeWorkoutTimeBinding!!.imgVideo.setOnClickListener {
            val i = Intent(getActivity(), ExerciseVideoActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(item))
            startActivity(i)
        }

        setWorkOutTimeDialog.getWindow()!!
                .setBackgroundDrawableResource(android.R.color.transparent);
        setWorkOutTimeDialog.show()
    }

    fun showExcDetailDialog(
            excList: MutableList<HomeExTableClass>,
            position: Int
    ) {
        var currPos = position
        val excDetailDialog = Dialog(getActivity())
        excDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        var dialogWorkoutDetailBinding =
                DataBindingUtil.inflate<DialogChangeWorkoutTimeBinding>(
                        getLayoutInflater(),
                        R.layout.dialog_change_workout_time, null, false
                )

        excDetailDialog.setContentView(dialogWorkoutDetailBinding.root)

        dialogWorkoutDetailBinding.llEdit.visibility = View.GONE
        dialogWorkoutDetailBinding.llPlusMinus.visibility = View.GONE
        dialogWorkoutDetailBinding.llPrevNext.visibility = View.VISIBLE

        fun showExcDetail(pos: Int) {
            val item = excList.get(pos)
            dialogWorkoutDetailBinding.tvTitle.text = item.exName
            dialogWorkoutDetailBinding.tvDes.text = item.exDescription
            dialogWorkoutDetailBinding.tvPos.text = "${pos + 1}/${excList.size}"

            if (item.exUnit.equals(Constant.workout_type_step)) {
                dialogWorkoutDetailBinding.tvTime.text = "X ${item.exTime}"
            } else {
                dialogWorkoutDetailBinding.tvTime.text =
                        Utils.secToString(item.exTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
            }

            if (item.exVideo.isNullOrEmpty()) {
                dialogWorkoutDetailBinding!!.imgVideo.visibility = View.GONE
            } else {
                dialogWorkoutDetailBinding!!.imgVideo.visibility = View.VISIBLE
            }

            dialogWorkoutDetailBinding.viewFlipper.removeAllViews()
            val listImg: ArrayList<String>? =
                    Utils.ReplaceSpacialCharacters(item.exPath!!)?.let { Utils.getAssetItems(this, it) }

            if (listImg != null) {
                for (i in 0 until listImg.size) {
                    val imgview = ImageView(this)
                    //            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                    Glide.with(this).load(listImg.get(i)).into(imgview)
                    imgview.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    dialogWorkoutDetailBinding.viewFlipper.addView(imgview)
                }
            }

            dialogWorkoutDetailBinding.viewFlipper.isAutoStart = true
            dialogWorkoutDetailBinding.viewFlipper.setFlipInterval(resources.getInteger(R.integer.viewfliper_animation))
            dialogWorkoutDetailBinding.viewFlipper.startFlipping()

            dialogWorkoutDetailBinding!!.imgVideo.setOnClickListener {
                val i = Intent(getActivity(), ExerciseVideoActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(item))
                startActivity(i)
            }
        }

        showExcDetail(currPos)
        dialogWorkoutDetailBinding.imgNext.setOnClickListener {
            if (currPos < excList.size - 1) {
                currPos++
                showExcDetail(currPos)
            }
        }

        dialogWorkoutDetailBinding.imgPrev.setOnClickListener {
            if (currPos > 0) {
                currPos--
                showExcDetail(currPos)
            }
        }

        dialogWorkoutDetailBinding.imgClose.setOnClickListener {
            excDetailDialog.dismiss()
        }

        excDetailDialog.getWindow()!!
                .setBackgroundDrawableResource(android.R.color.transparent);
        excDetailDialog.show()

    }


    var dialogSoundOptionBinding: BottomSheetSoundOptionBinding? = null
    lateinit var dialogSoundOption: BottomSheetDialog

    fun showSoundOptionDialog(mContext: Context, listner: DialogDismissListener) {
        val v: View = (mContext as Activity).getLayoutInflater()
                .inflate(R.layout.bottom_sheet_sound_option, null)
        dialogSoundOptionBinding = DataBindingUtil.bind(v)
        dialogSoundOption = BottomSheetDialog(mContext)
        dialogSoundOption.setContentView(v)

        dialogSoundOptionBinding!!.switchMute.isChecked =
                Utils.getPref(this@BaseActivity, Constant.PREF_IS_SOUND_MUTE, false)
        dialogSoundOptionBinding!!.switchCoachTips.isChecked =
                Utils.getPref(this@BaseActivity, Constant.PREF_IS_COACH_SOUND_ON, true)
        dialogSoundOptionBinding!!.switchVoiceGuide.isChecked =
                Utils.getPref(this@BaseActivity, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true)

        dialogSoundOptionBinding!!.switchMute.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dialogSoundOptionBinding!!.switchCoachTips.isChecked = false
                dialogSoundOptionBinding!!.switchVoiceGuide.isChecked = false
                Utils.setPref(this@BaseActivity, Constant.PREF_IS_SOUND_MUTE, true)
            } else {
                Utils.setPref(this@BaseActivity, Constant.PREF_IS_SOUND_MUTE, false)
            }
        }

        dialogSoundOptionBinding!!.switchCoachTips.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dialogSoundOptionBinding!!.switchMute.isChecked = false
                Utils.setPref(this, Constant.PREF_IS_COACH_SOUND_ON, true)
            } else {
                Utils.setPref(this, Constant.PREF_IS_COACH_SOUND_ON, false)
            }
        }

        dialogSoundOptionBinding!!.switchVoiceGuide.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dialogSoundOptionBinding!!.switchMute.isChecked = false
                Utils.setPref(this, Constant.PREF_IS_INSTRUCTION_SOUND_ON, true)
            } else {
                Utils.setPref(this, Constant.PREF_IS_INSTRUCTION_SOUND_ON, false)
            }
        }

        dialogSoundOptionBinding!!.llDone.setOnClickListener {
            dialogSoundOption.dismiss()
        }

        dialogSoundOption.setOnDismissListener {
            listner.onDialogDismiss()
        }

        dialogSoundOption.show()
    }

    fun showHeightWeightDialog(listner: DialogDismissListener) {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_height_weight, null)

        var boolKg: Boolean
        var boolInch: Boolean

        val editWeight = dialogLayout.findViewById<CEditTextView>(R.id.editWeight)
        val tvKG = dialogLayout.findViewById<CTextView>(R.id.tvKG)
        val tvLB = dialogLayout.findViewById<CTextView>(R.id.tvLB)
        val editHeightCM = dialogLayout.findViewById<CEditTextView>(R.id.editHeightCM)
        val tvCM = dialogLayout.findViewById<CTextView>(R.id.tvCM)
        val tvIN = dialogLayout.findViewById<CTextView>(R.id.tvIN)
        val editHeightFT = dialogLayout.findViewById<CEditTextView>(R.id.editHeightFT)
        val editHeightIn = dialogLayout.findViewById<CEditTextView>(R.id.editHeightIn)
        val llInch = dialogLayout.findViewById<LinearLayout>(R.id.llInch)

        var editWeightStr : String =""
        var editHeightCMStr : String =""
        var editHeightFTStr : String =""
        var editHeightInStr : String =""


        /*For weight*/
        editWeight.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (editWeight.text.toString().equals("0.0 KG") || editWeight.text.toString().equals("0.0 LB") ) {
                    editWeight.setText("")
                }else{
                    editWeight.setText(editWeight.text.toString())
                }
                editWeight.setSelection(editWeight.text!!.length)
            } else {
                if (Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString() == "0.0") {
                    if (Utils.getPref(this, Constant.CHECK_LB_KG, Constant.DEF_KG) == Constant.DEF_KG) {
                        if (editWeightStr.isEmpty()) {
                            editWeight.setText("0.0 KG")
                        }else{
                            editWeight.setText(editWeight.text.toString())
                        }
                    } else {
                        if (editWeightStr.isEmpty()) {
                            editWeight.setText("0.0 LB")
                        }else{
                            editWeight.setText(editWeight.text.toString())
                        }
                        editWeight.setSelection(editWeight.text!!.length)
                    }

                } else {
                    editWeight.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString())
                    editWeight.setSelection(editWeight.text!!.length)
                }
            }
        }
        editWeight.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().equals("0.0 KG").not() || s.toString().equals("0.0 LB").not()){
                    editWeightStr = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        /*for height cm*/
        editHeightCM.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (editHeightCM.text.toString().equals("0.0 CM")) {
                    editHeightCM.setText("")
                }else{
                    editHeightCM.setText(editHeightCM.text.toString())
                }
//                editHeightCM.setSelection(editHeightCM.text!!.length)
            } else {
                if (Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString() == "0"
                        && Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0f).toString().equals("0.0")) {
                    if (editHeightCMStr.isEmpty()) {
                        editHeightCM.setText("0.0 CM")
                    }else{
                        editHeightCM.setText(editHeightCM.text.toString())
                    }
                    editHeightCM.setSelection(editHeightCM.text!!.length)
                } else {
                    val inch = Utils.ftInToInch(
                            Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0),
                            Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toDouble())
                    editHeightCM.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString())
                    editHeightCM.setSelection(editHeightCM.text!!.length)
                }

            }
        }
        editHeightCM.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().equals("0.0 CM").not()){
                    editHeightCMStr = s.toString()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        /*for height ft*/
        editHeightFT.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {

                if (editHeightFT.text.toString().equals("0.0 FT")) {
                    editHeightFT.setText("")
                }else{
                    editHeightFT.setText(editHeightFT.text.toString())
                }

                editHeightFT.setSelection(editHeightFT.text!!.length)

            } else {
                if (Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString() == "0") {

                    if (editHeightFTStr.isEmpty()) {
                        editHeightFT.setText("0.0 FT")
                    }else{
                        editHeightFT.setText(editHeightFT.text.toString())
                    }
                    editHeightFT.setSelection(editHeightFT.text!!.length)
                } else {
                    editHeightFT.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString())
                    editHeightFT.setSelection(editHeightFT.text!!.length)
                }

            }
        }
        editHeightFT.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if (s.toString().equals("0.0 FT").not()){
                    editHeightFTStr = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        /*for height in*/
        editHeightIn.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (editHeightIn.text.toString().equals("0.0 IN")) {
                    editHeightIn.setText("")
                }else{
                    editHeightIn.setText(editHeightIn.text.toString())
                }
                editHeightIn.setSelection(editHeightIn.text!!.length)
            } else {
                if (Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0f).toString() == "0.0") {

                    if (editHeightInStr.isEmpty()) {
                        editHeightIn.setText("0.0 IN")
                    }else{
                        editHeightIn.setText(editHeightIn.text.toString())
                    }
                    editHeightIn.setSelection(editHeightIn.text!!.length)
                } else {
                    editHeightIn.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0f).toString())
                    editHeightIn.setSelection(editHeightIn.text!!.length)
                }

            }
        }
        editHeightIn.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().equals("0.0 IN").not()){
                    editHeightInStr = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        boolKg = true
        try {
            if (Utils.getPref(this, Constant.CHECK_LB_KG, Constant.DEF_KG) == Constant.DEF_KG) {

                editWeight.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString())

                editWeight.setSelection(editWeight.text!!.length)

                tvKG.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvLB.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                tvKG.isSelected = true
                tvLB.isSelected = false
            } else {
                boolKg = false

                editWeight.setText(Utils.kgToLb(Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toDouble()).toString())

                editWeight.setSelection(editWeight.text!!.length)
                tvKG.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                tvLB.setTextColor(ContextCompat.getColor(this, R.color.white))

                tvKG.isSelected = false
                tvLB.isSelected = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.e("TAG", "showHeightWeightDialog:::Check LbKg:::::::: "+ Utils.getPref(this, Constant.CHECK_LB_KG, Constant.DEF_LB))
        boolInch = true
        try {
            if (Utils.getPref(this, Constant.CHECK_LB_KG, Constant.DEF_KG) == Constant.DEF_LB) {

                editHeightCM.visibility = View.GONE
                llInch.visibility = View.VISIBLE

                tvIN.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvCM.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                tvIN.isSelected = true
                tvCM.isSelected = false

                /*if (Utils.getPref(this,Constant.PREF_LAST_INPUT_FOOT,0).toString().equals("0")){
                    editHeightFT.setText("0.0 FT")
                }else{
                    editHeightFT.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString())
                }

                if (Utils.getPref(this,Constant.PREF_LAST_INPUT_INCH,0).toString().equals("0")){
                    editHeightIn.setText("0.0 IN")
                }else{
                    editHeightIn.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toString())
                }*/
                checkLbKg(editWeight, editHeightCM, editHeightFT, editHeightIn)
            } else {
                boolInch = false

                editHeightCM.visibility = View.VISIBLE
                llInch.visibility = View.GONE

                tvIN.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                tvCM.setTextColor(ContextCompat.getColor(this, R.color.white))

                tvIN.isSelected = false
                tvCM.isSelected = true

               /* if (Utils.getPref(this,Constant.PREF_LAST_INPUT_FOOT,0).toString().equals("0")
                        && Utils.getPref(this,Constant.PREF_LAST_INPUT_INCH,0).toString().equals("0")){

                }else{

                }*/
                checkLbKg(editWeight, editHeightCM, editHeightFT, editHeightIn)

                /*val inch = Utils.ftInToInch(
                        Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0),
                        Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toDouble()
                )

                editHeightCM.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString())*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        tvKG.setOnClickListener {
            try {

                Utils.setPref(this, Constant.CHECK_LB_KG, Constant.DEF_KG)
                checkLbKg(editWeight, editHeightCM, editHeightFT, editHeightIn)
                if (boolInch) {
                    boolInch = false

                    editHeightCM.visibility = View.VISIBLE
                    llInch.visibility = View.GONE

                    tvIN.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                    tvCM.setTextColor(ContextCompat.getColor(this, R.color.white))

                    tvIN.isSelected = false
                    tvCM.isSelected = true

                    var inch = 0.0
                    if (editHeightFT.text.toString() != "" && editHeightFT.text.toString() != "") {
                        inch = Utils.ftInToInch(
                                editHeightFT.text.toString().toInt(),
                                editHeightIn.text.toString().toDouble()
                        )
                    } else if (editHeightFT.text.toString() != "" && editHeightIn.text.toString() == "") {
                        inch = Utils.ftInToInch(editHeightFT.text.toString().toInt(), 0.0)
                    } else if (editHeightFT.text.toString() == "" && editHeightIn.text.toString() != "") {
                        inch = Utils.ftInToInch(1, editHeightIn.text.toString().toDouble())
                    }

                    editHeightCM.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if (!boolKg) {
                    boolKg = true

                    tvKG.setTextColor(ContextCompat.getColor(this, R.color.white))
                    tvLB.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                    tvKG.isSelected = true
                    tvLB.isSelected = false

//                    editWeight.hint = Constant.DEF_KG

                    if (editWeight.text.toString() != "") {
                        editWeight.setText(
                                Utils.lbToKg(editWeight.text.toString().toDouble()).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        tvLB.setOnClickListener {
            try {
                Utils.setPref(this, Constant.CHECK_LB_KG, Constant.DEF_LB)
                checkLbKg(editWeight, editHeightCM, editHeightFT, editHeightIn)
                if (boolKg) {
                    boolKg = false


                    tvKG.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                    tvLB.setTextColor(ContextCompat.getColor(this, R.color.white))

                    tvKG.isSelected = false
                    tvLB.isSelected = true

//                    editWeight.hint = Constant.DEF_LB

                    if (editWeight.text.toString() != "") {
                        editWeight.setText(
                                Utils.kgToLb(editWeight.text.toString().toDouble()).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                editHeightCM.visibility = View.GONE
                llInch.visibility = View.VISIBLE

                tvIN.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvCM.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                tvIN.isSelected = true
                tvCM.isSelected = false

                try {
                    if (!boolInch) {
                        boolInch = true

                        if (editHeightCM.text.toString() != "") {
                            val inch = Utils.cmToInch(editHeightCM.text.toString().toDouble())
                            editHeightFT.setText(Utils.calcInchToFeet(inch).toString())
                            editHeightIn.setText(Utils.calcInFromInch(inch).toString())
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        tvCM.setOnClickListener {
            try {
                Utils.setPref(this, Constant.CHECK_LB_KG, Constant.DEF_KG)
                checkLbKg(editWeight, editHeightCM, editHeightFT, editHeightIn)
                editWeight.setSelection(editWeight.text!!.length)
                if (boolInch) {
                    boolInch = false

                    editHeightCM.visibility = View.VISIBLE
                    llInch.visibility = View.GONE

                    tvIN.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                    tvCM.setTextColor(ContextCompat.getColor(this, R.color.white))

                    tvIN.isSelected = false
                    tvCM.isSelected = true

                    if (editHeightIn.text.toString().equals("0.0 IN") || editHeightFT.text.toString().equals("0.0 FT")){
                        editHeightCM.setText("0.0 CM")
                    }else {
                        var inch = 0.0
                        if (editHeightFT.text.toString() != "" && editHeightIn.text.toString() != "") {
                            inch = Utils.ftInToInch(
                                    editHeightFT.text.toString().toInt(),
                                    editHeightIn.text.toString().toDouble()
                            )
                        } else if (editHeightFT.text.toString() != "" && editHeightIn.text.toString() == "") {
                            inch = Utils.ftInToInch(editHeightFT.text.toString().toInt(), 0.0)
                        } else if (editHeightFT.text.toString() == "" && editHeightIn.text.toString() != "") {
                            inch = Utils.ftInToInch(1, editHeightIn.text.toString().toDouble())
                        }

                        editHeightCM.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if (!boolKg) {
                    boolKg = true

                    tvKG.setTextColor(ContextCompat.getColor(this, R.color.white))
                    tvLB.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                    tvKG.isSelected = true
                    tvLB.isSelected = false

//                    editWeight.hint = Constant.DEF_KG

                    if (editWeight.text.toString() != "") {
                        editWeight.setText(
                                Utils.lbToKg(editWeight.text.toString().toDouble()).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        tvIN.setOnClickListener {
            try {
                Utils.setPref(this, Constant.CHECK_LB_KG, Constant.DEF_LB)
                checkLbKg(editWeight, editHeightCM, editHeightFT, editHeightIn)
                if (boolKg) {
                    boolKg = false


                    tvKG.setTextColor(ContextCompat.getColor(this, R.color.col_666))
                    tvLB.setTextColor(ContextCompat.getColor(this, R.color.white))

                    tvKG.isSelected = false
                    tvLB.isSelected = true

//                    editWeight.hint = Constant.DEF_LB

                    if (editWeight.text.toString() != "") {
                        editWeight.setText(
                                Utils.kgToLb(editWeight.text.toString().toDouble()).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                editHeightCM.visibility = View.GONE
                llInch.visibility = View.VISIBLE

                tvIN.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvCM.setTextColor(ContextCompat.getColor(this, R.color.col_666))

                tvIN.isSelected = true
                tvCM.isSelected = false

                try {
                    if (!boolInch) {
                        boolInch = true

                        if (editHeightCM.text.toString() != "") {
                            val inch = Utils.cmToInch(editHeightCM.text.toString().toDouble())
                            editHeightFT.setText(Utils.calcInchToFeet(inch).toString())
                            editHeightIn.setText(Utils.calcInFromInch(inch).toString())
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        builder.setView(dialogLayout)

        val dob = Utils.getPref(this, Constant.PREF_DOB, "")
        var posBtnText = ""
        if (dob.isNullOrEmpty()) {
            posBtnText = getString(R.string.next)
        } else {
            posBtnText = getString(R.string.set)
        }

        editWeight.setOnClickListener {
            if (editWeight.text.toString().equals("0.0 KG") || editWeight.text.toString().equals("0.0 LB")){
                editWeight.setText("")
            }
        }
        builder.setPositiveButton(posBtnText) { dialog, which ->

            try {
                if (boolInch) {
                    Utils.setPref(this, Constant.PREF_LAST_INPUT_FOOT, editHeightFT.text.toString().toInt())
                    Utils.setPref(this, Constant.PREF_LAST_INPUT_INCH, editHeightIn.text.toString().toFloat())
                    Utils.setPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_IN)

                } else {
                    val inch = Utils.cmToInch(editHeightCM.text.toString().toDouble())
                    Utils.setPref(this, Constant.PREF_LAST_INPUT_FOOT, Utils.calcInchToFeet(inch))
                    Utils.setPref(this, Constant.PREF_LAST_INPUT_INCH, Utils.calcInFromInch(inch).toFloat())
                    Utils.setPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_CM)

                }


                val strKG: Float
                if (boolKg) {
                    strKG = editWeight.text.toString().toFloat()
                    Utils.setPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)
                    Utils.setPref(this, Constant.PREF_LAST_INPUT_WEIGHT, strKG)
                } else {
                    strKG =
                            Utils.lbToKg(editWeight.text.toString().toDouble()).roundToInt().toFloat()
                    Utils.setPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_LB)
                    Utils.setPref(this, Constant.PREF_LAST_INPUT_WEIGHT, strKG)
                }

                val currentDate = Utils.parseTime(Date().time, Constant.WEIGHT_TABLE_DATE_FORMAT)

                if (dbHelper.weightExistOrNot(currentDate)) {
                    dbHelper.updateWeight(currentDate, strKG.toString(), "")
                } else {
                    dbHelper.addUserWeight(strKG.toString(), currentDate, "")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (dob.isNullOrEmpty()) {
                showGenderDOBDialog(this, listner)
            } else {
                listner.onDialogDismiss()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.btn_cancel) { dialog, which ->
            dialog.dismiss()
            listner.onDialogDismiss()
        }
        builder.create().show()


    }

    private fun checkLbKg(editWeight: EditText, editHeightCM: EditText, editHeightFT: EditText, editHeightIn: EditText) {

        /*for weight*/

        if (Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString().equals("0.0")
                && Utils.getPref(this, Constant.CHECK_LB_KG, Constant.DEF_KG).equals(Constant.DEF_KG)) {
            editWeight.setText("0.0 KG")
        } else if (Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f).toString().equals("0.0")
                && Utils.getPref(this, Constant.CHECK_LB_KG, Constant.DEF_LB).equals(Constant.DEF_LB)) {
            editWeight.setText("0.0 LB")
        }
        editWeight.setSelection(editWeight.text!!.length)

        /*for height cm*/
        Log.e("TAG", "checkLbKg:::::FIFIIF "+Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0)+" "+
                Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0f))

        if (Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString().equals("0")
                && Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0f).toString().equals("0.0")) {
            editHeightCM.setText("0.0 CM")
        } else {
            val inch = Utils.ftInToInch(Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0),
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toDouble())
            Log.e("TAG", "checkLbKg:::::FIFIIF "+Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0)+" "+
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0f))
            editHeightCM.setText(Utils.inchToCm(inch).roundToInt().toDouble().toString())
        }


        /*for height ft*/
        if (Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString().equals("0")) {
            editHeightFT.setText("0.0 FT")
        } else {
            editHeightFT.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString())
        }


        /*for height in*/
        if (Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0f).toString().equals("0.0")) {
            editHeightIn.setText("0.0 IN")
        } else {
            editHeightIn.setText(Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0f).toString())
        }

    }

    private val DEFAULT_MIN_YEAR = 1960
    private var yearPos = 0
    private var monthPos = 0
    private var dayPos = 0

    var yearList = arrayListOf<String?>()
    var monthList = arrayListOf<String?>()
    var dayList = arrayListOf<String?>()

    private var minYear = 0
    private var maxYear = 0
    private var viewTextSize = 0

    fun showGenderDOBDialog(
            mContext: Context,
            listner: DialogDismissListener
    ) {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        val v: View = (mContext as Activity).getLayoutInflater()
                .inflate(R.layout.dialog_gender_dob, null)
        val dialogbinding: DialogGenderDobBinding? = DataBindingUtil.bind(v)

        builder.setView(dialogbinding!!.root)

        if (Utils.getPref(this, Constant.PREF_GENDER, "").isNullOrEmpty().not()) {
            if (Utils.getPref(this, Constant.PREF_GENDER, "").equals(Constant.FEMALE)) {
                dialogbinding.tvFemale.isSelected = true
                dialogbinding.tvMale.isSelected = false
            } else {
                dialogbinding.tvMale.isSelected = true
                dialogbinding.tvFemale.isSelected = false
            }
        }

        dialogbinding.tvMale.setOnClickListener {
            dialogbinding.tvMale.isSelected = true
            dialogbinding.tvFemale.isSelected = false
        }

        dialogbinding.tvFemale.setOnClickListener {
            dialogbinding.tvFemale.isSelected = true
            dialogbinding.tvMale.isSelected = false
        }

        minYear = DEFAULT_MIN_YEAR
        viewTextSize = 25
        maxYear = Calendar.getInstance().get(Calendar.YEAR) - 10

        setSelectedDate()
        dialogbinding.tvMale.isSelected = true
        initPickerViews(dialogbinding.dobPicker)
        initDayPickerView(dialogbinding.dobPicker)

        builder.setPositiveButton(R.string.save) { dialog, which ->

            if (dialogbinding.tvMale.isSelected || dialogbinding.tvFemale.isSelected) {

                val day = dayList[dialogbinding.dobPicker.npDay.value]
                val month = monthList[dialogbinding.dobPicker.npMonth.value]
                val year = yearList[dialogbinding.dobPicker.npYear.value]

                val dob = Utils.parseTime("$day-$month-$year", "dd-mm-yyyy")

                if (dob < Date()) {

                    Utils.setPref(this, Constant.PREF_DOB, "$day-$month-$year")
                    if (dialogbinding.tvMale.isSelected)
                        Utils.setPref(this, Constant.PREF_GENDER, Constant.MALE)

                    if (dialogbinding.tvFemale.isSelected)
                        Utils.setPref(this, Constant.PREF_GENDER, Constant.FEMALE)

                    dialog.dismiss()
                    listner.onDialogDismiss()
                } else {
                    showToast("Date of birth must less then current date")
                }
            }
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which ->
            dialog.dismiss()
            listner.onDialogDismiss()
        })
        builder.setNeutralButton(R.string.previous, { dialog, which ->
            dialog.dismiss()
            showHeightWeightDialog(listner)
        })
        builder.create().show()
    }

    fun showYearOfBirthDialog(mContext: Context, listner: DialogDismissListener) {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        val v: View = (mContext as Activity).getLayoutInflater()
                .inflate(R.layout.dialog_dob, null)
        val dialogbinding: DialogDobBinding? = DataBindingUtil.bind(v)

        builder.setView(dialogbinding!!.root)

        minYear = DEFAULT_MIN_YEAR;
        viewTextSize = 25
        maxYear = Calendar.getInstance().get(Calendar.YEAR) - 10

        setSelectedDate()

        initPickerViews(dialogbinding)
        initDayPickerView(dialogbinding)

        builder.setPositiveButton(R.string.set) { dialog, which ->
            val day = dayList[dialogbinding.npDay.value]
            val month = monthList[dialogbinding.npMonth.value]
            val year = yearList[dialogbinding.npYear.value]

            Utils.setPref(this, Constant.PREF_DOB, "$day-$month-$year")
            dialog.dismiss()
            listner.onDialogDismiss()
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> dialog.dismiss() })
        builder.create().show()
    }

    open fun setSelectedDate() {
        var today = Calendar.getInstance()
        if (Utils.getPref(this, Constant.PREF_DOB, "").isNullOrEmpty().not()) {
            val date = Utils.parseTime(Utils.getPref(this, Constant.PREF_DOB, "")!!, "dd-mm-yyyy")
            today.time = date
        }
        yearPos = today[Calendar.YEAR] - minYear
        monthPos = today[Calendar.MONTH]
        dayPos = today[Calendar.DAY_OF_MONTH] - 1
    }


    fun initPickerViews(dialogbinding: DialogDobBinding) {
        val yearCount: Int = maxYear - minYear
        for (i in 0 until yearCount) {
            yearList.add(format2LenStr(minYear + i))
        }
        for (j in 0..11) {
            monthList.add(format2LenStr(j + 1))
        }
        dialogbinding.npYear.setDisplayedValues(yearList.toArray(arrayOf()))
        dialogbinding.npYear.setMinValue(0)
        dialogbinding.npYear.setMaxValue(yearList.size - 1)
        dialogbinding.npYear.value = yearPos

        dialogbinding.npMonth.setDisplayedValues(monthList.toArray(arrayOf()))
        dialogbinding.npMonth.setMinValue(0)
        dialogbinding.npMonth.setMaxValue(monthList.size - 1)
        dialogbinding.npMonth.value = monthPos

    }

    /**
     * Init day item
     */
    fun initDayPickerView(dialogbinding: DialogDobBinding) {
        val dayMaxInMonth: Int
        val calendar = Calendar.getInstance()
        dayList = arrayListOf()
        calendar[Calendar.YEAR] = minYear + yearPos
        calendar[Calendar.MONTH] = monthPos

        //get max day in month
        dayMaxInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until dayMaxInMonth) {
            dayList.add(format2LenStr(i + 1))
        }

        dialogbinding.npDay.setDisplayedValues(dayList.toArray(arrayOf()))
        dialogbinding.npDay.setMinValue(0)
        dialogbinding.npDay.setMaxValue(dayList.size - 1)
        dialogbinding.npDay.value = dayPos
    }

    open fun format2LenStr(num: Int): String? {
        return if (num < 10) "0$num" else num.toString()
    }

    open fun isLeapYear(year: Int): Boolean {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365
    }

    fun showTimePickerDialog(context: Context, date: Date, eventListener: DateEventListener?,hour:Int,minute:Int) {
        val c = Calendar.getInstance()
        c.time = date
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    Debug.e(
                            "TAG",
                            "onTimeSet() called with: view = [$view], hourOfDay = [$hourOfDay], minute = [$minute]"
                    )

                    //Date date = new Date(selectedyear, selectedmonth, selectedday, hourOfDay, minute, 0);
                    val date = Utils.parseTime(
                            c.get(Calendar.DAY_OF_MONTH)
                                    .toString() + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR) + " " + hourOfDay + ":" + minute + ":00",
                            "dd/MM/yyyy HH:mm:ss"
                    )
                    eventListener?.onDateSelected(date, hourOfDay, minute)
                }, hour, minute, false
        )
        timePicker.show()
    }

    fun showUnlockTrainingDialog(mContext: Context) {

        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        val v: View = (mContext as Activity).getLayoutInflater()
                .inflate(R.layout.dialog_unloack_training, null)
        val dialogbinding: DialogUnloackTrainingBinding? = DataBindingUtil.bind(v)

        dialogbinding!!.imgBack.setOnClickListener {
            finish()
        }

        dialogbinding!!.llFreeTrial.setOnClickListener {
            val i = Intent(this, AccessAllFeaturesActivity::class.java)
            startActivity(i)
        }

        dialogbinding.llWatchVideo.setOnClickListener {
            if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_GOOGLE) {
                /*showVideoAd(object : CallbackListener {
                    override fun onSuccess() {
                        dialog.dismiss()
                    }

                    override fun onCancel() {
                        dialog.dismiss()
                        finish()
                    }

                    override fun onRetry() {
                        dialog.dismiss()
                    }

                })*/
                AdUtils.loadGoogleFullAd(mContext,object : AdsCallback{
                    override fun startNextScreenAfterAd() {
                        dialog.dismiss()
                    }

                })
            }else if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_FACEBOOK) {
                AdUtils.loadFacebookFullAd(mContext,object :AdsCallback{
                    override fun startNextScreenAfterAd() {
                        dialog.dismiss()
                    }

                })

               /* AdUtils.loadFacebookVideoAd(mContext,object :CallbackListener{
                    override fun onSuccess() {

                    }

                    override fun onCancel() {

                    }

                    override fun onRetry() {

                    }

                })*/
            }
        }

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels


        dialog.setContentView(v)
        try {
            if (Utils.isPurchased(this).not())
                dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dialog.window!!.setLayout(width.toInt(), height.toInt())
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initVideoAd()

    }

    fun initVideoAd() {
        if (mRewardedVideoAd == null) {
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        }
    }

    fun loadVideoAd(adLoadCallback: RewardedAdLoadCallback) {
        if (mRewardedVideoAd!!.isLoaded.not())
            mRewardedVideoAd!!.loadAd(Constant.GOOGLE_REWARDED_VIDEO,
                    AdRequest.Builder().build())
    }

    fun showVideoAd(listner: CallbackListener) {

        if (Debug.DEBUG_IS_HIDE_AD) {
            listner.onSuccess()
            return
        }

        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {

            }

            override fun onRewardedAdFailedToLoad(p0: Int) {
                super.onRewardedAdFailedToLoad(p0)

            }

        }

        mRewardedVideoAd!!.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {

            }

            override fun onRewardedVideoAdLeftApplication() {
                listner.onCancel()
            }

            override fun onRewardedVideoAdLoaded() {
                dismissDialog()
                mRewardedVideoAd!!.show()
            }

            override fun onRewardedVideoAdOpened() {

            }

            override fun onRewardedVideoCompleted() {
                listner.onSuccess()
            }

            override fun onRewarded(p0: RewardItem?) {
                listner.onSuccess()
            }

            override fun onRewardedVideoStarted() {
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                Debug.e("onRewardedVideoAdFailedToLoad ==>", p0.toString())
                dismissDialog()
                listner.onSuccess()
            }

        }

        if (mRewardedVideoAd!!.isLoaded)
            mRewardedVideoAd!!.show()
        else {
            showDialog("")
            loadVideoAd(adLoadCallback)
        }

    }

    fun openInternetDialog(callbackListener: CallbackListener) {
        if (!isOnline(this)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("No internet Connection")
            builder.setCancelable(false)
            builder.setMessage("Please turn on internet connection to continue")
            builder.setNegativeButton("Retry") { dialog, _ ->
                dialog!!.dismiss()
                openInternetDialog(callbackListener)
                callbackListener.onRetry()

            }
            builder.setPositiveButton("Close") { dialog, _ ->
                dialog!!.dismiss()
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(homeIntent)
                finishAffinity()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}
