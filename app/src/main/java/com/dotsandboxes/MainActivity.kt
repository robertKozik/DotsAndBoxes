package com.dotsandboxes

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.preference.PreferenceManager

class MainActivity : Activity() {
    var activityState: Int = R.string.MAIN_MENU
    var gameView: GameView? = null
    var yesterdayToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
    }

    fun start(view: View) {
        gameView = GameView(this)
        setContentView(gameView)
        activityState = R.string.GAME
    }

    fun settings(view: View) {
        activityState = R.string.SETTINGS

        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(R.layout.settings_layout)

        setEditText(preferences)
        setSeekBar(preferences)
        setSpinners(preferences)
    }

    private fun setSpinners(preferences: SharedPreferences) {
        val p1Color =
            preferences.getInt(resources.getString(R.string.PlayerOneColor), Color.rgb(255, 0, 0))
        val p2Color =
            preferences.getInt(resources.getString(R.string.PlayerTwoColor), Color.rgb(255, 255, 0))


        val p1spinnerListener = SpinnerActivity(this, 1)
        val spinnerPlayerOne: Spinner = findViewById(R.id.PlayerOneColor)
        spinnerPlayerOne.onItemSelectedListener = p1spinnerListener
        createSpinnerAdapter(spinnerPlayerOne)
        spinnerPlayerOne.setSelection(p1spinnerListener.getColorPosition(p1Color))

        val p2spinnerListener = SpinnerActivity(this, 2)
        val spinnerPlayerTwo: Spinner = findViewById(R.id.PlayerTwoColor)
        spinnerPlayerTwo.onItemSelectedListener = p2spinnerListener
        createSpinnerAdapter(spinnerPlayerTwo)
        spinnerPlayerTwo.setSelection(p2spinnerListener.getColorPosition(p2Color))

    }

    private fun setSeekBar(preferences: SharedPreferences) {
        val bar: SeekBar = findViewById(R.id.sizeBar)
        bar.progress = preferences.getInt(resources.getString(R.string.BoardSize), 4)
        bar.setOnSeekBarChangeListener(BoardSizeBarListener(this))
    }

    private fun setEditText(preferences: SharedPreferences) {
        val p1text: EditText = findViewById(R.id.PlayerOneTextEdit)
        p1text.setText(
            preferences.getString(
                resources.getString(R.string.PlayerOneNickname),
                "Player One"
            )
        )
        val p2text: EditText = findViewById(R.id.PlayerTwoTextEdit)
        p2text.setText(
            preferences.getString(
                resources.getString(R.string.PlayerTwoNickname),
                "Player Two"
            )
        )
    }

    fun exit(view: View) {
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        gameView?.createThread()
    }

    override fun onBackPressed() {
        if (activityState == R.string.MAIN_MENU) {
            if (yesterdayToast != null) {
                super.onBackPressed()
            } else {
                yesterdayToast = Toast.makeText(this, "Are you sure?", Toast.LENGTH_SHORT)
                yesterdayToast?.show()
            }
        } else if (activityState == R.string.SETTINGS) {
            getNickInput()
            activityState = R.string.MAIN_MENU
            setContentView(R.layout.main_menu_layout)
            // gameView?.thread?.join()
        } else {
            setContentView(R.layout.main_menu_layout)
            activityState = R.string.MAIN_MENU
            gameView?.thread?.join()
        }

    }

    private fun createSpinnerAdapter(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            this,
            R.array.colors_array,
            R.layout.fancy_spinner
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.fancy_spinner)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun getNickInput() {
        val inputPlayerOne = findViewById<EditText>(R.id.PlayerOneTextEdit)
        val inputPlayerTwo = findViewById<EditText>(R.id.PlayerTwoTextEdit)

        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putString(
            resources.getString(R.string.PlayerOneNickname),
            inputPlayerOne.text.toString()
        )
            .putString(
                resources.getString(R.string.PlayerTwoNickname),
                inputPlayerTwo.text.toString()
            )
            .apply()


    }
}