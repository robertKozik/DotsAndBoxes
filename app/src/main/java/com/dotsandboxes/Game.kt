package com.dotsandboxes

import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import java.lang.Math.abs
import kotlin.random.Random

class Game(val mapSize: Int, private var context: Context) {
    var turn: Int = 0
    var mapPoint: Array<Array<Point>>
    val squaresClosed: MutableList<Point> = mutableListOf()
    var players: Array<Player>
    val maxSquares: Int = (mapSize-1) * (mapSize-1)

    init {
        mapPoint = Array(mapSize) { row ->
                        Array(mapSize) { col ->
                            Point(row, col,context)
                        }
                    }
        players = arrayOf(Player("Player One"), Player( "Player Two"))


    }

    fun tryToConnect(nowSelectedNode: Point): Boolean{
        val previousSelectedNode: Point? = checkPointsClickState()

        if(previousSelectedNode != null && previousSelectedNode!=nowSelectedNode){
            val newLine = Line(nowSelectedNode,previousSelectedNode)
            if(nowSelectedNode.isLegalNeighbour(previousSelectedNode)
                && !checkIfLineExists(newLine)){
                players[turn%2].lines.add(newLine)
                val squareClosed = checkIfSquareIsClosed()
                if(squareClosed == 0) {
                    turn++

                }else {
                    players[turn%2].score += squareClosed
                }
                if( squaresClosed.size == maxSquares )
                return true
            }
            previousSelectedNode.clicked()
        } else {
            nowSelectedNode.clicked()
            return false
        }
        return false
    }
    private fun checkPointsClickState(): Point? {
        mapPoint.forEach { col ->
            run {
                col.forEach { node ->
                    if (node.isClicked) return node
                }
            }
        }
        return null
    }
    private fun checkIfLineExists(newLine: Line): Boolean{
        for(player in players){
            player.lines.forEach{line ->
                run {
                    if (line == newLine)
                        return true
                }
            }
        }
        return false
    }
    private fun checkIfSquareIsClosed(): Int {
        //TODO "Zrób lepiej"
        var size = squaresClosed.size
            for (x in 0..mapSize) {
                for (y in 0..mapSize) {
                    if (checkIfLineExists(Line(Point(x, y, context), Point(x + 1, y, context))) &&
                        checkIfLineExists(Line(Point(x + 1, y, context), Point(x + 1, y - 1, context))) &&
                        checkIfLineExists(Line(Point(x + 1, y - 1, context), Point(x, y - 1, context))) &&
                        checkIfLineExists(Line(Point(x, y - 1, context), Point(x, y, context)))
                    ) {
                        val topRightNodeOfSquare = Point(x, y, context)
                        if (!squaresClosed.contains(topRightNodeOfSquare)) {
                            squaresClosed.add(topRightNodeOfSquare)
                        }
                    }
                }
            }
        return squaresClosed.size - size
    }
}

class Point(var xCoodinate: Int, var yCoordinate: Int,var context: Context) {
    var isClicked: Boolean = false
    var paint: Paint = Paint()
    var pointRadius: Float = 20F


    init{
        paint.color = ResourcesCompat.getColor(context.resources, R.color.nodeNotSelected, null)
    }

    fun clicked() {
        if(isClicked){
            isClicked = false
            paint.color = ResourcesCompat.getColor(context.resources, R.color.nodeNotSelected, null)
            pointRadius =20F
        } else {
            isClicked = true
            paint.color = ResourcesCompat.getColor(context.resources, R.color.nodeSelected, null)
            pointRadius = 30F
        }
    }

    fun isLegalNeighbour(point: Point): Boolean { // legal Neighbour means that they can be connected
        if(xCoodinate == point.xCoodinate){
            return abs((yCoordinate - point.yCoordinate)) <= 1
        }
        if(yCoordinate == point.yCoordinate) {
            return abs((xCoodinate - point.xCoodinate)) <= 1
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        val other = other as Point
        return (xCoodinate == other.xCoodinate) && (yCoordinate == other.yCoordinate)
    }
}

class Line(var begin: Point, var end: Point) {
    var orientation: Int

    init {
        orientation = specifyOrientation()
    }
    private fun specifyOrientation(): Int {
        return if(begin.xCoodinate == end.xCoodinate){
            0
        } else {
            1
        }
    }

    override fun equals(other: Any?): Boolean {
        if(other is Line){
            return (begin == other.begin || begin == other.end) &&
                    (end == other.begin || end == other.end)
        }
        return super.equals(other)
    }
}

class Player(val name: String, var paint: Paint = Paint()) {
    var score: Int = 0
    var lines: MutableList<Line> = mutableListOf()
    init {
        paint.color = Color.rgb(Random.nextInt(0,255), Random.nextInt(0,255), Random.nextInt(0,255))
        paint.strokeWidth = 10F
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 200F
    }

}