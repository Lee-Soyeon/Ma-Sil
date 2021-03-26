package com.gsc.silverwalk.ui.history

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.gsc.silverwalk.MyMapViewAsync
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.activity_history.*


class HistoryActivity : AppCompatActivity() {

    private lateinit var historyViewModel: HistoryViewModel

    val labels = arrayOf("50s", "60s", "70s")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        history_mapview.onCreate(savedInstanceState)

        supportActionBar?.hide()

        historyViewModel = ViewModelProvider(this, HistoryViewModelFactory())
            .get(HistoryViewModel::class.java)

        historyViewModel.achievementHistoryItem.observe(this, Observer {
            val achievementHistoryItem = it ?: return@Observer

            // Statistics
            history_steps_text.setText(achievementHistoryItem.steps.toString())
            history_averagepace_text.setText(achievementHistoryItem.averagePace.toString())
            history_calories_text.setText(achievementHistoryItem.calories.toString())
            history_heartrate_text.setText(achievementHistoryItem.heartRate.toString())
            history_distance_text.setText(achievementHistoryItem.getDistanceString())
            history_time_text.setText(achievementHistoryItem.walkTimeToStringFormatHHMM())

            // Map View
            history_mapview.getMapAsync(
                MyMapViewAsync(achievementHistoryItem.location.toString(),this))

            // Title
            history_title_1_text.setText(
                getString(R.string.history_activity_title_1)
                    .replace("(%location)", achievementHistoryItem.location.toString())
            )

            // Display Text
            history_display_1_text.setText(
                history_display_1_text.text.toString().replace(
                    "(%location)", achievementHistoryItem.location.toString()
                )
            )
            history_display_2_text.setText(
                history_display_2_text.text.toString().replace(
                    "(%location)", achievementHistoryItem.location.toString()
                )
            )
            history_display_3_text.setText(
                history_display_3_text.text.toString().replace(
                    "(%location)", achievementHistoryItem.location.toString()
                )
            )
            history_display_4_text.setText(
                history_display_4_text.text.toString().replace(
                    "(%location)", achievementHistoryItem.location.toString()
                )
            )
            history_display_5_text.setText(
                history_display_5_text.text.toString().replace(
                    "(%location)", achievementHistoryItem.location.toString()
                )
            )

            historyViewModel.getLocationStatistics(achievementHistoryItem.location!!)
        })

        historyViewModel.historyForm.observe(this, Observer {
            val historyForm = it ?: return@Observer

            // Level Count
            history_level_easy_count_text.setText(historyForm.totalEasyCount.toString())
            history_level_moderate_count_text.setText(historyForm.totalModerateCount.toString())
            history_level_hard_count_text.setText(historyForm.totalHardCount.toString())

            // Gender Count
            history_statistics_gender_women_button.setText(historyForm.getWomenPercentString())
            history_statistics_gender_men_button.setText(historyForm.getMenPercentString())

            // Display Text
            history_display_1_text.setText(
                history_display_1_text.text.toString().replace(
                    "(%count)", historyForm.totalHumanCount.toString()
                )
            )
            history_display_2_text.setText(
                history_display_2_text.text.toString().replace(
                    "(%count)", historyForm.totalHumanCount.toString()
                )
            )
            history_display_3_text.setText(
                history_display_3_text.text.toString().replace(
                    "(%count)", historyForm.totalHumanCount.toString()
                )
            )
            history_display_4_text.setText(
                history_display_4_text.text.toString().replace(
                    "(%count)", historyForm.totalHumanCount.toString()
                )
            )
            history_display_5_text.setText(
                history_display_5_text.text.toString().replace(
                    "(%count)", historyForm.totalHumanCount.toString()
                )
            )

            // Age Range Chart
            val entries = arrayListOf<BarEntry>()
            entries.add(BarEntry(0.0f, historyForm.total50sCount!!.toFloat()))
            entries.add(BarEntry(1.0f, historyForm.total60sCount!!.toFloat()))
            entries.add(BarEntry(2.0f, historyForm.total70sCount!!.toFloat()))

            val dataSet = BarDataSet(entries, "Label")
            dataSet.setColors(
                intArrayOf(
                    getColor(R.color.blue),
                    getColor(R.color.blue_60),
                    getColor(R.color.blue_70)
                ), 255
            )

            val barData = BarData(dataSet)
            barData.barWidth = 0.25f
            barData.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value.toInt() == 0) historyForm.get50sPercentString() else
                        if (value.toInt() == 1) historyForm.get60sPercentString() else
                            historyForm.get70sPercentString()
                }
            })

            barData.setValueTextColors(
                listOf(
                    getColor(R.color.blue),
                    getColor(R.color.blue_60),
                    getColor(R.color.blue_70)
                )
            )
            barData.setValueTextSize(20f)

            history_age_chart.data = barData
            history_age_chart.extraBottomOffset = 10f
            history_age_chart.setFitBars(true)
            history_age_chart.invalidate()
        })

        initChart()

        history_top_button.setOnClickListener(View.OnClickListener {
            history_root_scrollview.scrollTo(0, 0)
        })

        historyViewModel.getIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        history_mapview.onResume()
    }

    override fun onPause() {
        super.onPause()
        history_mapview.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initChart() {
        history_age_chart.run {
            description.isEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)

            axisLeft.axisMinimum = 0f

            xAxis.setDrawGridLines(false)
            xAxis.labelCount = 3
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textSize = 20f
            xAxis.textColor = getColor(R.color.blue)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return labels[value.toInt()]
                }
            }

            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            setTouchEnabled(false)
            animateY(1000)

            legend.isEnabled = false
        }
    }
}