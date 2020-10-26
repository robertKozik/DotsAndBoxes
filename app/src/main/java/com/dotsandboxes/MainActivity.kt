package com.dotsandboxes

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.ContentView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity(): Activity() {
    var activityState: Int = R.string.MAIN_MENU
    var gameView: GameView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
    }

    fun start(view: View) {
        gameView = GameView(this)
        setContentView(gameView)
        activityState = R.string.GAME
    }

    override fun onResume() {
        super.onResume()
        //setContentView(R.layout.main_menu_layout)
        gameView?.createThread()
        }


}