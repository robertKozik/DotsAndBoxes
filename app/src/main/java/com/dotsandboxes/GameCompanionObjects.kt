package com.dotsandboxes

import android.content.Context
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat

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
            return Math.abs((yCoordinate - point.yCoordinate)) <= 1
        }
        if(yCoordinate == point.yCoordinate) {
            return Math.abs((xCoodinate - point.xCoodinate)) <= 1
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

class Player(val name: String, var color: Int) {
    var paint: Paint = Paint()
    var score: Int = 0
    var lines: MutableList<Line> = mutableListOf()

    init {
        paint.color = color
        paint.strokeWidth = 10F
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 200F
    }

}