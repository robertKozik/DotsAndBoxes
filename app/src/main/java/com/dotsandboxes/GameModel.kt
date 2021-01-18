package com.dotsandboxes

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceManager

class GameModel(val mapSize: Int, val context: Context) {
    var turn: Int = 0
    var mapPoint: Array<Array<Point>>
    val squaresClosed: MutableList<Point> = mutableListOf()
    var players: Array<Player>
    var winner: Player? = null
    val maxSquares: Int = (mapSize - 1) * (mapSize - 1)
    var gameState: Boolean = true
    val editor = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        //init map
        mapPoint = Array(mapSize) { row ->
            Array(mapSize) { col ->
                Point(row, col, context)
            }
        }

        //init players
        val playerOne = editor.getString(
            context.resources.getString(R.string.PlayerOneNickname),
            "Player One"
        )!!
        val playerOneColor = editor.getInt(
            context.resources.getString(R.string.PlayerOneColor),
            Color.rgb(0, 0, 255)
        )
        val playerTwo = editor.getString(
            context.resources.getString(R.string.PlayerTwoNickname),
            "Player One"
        )!!
        val playerTwoColor = editor.getInt(
            context.resources.getString(R.string.PlayerTwoColor),
            Color.rgb(0, 255, 0)
        )

        players = arrayOf(Player(playerOne, playerOneColor), Player(playerTwo, playerTwoColor))


    }
}