package com.dotsandboxes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.preference.PreferenceManager
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import kotlin.random.Random


class GameView2(context: Context): SurfaceView(context), SurfaceHolder.Callback{
    private val dimensions2: ScreenDimensions2 = ScreenDimensions2(context)
    val myPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    lateinit var thread: GameThread
    private var game: Game = Game(myPreferences.getInt(context.resources.getString(R.string.BoardSize), 4), context)
    private val nodeToNodeDistance = dimensions2.boardSize/(game.mapSize-1)
    private val rectangle: Rectangle = Rectangle(dimensions2)

    init {
        createThread()
        focusable = View.FOCUSABLE
        holder.addCallback(this)
    }

    fun createThread(){
        thread = GameThread(holder, this)
    }

    fun update() {
        rectangle.updateElementPosition()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            canvas.drawColor(Color.BLACK)
            //drawBoard(canvas)
            drawTurnIndicator(canvas)
            rectangle.drawMovingRectangle(canvas)
            drawLines(canvas)
            drawSquares(canvas)
            drawNodes(canvas)
            drawName(canvas)
            drawScore(canvas)
            if(!game.gameState)
                itsEndGameNow(canvas)
        }
    }

    private fun drawSquares(canvas: Canvas) {
        game.squaresClosed.forEach{ square ->
            run {
                val paint: Paint = Paint()
                paint.color = Color.rgb(255, 255, 255)//white
                paint.strokeWidth = 8F
                var position = getNodePosition(square)
                canvas.drawLine(
                    position[0],
                    position[1],
                    position[0] + nodeToNodeDistance,
                    position[1] - nodeToNodeDistance,
                    paint
                )
                canvas.drawLine(
                    position[0] + nodeToNodeDistance,
                    position[1],
                    position[0],
                    position[1] - nodeToNodeDistance,
                    paint
                )
            }
        }
    }

    private fun drawLines(canvas: Canvas) {
        var paint: Paint?
        for(player in game.players) {
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
    }

    private fun drawTurnIndicator(canvas: Canvas) {
        var paint: Paint = game.players[game.turn % 2].paint
        canvas.drawRoundRect(
            RectF(
                0F, 0F,
                dimensions2.screenWidth.toFloat(), dimensions2.screenHeight.toFloat()
            ), 80F, 80F, paint
        )
        paint = Paint()
        paint.color = Color.rgb(0, 0, 0)
        canvas.drawRoundRect(
            RectF(
                6F, 6F,
                dimensions2.screenWidth.toFloat() - 6, dimensions2.screenHeight.toFloat() - 6
            ), 80F, 80F, paint
        )
    }

    private fun drawScore(canvas: Canvas){

        var xPos = 100F
        var yPos = 160F
        canvas.drawText(game.players[0].score.toString(), xPos, yPos, game.players[0].paint);

        xPos = dimensions2.screenWidth - 100F
        canvas.drawText(game.players[1].score.toString(), xPos, yPos, game.players[1].paint);
    }

    private fun drawName(canvas: Canvas){
        val paint: Paint = Paint()
        paint.color = game.players[game.turn % 2].paint.color
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 80F

        val xPos = dimensions2.screenWidth.toFloat()/2
        val yPos = 80F

        canvas.drawText(game.players[game.turn % 2].name, xPos, yPos, paint);
    }

    private fun drawNodes(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.rgb(0, 255, 0)
        for(row in game.mapPoint){
            for( node in row){
                canvas.drawCircle(
                    dimensions2.horizontalMargain.toFloat() + (node.xCoodinate * nodeToNodeDistance),
                    dimensions2.verticalMargain.toFloat() + (node.yCoordinate * nodeToNodeDistance),
                    node.pointRadius,
                    node.paint
                )
            }
        }

    }

    private fun getNodePosition(node: Point): Array<Float> {
        return arrayOf(
            dimensions2.horizontalMargain.toFloat() + (node.xCoodinate * nodeToNodeDistance),
            dimensions2.verticalMargain.toFloat() + (node.yCoordinate * nodeToNodeDistance)
        )
    }

    private fun actionDown(x: Float, y: Float): Boolean {
        val touchTolerance: Float = (dimensions2.boardSize/(game.mapSize*2)).toFloat()
        for(row in game.mapPoint) {
            for (node in row) {
                if( x > dimensions2.horizontalMargain.toFloat()+(node.xCoodinate*nodeToNodeDistance) - touchTolerance
                    && x < dimensions2.horizontalMargain.toFloat()+(node.xCoodinate*nodeToNodeDistance) + touchTolerance
                    && y > dimensions2.verticalMargain.toFloat()+(node.yCoordinate*nodeToNodeDistance) - touchTolerance
                    && y < dimensions2.verticalMargain.toFloat()+(node.yCoordinate*nodeToNodeDistance) + touchTolerance) {
                    if(!game.tryToConnect(node)){
                        Toast.makeText(context, " Illegal Move ", Toast.LENGTH_SHORT).show();
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun itsEndGameNow(canvas: Canvas) {
        var paint: Paint? = game.winner?.paint
        val name: String? = game.winner?.name

        if (paint != null) {
            canvas.drawRoundRect(RectF(dimensions2.horizontalMargain.toFloat(),dimensions2.screenHeight/2+200F,
                dimensions2.screenWidth-dimensions2.horizontalMargain.toFloat(), dimensions2.screenHeight/2-200F),
                50F,50F,paint)
        }
        paint = Paint()
        paint.color = Color.rgb(0, 0, 0)
        canvas.drawRoundRect(RectF(dimensions2.horizontalMargain.toFloat()+5,dimensions2.screenHeight/2+195F,
            dimensions2.screenWidth-dimensions2.horizontalMargain.toFloat()-5, dimensions2.screenHeight/2-195F),
            50F,50F,paint)

        //Text
        paint.color = game.winner?.paint?.color!!
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 80F

        val xPos = dimensions2.screenWidth.toFloat()/2
        val yPos = dimensions2.screenHeight/2-40F

        canvas.drawText("WINNER!", xPos, yPos, paint)
        canvas.drawText(game.players[game.turn % 2].name, xPos, yPos+120F, paint)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        thread.running = true
        thread.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        var retry: Boolean = true
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //TODO GOTO CONTROLLER
        val xAxis: Float = event.x
        val yAxis: Float = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDown(xAxis, yAxis)
                return true
            }
        }
        return false
    }


}

data class ScreenDimensions2(val context: Context) {
    val screenHeight: Int
    val screenWidth: Int
    val horizontalMargain: Int = 200
    val boardSize: Int
    val verticalMargain: Int
    init {
        screenHeight = context.resources.displayMetrics.heightPixels
        screenWidth = context.resources.displayMetrics.widthPixels
        boardSize = calculateBoardSize()
        verticalMargain = calculateVerticalMargain()
    }
    private fun calculateBoardSize(): Int {
        return screenWidth - 2 * horizontalMargain
    }

    private fun calculateVerticalMargain(): Int {
        return (screenHeight - boardSize) / 2
    }
}

class Rectangle(private val dimensions2: ScreenDimensions2) {
    private var dx: Float = 15F
    private var dy: Float = -10F
    private var xx: Float = dimensions2.screenWidth/2.toFloat()
    private var yy: Float = dimensions2.screenHeight/2.toFloat()
    private var width: Float = 100F
    private var rectPaint: Paint = Paint()

    init {
        rectPaint.color = Color.rgb(255, 0, 0)
    }

    fun drawMovingRectangle(canvas: Canvas) {
        canvas.drawCircle(xx + width / 2, yy + width / 2, width / 2, rectPaint)
    }

    fun updateElementPosition() {
        yy+=dy
        xx+=dx
        if(yy > dimensions2.verticalMargain + dimensions2.screenWidth - 2 * dimensions2.horizontalMargain - width
            || yy < dimensions2.verticalMargain){
            dy *= -1
            rectPaint.color = Color.rgb(
                Random.nextInt(0, 255).toFloat(),
                Random.nextInt(0, 255).toFloat(),
                Random.nextInt(0, 255).toFloat()
            )

        }
        if(xx < dimensions2.horizontalMargain
            || xx > dimensions2.screenWidth - 2* dimensions2.horizontalMargain + dimensions2.horizontalMargain - width){
            dx *= -1
            rectPaint.color = Color.rgb(
                Random.nextInt(0, 255).toFloat(),
                Random.nextInt(0, 255).toFloat(),
                Random.nextInt(0, 255).toFloat()
            )
        }



    }

}