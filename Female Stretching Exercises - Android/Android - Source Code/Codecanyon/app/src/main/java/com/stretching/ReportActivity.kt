package com.stretching

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.stretching.adapter.ReportWeekGoalAdapter
import com.stretching.databinding.ActivityReportBinding
import com.stretching.databinding.DialogWeightWithDateBinding
import com.stretching.interfaces.DialogDismissListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class ReportActivity : BaseActivity() {

    var binding: ActivityReportBinding? = null
    var reportWeekGoalAdapter: ReportWeekGoalAdapter? = null
    private var count = 0
    var avgAnnualWight = 0F

    private lateinit var daysText: ArrayList<String>
    private lateinit var daysYearText: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report)

        AdUtils.loadBannerAd(binding!!.adView,this)
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
        binding!!.topbar.tvTitleText.text = getString(R.string.menu_report)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        reportWeekGoalAdapter = ReportWeekGoalAdapter(this)
        binding!!.rvWeekGoal.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding!!.rvWeekGoal.setAdapter(reportWeekGoalAdapter)

        reportWeekGoalAdapter!!.setEventListener(object : ReportWeekGoalAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val i = Intent(this@ReportActivity, HistoryActivity::class.java)
                startActivity(i)
            }
        })

        initCommon()
    }

    private fun initCommon() {
        setupGraph()
        setWeightValues()
        setBmiCalculation()

        binding!!.tvWorkOuts.text = dbHelper.getHistoryTotalWorkout().toString()
        binding!!.tvCalorie.text = dbHelper.getHistoryTotalKCal().toInt().toString()
        binding!!.tvMinutes.text =
            ((dbHelper.getHistoryTotalMinutes() / 60).toDouble()).roundToInt().toString()
    }

    /* Todo set bmi calculation */
    private fun setBmiCalculation() {

        try {
            var lastWeight = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f)
            val lastFoot = Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0)
            val lastInch = Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F)

            val heightUnit = Utils.getPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_CM)

            if (heightUnit.equals(Constant.DEF_CM)) {
                val inch = Utils.ftInToInch(
                    lastFoot,
                    lastInch.toDouble()
                )
                binding!!.editCurrHeightCM.visibility = View.VISIBLE
                binding!!.editCurrHeightFT.visibility = View.GONE
                binding!!.editCurrHeightIn.visibility = View.GONE
                binding!!.editCurrHeightCM.setText(
                    Utils.inchToCm(inch).roundToInt().toDouble().toString()
                )
            } else {
                binding!!.editCurrHeightCM.visibility = View.GONE
                binding!!.editCurrHeightFT.visibility = View.VISIBLE
                binding!!.editCurrHeightIn.visibility = View.VISIBLE
                binding!!.editCurrHeightFT.setText(
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_FOOT, 0).toString()
                )
                binding!!.editCurrHeightIn.setText(
                    Utils.getPref(this, Constant.PREF_LAST_INPUT_INCH, 0F).toString()
                )
            }

            if (lastWeight != 0f && lastFoot != 0 && lastInch.toInt() != 0) {

                binding!!.clBMIGraphView.visibility = View.VISIBLE


                val bmiValue = Utils.getBmiCalculation(
                    lastWeight,
                    lastFoot,
                    lastInch.toInt()
                )

                val bmiVal = Utils.calculationForBmiGraph(bmiValue.toFloat())

                val param = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    bmiVal
                )

                binding!!.txtBmiGrade.text = Utils.truncateUptoTwoDecimal(bmiValue.toString())
                binding!!.tvBMI.text = Utils.truncateUptoTwoDecimal(bmiValue.toString())
                binding!!.tvWeightString.text = Utils.bmiWeightString(bmiValue.toFloat())
                binding!!.tvWeightString.setTextColor(
                    ColorStateList.valueOf(
                        Utils.bmiWeightTextColor(
                            this,
                            bmiValue.toFloat()
                        )
                    )
                )
                binding!!.blankView1.layoutParams = param

            } else {
                binding!!.clBMIGraphView.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /* Todo set weight values */
    private fun setWeightValues() {

        val lastWeight = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f)
        val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)

        val maxWeight = dbHelper.getMaxWeight().toFloat()
        val minWeight = dbHelper.getMinWeight().toFloat()

        try {

            if (weightUnit == Constant.DEF_KG && lastWeight != 0f) {
                binding!!.tvCurrentWeight.setText(Utils.truncateUptoTwoDecimal(lastWeight.toString()))
                binding!!.tvCurretUnit.text = weightUnit
                binding!!.tvHeaviestWeight.text =
                    Utils.truncateUptoTwoDecimal(maxWeight.toString()) + " " + Constant.DEF_KG
                binding!!.tvLightestWeight.text =
                    Utils.truncateUptoTwoDecimal(minWeight.toString()) + " " + Constant.DEF_KG

            } else if (weightUnit == Constant.DEF_LB && lastWeight != 0f) {
                binding!!.tvCurrentWeight.setText(
                    Utils.truncateUptoTwoDecimal(
                        Utils.kgToLb(
                            lastWeight.toDouble()
                        ).toFloat().toString()
                    )
                )
                binding!!.tvCurretUnit.text = weightUnit
                binding!!.tvHeaviestWeight.text = Utils.truncateUptoTwoDecimal(
                    Utils.kgToLb(maxWeight.toDouble()).toFloat().toString()
                ) + " " + Constant.DEF_LB
                binding!!.tvLightestWeight.text = Utils.truncateUptoTwoDecimal(
                    Utils.kgToLb(minWeight.toDouble()).toFloat().toString()
                ) + " " + Constant.DEF_LB
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setupGraph() {

        try {
            val format = SimpleDateFormat("dd/MM", Locale.getDefault())
            val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)

            calendar.time = formatDate.parse("$year-01-01")!!

            count = getIsLeapYear(year) + 1
            daysText = ArrayList()
            daysYearText = ArrayList()

            for (i in 0 until count) {
                daysText.add(format.format(calendar.time))
                daysYearText.add(formatDate.format(calendar.time))
                calendar.add(Calendar.DATE, 1)
            }

            binding!!.chartWeight.drawOrder = arrayOf(CombinedChart.DrawOrder.LINE)

            binding!!.chartWeight.description.isEnabled = false
            binding!!.chartWeight.description.text = "Date"
            binding!!.chartWeight.setNoDataText(resources.getString(R.string.app_name))
            binding!!.chartWeight.setBackgroundColor(Color.WHITE)
            binding!!.chartWeight.setDrawGridBackground(false)
            binding!!.chartWeight.setDrawBarShadow(false)
            binding!!.chartWeight.isHighlightFullBarEnabled = false

            val l = binding!!.chartWeight.legend
            l.isWordWrapEnabled = true
            l.textSize = 14f
            l.formSize = 15F
            l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)

            val leftAxis = binding!!.chartWeight.axisLeft
            leftAxis.setDrawGridLines(true)
            leftAxis.axisMaximum = 1000f
            leftAxis.axisMinimum = 0f
            leftAxis.granularity = 0.5f

            val rightAxis = binding!!.chartWeight.axisRight
            rightAxis.isEnabled = false

            val xAxis = binding!!.chartWeight.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = count.toFloat()
            xAxis.granularity = 1f
            xAxis.labelCount = 30

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value < daysText.size && value > 0) {
                        daysText[value.toInt()]
                    } else ""
                }
            }

            val data = CombinedData()
            data.setData(generateLineData())

            if (avgAnnualWight > 0) {
                binding!!.chartWeight.getAxisLeft().removeAllLimitLines()
                val ll = LimitLine(avgAnnualWight, "")
                ll.lineColor = Color.rgb(181, 129, 189)
                ll.lineWidth = 2f
                binding!!.chartWeight.getAxisLeft().addLimitLine(ll)

                val legendEntryA = LegendEntry()
                legendEntryA.label = "Annual Average"
                legendEntryA.formColor = Color.rgb(181, 129, 189)
                l.setExtra(listOf(legendEntryA))
            }

            data.setValueTypeface(Typeface.DEFAULT)
            binding!!.chartWeight.data = data

            binding!!.chartWeight.setVisibleXRange(5f, 8f)
            binding!!.chartWeight.setVisibleYRange(4f, 10F, YAxis.AxisDependency.LEFT)

            val strDate = Utils.parseTime(Date().time, "yyyy-MM-dd")

            val xPos = daysYearText.indexOf(strDate)
            var yPos = 0f
            val lastWeight = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f)
            val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)

            yPos = if (weightUnit == Constant.DEF_KG) {
                lastWeight
            } else {
                Utils.kgToLb(lastWeight.toDouble()).toFloat()
            }

            binding!!.chartWeight.centerViewTo(xPos.toFloat(), yPos, YAxis.AxisDependency.LEFT)

            setGraphTouch()

            binding!!.chartWeight.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setGraphTouch() {

        binding!!.chartWeight.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    binding!!.chartWeight.parent.requestDisallowInterceptTouchEvent(true)
            }
            false
        }

    }

    private fun generateLineData(): LineData {

        val yAxisData = dbHelper.getUserWeightData()
        val d = LineData()

        val entries = ArrayList<Entry>()
        if (yAxisData.size > 0) {
            try {
                var totalWeight = 0f
                for (index in 0 until yAxisData.size) {
                    //            yAxisData[index]["KG"]
                    val strDate = yAxisData[index]["DT"]
                    val position = daysYearText.indexOf(strDate)
                    val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)
                    if (weightUnit == Constant.DEF_KG) {
                        totalWeight += yAxisData[index]["KG"]!!.toFloat()
                        entries.add(Entry(position.toFloat(), yAxisData[index]["KG"]!!.toFloat()))
                    } else {
                        totalWeight += Utils.kgToLb(yAxisData[index]["KG"]!!.toDouble()).toFloat()
                        entries.add(
                            Entry(
                                position.toFloat(),
                                Utils.kgToLb(yAxisData[index]["KG"]!!.toDouble()).toFloat()
                            )
                        )
                    }
                }
                avgAnnualWight = totalWeight.div(yAxisData.size)
                var firstWeight = yAxisData[0]["KG"]!!.toFloat()
                var lastWeight = yAxisData[yAxisData.lastIndex]["KG"]!!.toFloat()
                val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)
                var diffrence = lastWeight - firstWeight

                if (weightUnit == Constant.DEF_KG) {
                    binding!!.tvAvgWeight.text =
                        String.format("%.2f", diffrence).plus(" ${Constant.DEF_KG}")
                } else {
                    binding!!.tvAvgWeight.text =
                        String.format("%.2f", Utils.kgToLb(diffrence.toDouble()))
                            .plus(" ${Constant.DEF_LB}")
                }


                if (diffrence < 0) {
                    binding!!.tvAvgWeight.backgroundTintList = ColorStateList.valueOf(
                        Color.rgb(0, 192, 98)
                    )
                } else {
                    binding!!.tvAvgWeight.backgroundTintList = ColorStateList.valueOf(
                        Color.rgb(246, 176, 39)
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                val lastWeight = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f)
                val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)
                if (lastWeight > 0) {
                    val strDate = Utils.parseTime(Date().time, Constant.DATE_FORMAT)
                    val position = daysYearText.indexOf(strDate)
                    if (weightUnit == Constant.DEF_KG) {
                        entries.add(Entry(position.toFloat(), lastWeight))
                    } else {
                        entries.add(
                            Entry(
                                position.toFloat(),
                                Utils.kgToLb(lastWeight.toDouble()).toFloat()
                            )
                        )
                    }
                }
                if (weightUnit == Constant.DEF_KG) {
                    binding!!.tvAvgWeight.text =
                        String.format("%.2f", lastWeight).plus(" ${Constant.DEF_KG}")
                } else {
                    binding!!.tvAvgWeight.text =
                        String.format("%.2f", Utils.kgToLb(lastWeight.toDouble()))
                            .plus(" ${Constant.DEF_LB}")
                }
                binding!!.tvAvgWeight.backgroundTintList = ColorStateList.valueOf(
                    Color.rgb(246, 176, 39)
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val set = LineDataSet(entries, "Date")
        set.color = Color.rgb(246, 176, 39)
        set.lineWidth = 2f
        set.circleHoleRadius = 0f
        set.circleHoleColor = Color.rgb(246, 176, 39)
        set.setCircleColor(Color.rgb(246, 176, 39))
        set.circleRadius = 5f

//        set.fillColor = Color.rgb(130, 130, 130)

        set.fillColor = Color.rgb(246, 176, 39)
        set.mode = LineDataSet.Mode.LINEAR
        set.setDrawValues(false)
        set.valueTextSize = 15f
        set.valueTextColor = Color.rgb(246, 176, 39)

        set.axisDependency = YAxis.AxisDependency.LEFT
        d.addDataSet(set)

        return d
    }

    override fun onResume() {
        super.onResume()
        changeSelection(3)
    }

    private fun getIsLeapYear(year: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR)
    }


    inner class ClickHandler {

        fun onAddWeightClick() {
            showAddWeightByDate()
        }

        fun onEditBMIClick() {
            showHeightWeightDialog(object : DialogDismissListener {
                override fun onDialogDismiss() {
                    setupGraph()
                    setBmiCalculation()
                    setWeightValues()
                }

            })
        }

        fun onRecordsClick() {
            val i = Intent(this@ReportActivity, HistoryActivity::class.java)
            startActivity(i)
        }

    }

    private fun showAddWeightByDate() {

        var boolKg: Boolean = true
        var dateSelect: String = Utils.parseTime(Date().time, Constant.WEIGHT_TABLE_DATE_FORMAT)


        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        val v: View = (this).getLayoutInflater()
            .inflate(R.layout.dialog_weight_with_date, null)
        val dialogBinding: DialogWeightWithDateBinding? = DataBindingUtil.bind(v)
        builder.setView(v)
        dialogBinding!!.editWeight.requestFocus()
        dialogBinding!!.editWeight.setSelectAllOnFocus(true)
        val lastWeight = Utils.getPref(this, Constant.PREF_LAST_INPUT_WEIGHT, 0f)
        val weightUnit = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)

        if (weightUnit == Constant.DEF_KG && lastWeight != 0f) {
            dialogBinding!!.tvKG.isSelected = true
            dialogBinding!!.tvLB.isSelected = false
            dialogBinding!!.editWeight.setText(lastWeight.toString())

            dialogBinding!!.tvKG.background = resources.getDrawable(R.color.green_text, null)
            dialogBinding!!.tvLB.background = resources.getDrawable(R.color.gray_light_, null)
        } else if (weightUnit == Constant.DEF_LB && lastWeight != 0f) {
            dialogBinding!!.tvKG.isSelected = false
            dialogBinding!!.tvLB.isSelected = true
            boolKg = false
            dialogBinding!!.editWeight.setText(
                    Utils.kgToLb(lastWeight.toDouble()).toFloat().toString()
            )

            dialogBinding!!.tvKG.background = resources.getDrawable(R.color.gray_light_, null)
            dialogBinding!!.tvLB.background = resources.getDrawable(R.color.green_text, null)
        } else {
            dialogBinding!!.tvKG.isSelected = true
            dialogBinding!!.tvLB.isSelected = false
            dialogBinding!!.tvKG.background = resources.getDrawable(R.color.green_text, null)
            dialogBinding!!.tvLB.background = resources.getDrawable(R.color.gray_light_, null)
        }
        dialogBinding.editWeight.setOnClickListener {
            if (dialogBinding.editWeight.text.toString().equals("0.0")){
                dialogBinding.editWeight.setText("")
            }else{
                dialogBinding.editWeight.setSelectAllOnFocus(false)
            }
        }
        dialogBinding!!.tvKG.setOnClickListener {
            try {
                if (!boolKg) {
                    boolKg = true

                    dialogBinding!!.tvKG.isSelected = true
                    dialogBinding!!.tvLB.isSelected = false

                    dialogBinding!!.tvKG.background =
                        resources.getDrawable(R.color.green_text, null)
                    dialogBinding!!.tvLB.background =
                        resources.getDrawable(R.color.gray_light_, null)

                    dialogBinding!!.editWeight.hint = Constant.DEF_KG

                    if (dialogBinding!!.editWeight.text.toString() != "") {
                        dialogBinding!!.editWeight.setText(
                            Utils.lbToKg(
                                dialogBinding!!.editWeight.text.toString().toDouble()
                            ).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dialogBinding!!.tvLB.setOnClickListener {
            try {
                if (boolKg) {
                    boolKg = false

                    dialogBinding!!.tvKG.isSelected = false
                    dialogBinding!!.tvLB.isSelected = true

                    dialogBinding!!.tvLB.background =
                        resources.getDrawable(R.color.green_text, null)
                    dialogBinding!!.tvKG.background =
                        resources.getDrawable(R.color.gray_light_, null)

                    dialogBinding!!.editWeight.hint = Constant.DEF_LB

                    if (dialogBinding!!.editWeight.text.toString() != "") {
                        dialogBinding!!.editWeight.setText(
                            Utils.kgToLb(
                                dialogBinding!!.editWeight.text.toString().toDouble()
                            ).toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dialogBinding!!.dtpWeightSet
            .setDays(369)
            .setOffset(365)
            .setListener { dateSelected ->
                dateSelect =
                    Utils.parseTime(dateSelected.toDate().time, Constant.WEIGHT_TABLE_DATE_FORMAT)
                if (dbHelper.weightExistOrNot(dateSelect)) {
                    val weight = dbHelper.getWeightForDate(dateSelect)

                    if (dialogBinding!!.tvKG.isSelected) {
                        boolKg = true

                        dialogBinding!!.tvKG.isSelected = true
                        dialogBinding!!.tvLB.isSelected = false

                        dialogBinding!!.tvKG.background =
                            resources.getDrawable(R.color.green_text, null)
                        dialogBinding!!.tvLB.background =
                            resources.getDrawable(R.color.gray_light_, null)
                        dialogBinding!!.editWeight.setText(Utils.truncateUptoTwoDecimal(weight))
                    } else {
                        boolKg = false
                        dialogBinding!!.tvKG.isSelected = false
                        dialogBinding!!.tvLB.isSelected = true

                        dialogBinding!!.tvLB.background =
                            resources.getDrawable(R.color.green_text, null)
                        dialogBinding!!.tvKG.background =
                            resources.getDrawable(R.color.gray_light_, null)
                        dialogBinding!!.editWeight.setText(
                                Utils.kgToLb(
                                    weight.toDouble()
                                ).toString()
                        )
                    }
                } else {
                    dialogBinding!!.editWeight.setText("0.0")
                }
//                Toast.makeText(context, "Selected date is ${DateUtils.getDate(dateSelect.toDate().time, Locale.getDefault())}", Toast.LENGTH_SHORT).show()
            }
            .showTodayButton(false)
            .init()

        dialogBinding!!.dtpWeightSet.setDate(DateTime.now())

        builder.setPositiveButton(R.string.save) { dialog, which ->
            try {
                val strKG: Float
                var strUnit: String
                val date = Utils.parseTime(dateSelect, Constant.WEIGHT_TABLE_DATE_FORMAT)
                val currDate = Utils.parseTime(Date(), Constant.WEIGHT_TABLE_DATE_FORMAT)
                if (boolKg) {
                    strKG = dialogBinding.editWeight.text.toString().toFloat()
                    strUnit = Constant.DEF_KG
                } else {
                    strKG =
                        Utils.lbToKg(dialogBinding.editWeight.text.toString().toDouble())
                            .roundToInt().toFloat()
                    strUnit = Constant.DEF_LB
                }

                if (date >= currDate) {
                    Utils.setPref(this, Constant.PREF_WEIGHT_UNIT, strUnit)
                    Utils.setPref(this, Constant.PREF_LAST_INPUT_WEIGHT, strKG)
                }


                if (dbHelper.weightExistOrNot(dateSelect)) {
                    dbHelper.updateWeight(dateSelect, strKG.toString(), "")
                } else {
                    dbHelper.addUserWeight(strKG.toString(), dateSelect, "")
                }

                setupGraph()
                setWeightValues()
                setBmiCalculation()

                dialog.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

}
