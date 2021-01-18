package com.dotsandboxes

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import kotlin.random.Random


/**
 * Spinner listener that changes board size in SharedPrefferences after change in SeekBar object
 */
class BoardSizeBarListener(private val activity: MainActivity) : SeekBar.OnSeekBarChangeListener {
    private val text: TextView = activity.findViewById(R.id.currentBoardSize)
    private val editor: SharedPreferences.Editor =
        PreferenceManager.getDefaultSharedPreferences(activity).edit()

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        text.text = p1.toString()
        editor.putInt(activity.resources.getString(R.string.BoardSize), p1).apply()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

}

/**
 * Spinner listener that changes color value in sharedPreferences
 */
class SpinnerActivity(private val activity: MainActivity, val player: Int) : Activity(),
    AdapterView.OnItemSelectedListener {
    private val editor: SharedPreferences.Editor =
        PreferenceManager.getDefaultSharedPreferences(activity.applicationContext).edit()

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val selected = parent.getItemAtPosition(pos) as String
        if (player == 1) {
            editor.putInt(activity.resources.getString(R.string.PlayerOneColor), getColor(selected))
        } else {
            editor.putInt(activity.resources.getString(R.string.PlayerTwoColor), getColor(selected))
        }
        editor.apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }


    /**
     * function returns int value of desired color
     * @param color string name of desired color
     * @return int value of color
     */
    private fun getColor(color: String): Int {
        when (color) {
            "Red" -> return ResourcesCompat.getColor(activity.resources, R.color.Red, null)

            "Green" -> return ResourcesCompat.getColor(activity.resources, R.color.Green, null)

            "Orange" -> return ResourcesCompat.getColor(activity.resources, R.color.Orange, null)

            "Yellow" -> return ResourcesCompat.getColor(activity.resources, R.color.Yellow, null)

            "Pink" -> return ResourcesCompat.getColor(activity.resources, R.color.Pink, null)

            "Violet" -> return ResourcesCompat.getColor(activity.resources, R.color.Violet, null)

            "Blue" -> return ResourcesCompat.getColor(activity.resources, R.color.Blue, null)
        }
        return Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255))
    }

    fun getColorPosition(color: Int): Int {
        when (color) {
            getColor("Red") -> return 0

            getColor("Green") -> return 1

            getColor("Orange") -> return 2

            getColor("Yellow") -> return 3

            getColor("Pink") -> return 4

            getColor("Violet") -> return 5

            getColor("Blue") -> return 6
        }
        return -1
    }
}