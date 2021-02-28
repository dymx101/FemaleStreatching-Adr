package com.stretching.fragments

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import com.stretching.utils.RetrofitProgressDialog



open class BaseFragment : Fragment() {

    internal lateinit var toast: Toast

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toast = Toast.makeText(activity, "", Toast.LENGTH_LONG)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item != null) {
            when (item.getItemId()) {
                android.R.id.home -> {
                    // This ID represents the home or Up button. In the case of this
                    // activity, the Up button is shown. Use NavUtils to allow users
                    // to navigate up one level in the application structure. For
                    // more details, see the Navigation pattern on Android Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                    //
                    NavUtils.navigateUpFromSameTask(activity!!)
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showToast(text: String, duration: Int) {
        activity!!.runOnUiThread {
            toast.setText(text)
            toast.duration = duration
            toast.show()
        }
    }

    fun showToast(text: String) {
        activity!!.runOnUiThread {
            toast.setText(text)
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }
    }

    internal var ad: RetrofitProgressDialog? = null

    fun showDialog(msg: String) {
        try {
            if (ad != null && ad!!.isShowing) {
                return
            }
            ad = RetrofitProgressDialog.getInstant(activity!!)
            ad!!.show(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setMessage(msg: String) {
        try {
            ad!!.setMessage(msg)
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


}
