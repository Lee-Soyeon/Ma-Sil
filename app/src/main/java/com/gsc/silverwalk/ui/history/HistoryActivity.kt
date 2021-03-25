package com.gsc.silverwalk.ui.history

import android.R.attr.button
import android.location.Address
import android.location.Geocoder
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.view.*
import java.io.IOException


class HistoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var historyViewModel: HistoryViewModel
    private var mMap: GoogleMap? = null
    private var geocoder: Geocoder? = null
    private var history_location: String? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

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

            history_location = achievementHistoryItem.location.toString()

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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.history_mapview) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    val labels = arrayOf("50s", "60s", "70s")

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        geocoder = Geocoder(this)

        val str: String? = history_location
        var addressList: List<Address>? = null
        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder!!.getFromLocationName(
                str,  // 주소
                10
            ) // 최대 검색 결과 개수
        } catch (e: IOException) {
            e.printStackTrace()
        }
        System.out.println(addressList!![0].toString())
        // 콤마를 기준으로 split
        val splitStr: List<String> = addressList!![0].toString().split(",")
        val address = splitStr[0].substring(
            splitStr[0].indexOf("\"") + 1,
            splitStr[0].length - 2
        ) // 주소
        println(address)
        val latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1) // 위도
        val longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1) // 경도
        println(latitude)
        println(longitude)

        // 좌표(위도, 경도) 생성
        val point = LatLng(latitude.toDouble(), longitude.toDouble())
        // 마커 생성
        val mOptions = MarkerOptions()
        mOptions.title(history_location)
        mOptions.snippet(address)
        mOptions.position(point)
        // 마커 추가
        mMap!!.addMarker(mOptions)
        // 해당 좌표로 화면 줌
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15f))
    }
}