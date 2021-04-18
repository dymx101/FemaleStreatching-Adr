package com.stretching.discover

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.drakeet.multitype.MultiTypeAdapter
import com.stretching.BaseActivity
import com.stretching.R
import com.stretching.databinding.ActivityDiscoverMultiTypeBinding
import com.stretching.interfaces.CallbackListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.utils.Utils
import java.util.*


class DiscoverMultitypeActivity : BaseActivity(), CallbackListener {

    var binding: ActivityDiscoverMultiTypeBinding? = null

    private val adapter = MultiTypeAdapter()
    private val items = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_discover_multi_type
        )

        initIntentParam()
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
        binding!!.topbar.tvTitleText.text = getString(R.string.menu_discover)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        adapter.register(TextItemViewBinder())
        adapter.register(ViewPagerViewBinder(this))
        binding!!.rvDiscover.adapter = adapter

        items.add(TextItem("Pain relief"))
        items.add(ViewPagerItem(""))

        adapter.items = items
        adapter.notifyDataSetChanged()

        fillData()

    }


    private fun fillData() {



    }

    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
        changeSelection(1)
    }


    inner class ClickHandler {

        fun onAddReminderClick(){
            /*showTimePickerDialog(this@DiscoverMultitypeActivity, Date(),object :DateEventListener{
                override fun onDateSelected(
                    date: Date,
                    hourOfDay: Int,
                    minute: Int
                ) {
                    showDaySelectionDialog()
                }
            })*/
        }
    }

    private fun showDaySelectionDialog() {

        val daysList = arrayOf<CharSequence>("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

        val builder = AlertDialog.Builder(getActivity(),
            R.style.MyAlertDialogStyle
        )
        builder.setCancelable(false)
        builder.setTitle(R.string.repeat)
        builder.setMultiChoiceItems(daysList, BooleanArray(7),object :DialogInterface.OnMultiChoiceClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {

            }
        })

        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
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
