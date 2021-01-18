package com.dotsandboxes

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*

class MainActivity(): Activity() {
    var activityState: Int = R.string.MAIN_MENU
    var gameView: GameView? = null
    var yesterdayToast : Toast? = null

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

        setContentView(R.layout.settings_layout)
        val bar: SeekBar = findViewById( R.id.sizeBar)
        bar.setOnSeekBarChangeListener( BoardSizeBarListener(this) )

        val spinnerPlayerOne: Spinner = findViewById(R.id.PlayerOneColor)
            spinnerPlayerOne.onItemSelectedListener = SpinnerActivity(this, 1);
            createSpinnerAdapter(spinnerPlayerOne)
        val spinnerPlayerTwo: Spinner = findViewById(R.id.PlayerTwoColor)
            spinnerPlayerTwo.onItemSelectedListener = SpinnerActivity(this, 2);
            createSpinnerAdapter(spinnerPlayerTwo)

    }

    fun exit(view: View) {
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        gameView?.createThread()
        }

    override fun onBackPressed() {
        if(activityState == R.string.MAIN_MENU) {
            if(yesterdayToast != null) {
                super.onBackPressed()
            } else {
                yesterdayToast = Toast.makeText(this, "Are you sure?", Toast.LENGTH_SHORT)
                yesterdayToast?.show()
            }
        } else if(activityState == R.string.SETTINGS) {
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
        editor.putString(resources.getString(R.string.PlayerOneNickname), inputPlayerOne.text.toString())
            .putString(resources.getString(R.string.PlayerTwoNickname), inputPlayerTwo.text.toString())
            .apply()


    }
}