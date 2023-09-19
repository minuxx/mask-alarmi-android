package com.minux.mask_alarmi.ui.main

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.ui.main.map.MapFragment
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private lateinit var tvAnnounce: TextView
    private lateinit var tvPossibleDay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_fragment_container, MapFragment.newInstance())
                .commit()
        }

        tvAnnounce = findViewById(R.id.main_tv_announce)
        tvPossibleDay = findViewById(R.id.main_tv_possible_day)
        tvAnnounce.isSelected = true
        setPossibleDay()
    }

    private fun setPossibleDay() {
        val cal: Calendar = Calendar.getInstance()
        when (val weekNum: Int = cal.get(Calendar.DAY_OF_WEEK)) {
            1, 7 -> tvPossibleDay.text = getString(R.string.main_possible_weekend)
            else -> {
                val first = (weekNum - 1) % 10
                val second = (first + 5) % 10
                val possibleDayStr = getString(R.string.main_possible_day, first, second)
                tvPossibleDay.text = SpannableStringBuilder(possibleDayStr).apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.mainPossibleYearText)),
                        9,
                        14,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }
}