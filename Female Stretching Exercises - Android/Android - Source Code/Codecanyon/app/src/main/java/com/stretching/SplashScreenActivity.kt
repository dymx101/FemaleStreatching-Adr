package com.stretching

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.provider.Settings
import com.android.vending.billing.IInAppBillingService
import com.stretching.inapp.IabHelper
import com.stretching.utils.Constant
import com.stretching.utils.Debug
import com.stretching.utils.RequestParamsUtils
import com.stretching.utils.Utils
import com.utillity.db.DataHelper
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap


class SplashScreenActivity : BaseActivity() {

    internal var handler = Handler()
    internal var bindService: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        DataHelper(this).checkDBExist()

        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.setPackage("com.android.vending")

        try {
            //TodO remove below comment
            bindService = bindService(serviceIntent, mServiceConn1, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showAlertDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle("Important")
            .setMessage(getActivity().resources.getString(R.string.permission_msg))
            .setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
            .setNegativeButton(getActivity().resources.getString(R.string.permission_setting)) { dialog, which ->
                dialog.dismiss()
                finish()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }.show()
    }

    fun startapp(sleepTime: Long) {
        handler.postDelayed(startApp, sleepTime)
    }

    internal var startApp: Runnable = object : Runnable {
        override fun run() {
            handler.removeCallbacks(this)

            if (Utils.getPref(
                    this@SplashScreenActivity,
                    Constant.PREF_IS_FIRST_TIME,
                    true
                ) && Utils.getPref(this@SplashScreenActivity, Constant.PREF_WHATS_YOUR_GOAL, "")
                    .isNullOrEmpty()
            ) {
                Utils.setPref(this@SplashScreenActivity, Constant.PREF_IS_FIRST_TIME, false)
                val i = Intent(getActivity(), WhatsYourGoalActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(i)
                finish()
            } else {
                val i = Intent(getActivity(), HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(i)
                finish()
            }
        }
    }

    // Todo In-app service connection
    //ServiceConnection mServiceConn;
    internal var mService: IInAppBillingService? = null
    internal var mServiceConn1: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = IInAppBillingService.Stub.asInterface(service)
            try {
                val ownedItems =
                    mService!!.getPurchases(3, packageName, IabHelper.ITEM_TYPE_SUBS, null)
                val response = ownedItems.getInt("RESPONSE_CODE")

                //JSONArray arr = new JSONArray(ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST"));
                val arrHashProduct = ArrayList<HashMap<String, String>>()
                try {
                    val purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
                    if (purchaseDataList != null && purchaseDataList.size > 0) {
                        for (i in purchaseDataList.indices) {
                            val purchaseData = purchaseDataList[i]
                            val jo = JSONObject(purchaseData)
                            val skuId = jo.getString("productId")
                            val purchaseState: Int
                            if (jo.getString("purchaseState").isNotEmpty()) {
                                purchaseState = Integer.parseInt(jo.getString("purchaseState"))
                            } else {
                                purchaseState = -1
                            }
                            // InApp.addPurchasedItem(rootContext, skuId, purchaseState);
                            val valueMap = HashMap<String, String>()
                            valueMap["productId"] = skuId
                            valueMap["isProductPurchased"] =
                                "" + if (purchaseState == 0) "true" else "false"
                            arrHashProduct.add(valueMap)
                            if (purchaseState == 0) {
                                Utils.setPref(
                                    this@SplashScreenActivity,
                                    Constant.PREF_KEY_PURCHASE_STATUS,
                                    true
                                )
                                return
                            } else {
                                Utils.setPref(
                                    this@SplashScreenActivity,
                                    Constant.PREF_KEY_PURCHASE_STATUS,
                                    false
                                )
                            }
                        }
                    } else {
//                        checkPurchaseStatus()
                        Utils.setPref(
                            this@SplashScreenActivity,
                            Constant.PREF_KEY_PURCHASE_STATUS,
                            false
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mService != null) {
                unbindService(mServiceConn1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
