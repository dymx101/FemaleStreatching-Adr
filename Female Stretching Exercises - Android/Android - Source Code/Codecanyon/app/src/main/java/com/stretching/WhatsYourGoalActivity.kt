package com.stretching

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.stretching.adapter.WhatsYourGoalAdapter
import com.stretching.databinding.ActivityWhatsYourGoalBinding
import com.stretching.utils.Constant
import com.stretching.utils.Utils


class WhatsYourGoalActivity : BaseActivity() {
    val TAG = WhatsYourGoalActivity::class.java.name + Constant.ARROW

    var binding: ActivityWhatsYourGoalBinding? = null
    var whatsYourGoalAdapter: WhatsYourGoalAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_whats_your_goal)
        init()
    }


    private fun init() {

        binding!!.mRecyclerView!!.setLayoutManager(LinearLayoutManager(this))
        whatsYourGoalAdapter = WhatsYourGoalAdapter(this)
        binding!!.mRecyclerView!!.setAdapter(whatsYourGoalAdapter)
        whatsYourGoalAdapter!!.setEventListener(object : WhatsYourGoalAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                whatsYourGoalAdapter!!.changeSelection(position)
                binding!!.btnNext.visibility = View.VISIBLE
            }

        })

        binding!!.btnNext.setOnClickListener {

            if (whatsYourGoalAdapter!!.getSelectedItem() != null) {
                Utils.setPref(
                    this,
                    Constant.PREF_WHATS_YOUR_GOAL,
                    whatsYourGoalAdapter!!.getSelectedItem()!!.name!!
                )
            }

            if (Utils.getPref(this, Constant.PREF_IS_REMINDER_SET, false)) {

                val i = Intent(this, HomeActivity::class.java)
                startActivity(i)

            } else {
                val i = Intent(this, SetReminderActivity::class.java)
                startActivity(i)
            }
        }

        addData()
    }

    private fun addData() {
        var list = arrayListOf<Goals>()

        list.add(Goals(getString(R.string.goal_warm_up), R.drawable.icon_warm_ups))
        list.add(Goals(getString(R.string.goal_cool_down), R.drawable.icon_cool_downs))
        list.add(Goals(getString(R.string.goal_pain_relief), R.drawable.icon_pain_relief))
        list.add(Goals(getString(R.string.goal_body_relax), R.drawable.icon_body_relax))
        list.add(
            Goals(
                getString(R.string.goal_posture_correction),
                R.drawable.icon_posture_correction
            )
        )

        whatsYourGoalAdapter!!.addAll(list)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    class Goals {
        constructor(name: String, img: Int) {
            this.name = name
            this.img = img
        }

        var name: String? = null
        var img: Int = 0
        var isSelected = false
    }
}
