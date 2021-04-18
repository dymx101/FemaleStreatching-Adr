package com.stretching

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.android.vending.billing.IInAppBillingService
import com.stretching.databinding.ActivityAccessAllFeatureBinding
import com.stretching.inapp.*
import com.stretching.interfaces.CallbackListener
import com.stretching.utils.Constant
import com.stretching.utils.Debug
import com.stretching.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class AccessAllFeaturesActivity : BaseActivity(),IabBroadcastReceiver.IabBroadcastListener,
    CallbackListener {

    var binding: ActivityAccessAllFeatureBinding? = null
    // Todo inapp purchase declaration
    // The helper object
    lateinit var mHelper: IabHelper
    // Provides purchase notification while this app is running
    lateinit var mBroadcastReceiver: IabBroadcastReceiver
    private val TAG = "Purchase Activity"
    private val base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArcqqQ7G20/q3mR9bMqZBIaZhbR7lGU1dd/0Gvd4S+1aW2quPAWArpKzPpDHxs2pCe82unOFPNl64wTnqekM5aFFctDpTBeJ873nM59zieWrkdFI1ZJXXCG4ZhWdvABr/qMWqhVstacPlCcn50f55gl1ra9UybdvXwEzGBC5UvOuuWfJHl/BgYUk4RMHBhpJ3s22ow5E7wzlXl3hq+AatO4Qlr+phrDmAWBZ6aU5htlKIseaS+vTOs+oWd+8cCT1vA4oVZTydR3TZi/9aObEbDRWM2nb9U112/X6wssOtsmT0HyV81iPJs2Lc4pPGldkFQ58N3g1HMpG/CDIwMPjmrwIDAQAB"

    // static final String SKU_PREMIUM = "monthly_sub";
    internal val RC_REQUEST = 10001

    internal var bindService: Boolean = false

    internal val SKU_MONTHLY_SUB = "99monthlysubscription"
    internal val SKU_YEAR_SUB = "499yearlysubscription"

    internal var mInfiniteGasSku = ""
    // Will the subscription auto-renew?
    internal var mAutoRenewEnabled = false

    internal var mSubscribedToInfiniteGas = false

    internal var mSelectedSubscriptionPeriod = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_access_all_feature)

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
        binding!!.handler = ClickHandler()

        loadPurchaseData()

        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.setPackage("com.android.vending")

        try {
            bindService = bindService(serviceIntent, mServiceConn1, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding!!.handler?.onFreeTrialClick()

    }


    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
    }


    inner class ClickHandler {

        fun onBackClick(){
            finish()
        }

        fun onFreeTrialClick(){
            binding!!.borderFreeTrial.visibility = View.VISIBLE
            binding!!.borderPerMonth.visibility = View.INVISIBLE
            binding!!.llFreeTrial.setBackgroundResource(R.drawable.bg_dark_green_radius_8)
            binding!!.llPerMonth.setBackgroundResource(R.drawable.bg_dark_blue_radius_8)
            binding!!.imgCheckFreeTrial.setImageResource(R.drawable.ic_goal_complete)
            binding!!.imgCheckPerMonth.setImageResource(R.drawable.bg_circle_border)
            mSelectedSubscriptionPeriod = SKU_YEAR_SUB
        }

        fun onPerMonthClick(){
            binding!!.borderFreeTrial.visibility = View.INVISIBLE
            binding!!.borderPerMonth.visibility = View.VISIBLE
            binding!!.llFreeTrial.setBackgroundResource(R.drawable.bg_dark_blue_radius_8)
            binding!!.llPerMonth.setBackgroundResource(R.drawable.bg_dark_green_radius_8)
            binding!!.imgCheckFreeTrial.setImageResource(R.drawable.bg_circle_border)
            binding!!.imgCheckPerMonth.setImageResource(R.drawable.ic_goal_complete)
            mSelectedSubscriptionPeriod = SKU_MONTHLY_SUB


        }

        fun onContinueClick(){
            val payload = ""
            var oldSkus: MutableList<String>? = null
            if (!TextUtils.isEmpty(mInfiniteGasSku) && mInfiniteGasSku != mSelectedSubscriptionPeriod) {
                // The user currently has a valid subscription, any purchase action is going to
                // replace that subscription
                oldSkus = ArrayList()
                oldSkus!!.add(mInfiniteGasSku)
            }

            Log.d(TAG, "Launching purchase flow for Sync subscription.")
            if (mHelper.mAsyncInProgress) {
                mHelper.flagEndAsync()
            }

            try {
                mHelper.launchPurchaseFlow(this@AccessAllFeaturesActivity, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS, oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload)
            } catch (e: IabHelper.IabAsyncInProgressException) {
                complain("Error launching purchase flow. Another async operation in progress.")
            }
        }

    }

    internal var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener = object : IabHelper.OnIabPurchaseFinishedListener {
        override fun onIabPurchaseFinished(result: IabResult, purchase: Purchase) {
            Log.d(TAG, "Purchase finished: $result, purchase: $purchase")

//              if we were disposed of in the meantime, quit.
//            if (mHelper == null) {
//                return
//            }

            if (result.response == 7) {
                alert("Item already purchased.")
                return
            } else if (result.isFailure) {
                complain("Error purchasing: $result")
                return
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.")
                return
            }

            Debug.d(TAG, "Subscription successful.")
            Utils.setPref(this@AccessAllFeaturesActivity, Constant.PREF_KEY_PURCHASE_STATUS, true)
            if (purchase.sku.equals(SKU_MONTHLY_SUB)) {
                // bought the infinite gas subscription
                Debug.d(TAG, "Subscription purchased.")
                alert("Thank you for Subscribing Now you enjoy our app!")

                mSubscribedToInfiniteGas = true
                mAutoRenewEnabled = purchase.isAutoRenewing
                mInfiniteGasSku = purchase.sku
                startActivity(Intent(this@AccessAllFeaturesActivity, HomeActivity::class.java))
                finish()
            } else if (purchase.sku.equals(SKU_YEAR_SUB)) {
                // bought the infinite gas subscription
                Debug.d(TAG, "Subscription purchased.")
                alert("Thank you for Subscribing Now you enjoy our app!")

                mSubscribedToInfiniteGas = true
                mAutoRenewEnabled = purchase.isAutoRenewing
                mInfiniteGasSku = purchase.sku
                startActivity(Intent(this@AccessAllFeaturesActivity, HomeActivity::class.java))
                finish()
            }
        }

    }

    // Todo Here define inapp purchase methods and data
    private fun loadPurchaseData() {
        Debug.d(TAG, "Creating IAB helper.")
        mHelper = IabHelper(this, base64EncodedPublicKey)

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true)

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Debug.d(TAG, "Starting setup.")
        mHelper.startSetup(object : IabHelper.OnIabSetupFinishedListener {
            override fun onIabSetupFinished(result: IabResult) {
                Debug.d(TAG, "Setup finished.")

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: $result")
                    return
                }

                mBroadcastReceiver = IabBroadcastReceiver(this@AccessAllFeaturesActivity)
                val broadcastFilter = IntentFilter(IabBroadcastReceiver.ACTION)
                registerReceiver(mBroadcastReceiver, broadcastFilter)

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Debug.d(TAG, "Setup successful. Querying inventory.")
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener)
                } catch (e: IabHelper.IabAsyncInProgressException) {
                    complain("Error querying inventory. Another async operation in progress.")
                }
            }
        })
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    internal var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener = object : IabHelper.QueryInventoryFinishedListener {
        override fun onQueryInventoryFinished(result: IabResult, inventory: Inventory) {
            Debug.e(TAG, "Query inventory finished.")

            // Have we been disposed of in the meantime? If so, quit.
//            if (mHelper == null) return

            // Is it a failure?
            if (result.isFailure) {
                complain("Failed to query inventory: $result")
                return
            }

            Debug.e(TAG, "Query inventory was successful.")

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            //            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            //            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            //            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            val MonthlySub = inventory.getPurchase(SKU_MONTHLY_SUB)
            val YearlySub = inventory.getPurchase(SKU_YEAR_SUB)

            if (MonthlySub != null && MonthlySub.isAutoRenewing()) {
                mInfiniteGasSku = SKU_MONTHLY_SUB
                mAutoRenewEnabled = true
            } else if (YearlySub != null && YearlySub.isAutoRenewing()) {
                mInfiniteGasSku = SKU_YEAR_SUB
                mAutoRenewEnabled = true
            } else {
                mInfiniteGasSku = ""
                mAutoRenewEnabled = false
            }

            Debug.d(TAG, "Initial inventory query finished; enabling main UI.")
        }
    }

    //    ServiceConnection mServiceConn;
    internal var mService: IInAppBillingService? = null
    internal var mServiceConn1: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = IInAppBillingService.Stub.asInterface(service)
            getSKUDetail()
            try {
                val ownedItems = mService!!.getPurchases(3, packageName, "subs", null)
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
                            if (jo.getString("purchaseState").length > 0) {
                                purchaseState = Integer.parseInt(jo.getString("purchaseState"))
                            } else {
                                purchaseState = -1
                            }
                            // InApp.addPurchasedItem(rootContext, skuId, purchaseState);
                            val valueMap = HashMap<String, String>()
                            valueMap["productId"] = skuId
                            valueMap["isProductPurchased"] = "" + if (purchaseState == 0) "true" else "false"
                            arrHashProduct.add(valueMap)
                            if (purchaseState == 0) {
                                Utils.setPref(this@AccessAllFeaturesActivity, Constant.PREF_KEY_PURCHASE_STATUS, true)
                                return
                            } else {
                                Utils.setPref(this@AccessAllFeaturesActivity, Constant.PREF_KEY_PURCHASE_STATUS, false)
                            }
                        }
                    } else {
//                        checkPurchaseStatus()
                        Utils.setPref(this@AccessAllFeaturesActivity, Constant.PREF_KEY_PURCHASE_STATUS, false)
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

    fun getSKUDetail() {

        try {
            val querySkus = Bundle()
            val productIds = ArrayList<String>(1)
            productIds.add(SKU_MONTHLY_SUB)
            productIds.add(SKU_YEAR_SUB)
            querySkus.putStringArrayList("ITEM_ID_LIST", productIds)
            val skuDetails = mService!!.getSkuDetails(
                3,
                getPackageName(),
                "subs",
                querySkus
            )
            val responseCode = skuDetails!!.getInt("RESPONSE_CODE")
            if (responseCode == 0 && skuDetails != null && skuDetails.containsKey("DETAILS_LIST")) {
                val responseSubsList =
                    skuDetails.getStringArrayList("DETAILS_LIST")
                if (responseSubsList != null) {
                    for (thisResponse in responseSubsList) {
                        val valueMap =
                            HashMap<String, String>()
                        try {
                            val `object` = JSONObject(thisResponse)
                            valueMap["productId"] = `object`.getString("productId")
                            valueMap["type"] = `object`.getString("type")
                            valueMap["price_amount_micros"] =
                                `object`.getString("price_amount_micros")
                            valueMap["price_currency_code"] =
                                `object`.getString("price_currency_code")

                            if(valueMap["productId"].equals(SKU_MONTHLY_SUB))
                            {
                                binding!!.tvMonthly.text = String.format("%2s/Month", `object`.getString("price"))
                            }else{
                                binding!!.tvYearly.text =  "then " + String.format("%2s/Year", `object`.getString("price"))
                            }


                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    binding!!.llFrame.visibility=View.VISIBLE
                }
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    internal fun verifyDeveloperPayload(p: Purchase): Boolean {
        val payload = p.getDeveloperPayload()

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true
    }

    internal fun complain(message: String) {
        Debug.e(TAG, "**** Error: $message")
        alert("Error: $message")
    }

    internal fun alert(message: String) {
        val bld = android.app.AlertDialog.Builder(this)
        bld.setMessage(message)
        bld.setNeutralButton("OK", null)
        Debug.d(TAG, "Showing alert dialog: $message")
        bld.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mService != null) {
                unbindService(mServiceConn1)
            }

            if (mHelper != null) {
                mHelper.dispose()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        unregisterReceiver(mBroadcastReceiver)
    }

    override fun receivedBroadcast() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult($requestCode,$resultCode,$data")

        // Pass on the activity result to the helper for handling
        if (requestCode == RC_REQUEST && resultCode == Activity.RESULT_OK) {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
//            if(resultCode == Activity.RESULT_OK){
//                startActivity(Intent(this@PurchaseActivity,HomeActivity::class.java))
//            }
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                super.onActivityResult(requestCode, resultCode, data)
            } else {
                Log.d(TAG, "onActivityResult handled by IABUtil.")
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
