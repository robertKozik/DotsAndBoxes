package com.dotsandboxes

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(var surfaceHolder: SurfaceHolder, var gameView: GameView) : Thread() {
    var running: Boolean = false
    var canvas: Canvas? = null


    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        val targetFPS: Long = 60
        val targetTime: Long = 1000 / targetFPS

        while (running) {
            startTime = System.nanoTime()
            canvas = Canvas()

            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    this.gameView.update()
                    this.gameView.draw(canvas)
                }
            } catch (exception: Exception) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = targetTime - timeMillis

            try {
                sleep(waitTime)
            } catch (exception: Exception) {
            }


        }


    }


}