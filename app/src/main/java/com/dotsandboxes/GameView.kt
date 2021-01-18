package com.dotsandboxes

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.preference.PreferenceManager

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private val dimensions = ScreenDimensions(context)
    private val myPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val game =
        GameModel(myPreferences.getInt(context.resources.getString(R.string.BoardSize), 4), context)
    private val gameController = GameController(game, dimensions)
    lateinit var thread: GameThread
    //private val rectangle: Rectangle = Rectangle(dimensions)

    init {
        createThread()
        focusable = View.FOCUSABLE
        holder.addCallback(this)
    }

    fun createThread() {
        thread = GameThread(holder, this)
    }

    fun update() {
        //rectangle.updateElementPosition()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            canvas.drawColor(Color.BLACK)
            //drawBoard(canvas)
            //rectangle.drawMovingRectangle(canvas)
            drawTurnIndicator(canvas)
            drawLines(canvas)
            drawSquares(canvas)
            drawNodes(canvas)
            drawName(canvas)
            drawScore(canvas)
            if (!game.gameState)
                itsEndGameNow(canvas)
        }
    }

    private fun drawSquares(canvas: Canvas) {
        game.squaresClosed.forEach { square ->
            run {
                val paint: Paint = Paint()
                paint.color = Color.rgb(255, 255, 255)//white
                paint.strokeWidth = 8F
                var position = getNodePosition(square)
                canvas.drawLine(
                    position[0],
                    position[1],
                    position[0] + dimensions.nodeToNodeDistance,
                    position[1] - dimensions.nodeToNodeDistance,
                    paint
                )
                canvas.drawLine(
                    position[0] + dimensions.nodeToNodeDistance,
                    position[1],
                    position[0],
                    position[1] - dimensions.nodeToNodeDistance,
                    paint
                )
            }
        }
    }

    private fun drawLines(canvas: Canvas) {
        var paint: Paint?
        for (player in game.players) {
            paint = player.paint
            player.lines.forEach { line ->
                run {
                    val firstPointPosition: Array<Float> = getNodePosition(line.begin)
                    val secondPointPosition: Array<Float> = getNodePosition(line.end)
                    canvas.drawLine(
                        firstPointPosition[0],
                        firstPointPosition[1],
                        secondPointPosition[0],
                        secondPointPosition[1],
                        paint
                    )
                }
            }
        }
    } //OK

    private fun drawTurnIndicator(canvas: Canvas) {
        var paint: Paint = game.players[game.turn % 2].paint
        canvas.drawRoundRect(
            RectF(
                0F, 0F,
                dimensions.screenWidth.toFloat(), dimensions.screenHeight.toFloat()
            ), dimensions.indicatorRadius, dimensions.indicatorRadius, paint
        )
        paint = Paint()
        paint.color = Color.rgb(0, 0, 0)
        canvas.drawRoundRect(
            RectF(
                dimensions.indicatorWidth, dimensions.indicatorWidth,
                dimensions.screenWidth.toFloat() - 6, dimensions.screenHeight.toFloat() - 6
            ), dimensions.indicatorRadius, dimensions.indicatorRadius, paint
        )
    } //OK

    private fun drawScore(canvas: Canvas) {

        var xPos = dimensions.scoreXPosition
        var yPos = dimensions.scoreYPosition
        canvas.drawText(game.players[0].score.toString(), xPos, yPos, game.players[0].paint)

        xPos = dimensions.screenWidth - dimensions.scoreXPosition
        canvas.drawText(game.players[1].score.toString(), xPos, yPos, game.players[1].paint)
    } //OK

    private fun drawName(canvas: Canvas) {
        val paint = Paint()
        paint.color = game.players[game.turn % 2].paint.color
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 80F

        canvas.drawText(
            game.players[game.turn % 2].name,
            dimensions.nameXPosition,
            dimensions.nameYPosition,
            paint
        )
    } //OK

    private fun drawNodes(canvas: Canvas) {
        for (row in game.mapPoint) {
            for (node in row) {
                canvas.drawCircle(
                    dimensions.horizontalMargain.toFloat() + (node.xCoodinate * dimensions.nodeToNodeDistance),
                    dimensions.verticalMargain.toFloat() + (node.yCoordinate * dimensions.nodeToNodeDistance),
                    node.pointRadius,
                    node.paint
                )
            }
        }

    } //OK

    private fun getNodePosition(node: Point): Array<Float> {
        return arrayOf(
            dimensions.horizontalMargain.toFloat() + (node.xCoodinate * dimensions.nodeToNodeDistance),
            dimensions.verticalMargain.toFloat() + (node.yCoordinate * dimensions.nodeToNodeDistance)
        )
    } //OK

    private fun itsEndGameNow(canvas: Canvas) {
        var paint: Paint? = game.winner?.paint
        val name: String? = game.winner?.name

        if (paint != null) {
            canvas.drawRoundRect(
                RectF(
                    dimensions.horizontalMargain.toFloat(),
                    dimensions.screenHeight / 2 + 200F,
                    dimensions.screenWidth - dimensions.horizontalMargain.toFloat(),
                    dimensions.screenHeight / 2 - 200F
                ),
                50F, 50F, paint
            )
        }
        paint = Paint()
        paint.color = Color.rgb(0, 0, 0)
        canvas.drawRoundRect(
            RectF(
                dimensions.horizontalMargain.toFloat() + dimensions.messageBorderWidth,
                dimensions.screenHeight / 2 + 195F,
                dimensions.screenWidth - dimensions.horizontalMargain.toFloat() - dimensions.messageBorderWidth,
                dimensions.screenHeight / 2 - 195F
            ),
            50F, 50F, paint
        )

        //Text
        paint.color = game.winner?.paint?.color!!
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = dimensions.textSize

        val xPos = dimensions.screenWidth.toFloat() / 2
        val yPos = dimensions.screenHeight / 2 - 40F

        canvas.drawText("WINNER!", xPos, yPos, paint)
        canvas.drawText(name!!, xPos, yPos + 120F, paint)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.running = false
                thread.join()
            } catch (exception: InterruptedException) {
                exception.printStackTrace()
            }
            retry = false
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread.running = true
        thread.start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gameController.actionPerformed(event)
    }
}

data class ScreenDimensions(val context: Context) {

    val screenHeight: Int = context.resources.displayMetrics.heightPixels
    val screenWidth: Int = context.resources.displayMetrics.widthPixels
    val horizontalMargain: Int = 200
    val boardSize: Int = screenWidth - 2 * horizontalMargain
    val verticalMargain: Int = (screenHeight - boardSize) / 2
    val nodeToNodeDistance = boardSize / (PreferenceManager.getDefaultSharedPreferences(context)
        .getInt(context.resources.getString(R.string.BoardSize), 4) - 1)


    val scoreYPosition = 160F
    val scoreXPosition = 100F

    val indicatorRadius = 80F
    val indicatorWidth = 6F
    val lineStrokeWidth = 8F

    val nameYPosition = 80F
    val nameXPosition = screenWidth / 2F

    val messageBorderWidth = 5F

    val textSize = 80F

}