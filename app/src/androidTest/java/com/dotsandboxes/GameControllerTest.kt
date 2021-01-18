package com.dotsandboxes

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import com.google.common.truth.Truth.*

class GameControllerTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val controller: GameController = GameController(GameModel(4,appContext), ScreenDimensions(appContext))

    @Test
    fun gameEndsAccordinigly() {

    }

    @Test
    fun BiggerScoreWins() {
        controller.game.players[0].score = 5
        controller.game.players[1].score = 4
        val winner = controller.evaluateWinner()

        assertThat(winner).isEqualTo(controller.game.players[0])

    }

    @Test
    fun cannotDrawLongLine() {
        val firstPoint = controller.game.mapPoint[0][0]
        val secondPoint = controller.game.mapPoint[0][2]
        val playerDrawingLine = controller.game.players[0]

        controller.evaluateMove(firstPoint)
        controller.evaluateMove(secondPoint)

        assertThat(playerDrawingLine.lines.isEmpty()).isTrue()
    }

    @Test
    fun doubleClickPoint() {
        val point: Point = controller.game.mapPoint[0][0]

        controller.evaluateMove(point) // isClicked = true
        controller.evaluateMove(point) // isClicked = false

        assertThat(point.isClicked).isFalse()
    }

    @Test
    fun afterScoringTurnDoestChange() {
        val leftUpper = controller.game.mapPoint[0][0]
        val rightUpper = controller.game.mapPoint[0][1]
        val leftLower = controller.game.mapPoint[1][0]
        val rightLower = controller.game.mapPoint[1][1]

        val player1: Player = controller.game.players[0]
        val player2: Player = controller.game.players[1]

        controller.evaluateMove(leftUpper)
        controller.evaluateMove(rightUpper) //turn == 1

        controller.evaluateMove(leftUpper)
        controller.evaluateMove(leftLower) //turn == 2

        controller.evaluateMove(leftLower)
        controller.evaluateMove(rightLower) //turn == 3

        controller.evaluateMove(rightLower)
        controller.evaluateMove(rightUpper) //turn ==3

        assertThat(controller.game.turn).isEqualTo(3)
    }

    @Test
    fun tryDrawExistingLine() {
        val firstPoint = controller.game.mapPoint[0][0]
        val secondPoint = controller.game.mapPoint[0][1]

        controller.evaluateMove(firstPoint)
        controller.evaluateMove(secondPoint) //line is drawn

        controller.evaluateMove(secondPoint)
        controller.evaluateMove(firstPoint) //cannot draw line, because one exists

        assertThat(controller.game.turn).isEqualTo(1).also {
            assertThat(controller.game.players[1].lines).isEmpty()
        }
    }

}