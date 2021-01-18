package com.dotsandboxes

import android.view.MotionEvent
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import java.util.*

class GameController(val game:GameModel, private val dims: ScreenDimensions) {
    var previousSelectedNode:Point? = null


    fun actionPerformed(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val pointClicked: Point? = getNodeClicked(event.x, event.y);
                    if(pointClicked != null) {
                        return evaluateMove(pointClicked)
                    }
                    return false
                }
            }
        }
        return false
    }

    private fun getNodeClicked (x:Float, y:Float): Point? {
        val touchTolerance: Float = (dims.boardSize/(game.mapSize*2)).toFloat()

        for(row in game.mapPoint) {
            for (node in row) {
                if( x > dims.horizontalMargain.toFloat()+(node.xCoodinate*dims.nodeToNodeDistance) - touchTolerance
                    && x < dims.horizontalMargain.toFloat()+(node.xCoodinate*dims.nodeToNodeDistance) + touchTolerance
                    && y > dims.verticalMargain.toFloat()+(node.yCoordinate*dims.nodeToNodeDistance) - touchTolerance
                    && y < dims.verticalMargain.toFloat()+(node.yCoordinate*dims.nodeToNodeDistance) + touchTolerance) {
                    return node
                }
            }
        }
        return null
    }
    fun evaluateMove(nowSelectedNode: Point): Boolean{

        if(previousSelectedNode != null && previousSelectedNode!=nowSelectedNode){
            val newLine = Line(nowSelectedNode, previousSelectedNode!!)
            if(nowSelectedNode.isLegalNeighbour(previousSelectedNode!!)
                && !checkIfLineExists(newLine)){
                game.players[game.turn%2].lines.add(newLine)
                previousSelectedNode!!.clicked()
                previousSelectedNode = null;
                val squareClosed = checkIfSquareIsClosed()
                if(squareClosed == 0) {
                    game.turn++

                }else {
                    game.players[game.turn%2].score += squareClosed
                }

                if( game.squaresClosed.size == game.maxSquares ) {
                    game.gameState = false
                    game.winner = evaluateWinner()
                }
                return true
            } else {
                previousSelectedNode!!.clicked()
                previousSelectedNode = null;
            }
        } else {
            nowSelectedNode.clicked()
            previousSelectedNode = nowSelectedNode
            return true
        }
        return false
    }
    fun evaluateWinner(): Player {
        val player1Score = game.players[0].score
        val player2Score = game.players[1].score
        return if(player1Score>player2Score) game.players[0] else game.players[1]
    }
    private fun checkPointsClickState(): Point? {
        game.mapPoint.forEach { col ->
            run {
                col.forEach { node ->
                    if (node.isClicked) return node
                }
            }
        }
        return null
    }
    private fun checkIfLineExists(newLine: Line): Boolean{
        for(player in game.players){
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
        var size = game.squaresClosed.size
        for (x in 0..game.mapSize) {
            for (y in 0..game.mapSize) {
                if (checkIfLineExists(Line(Point(x, y, game.context), Point(x + 1, y, game.context))) &&
                    checkIfLineExists(Line(Point(x + 1, y, game.context), Point(x + 1, y - 1, game.context))) &&
                    checkIfLineExists(Line(Point(x + 1, y - 1, game.context), Point(x, y - 1, game.context))) &&
                    checkIfLineExists(Line(Point(x, y - 1, game.context), Point(x, y, game.context)))
                ) {
                    val topRightNodeOfSquare = Point(x, y, game.context)
                    if (!game.squaresClosed.contains(topRightNodeOfSquare)) {
                        game.squaresClosed.add(topRightNodeOfSquare)
                    }
                }
            }
        }
        return game.squaresClosed.size - size
    }
}