package com.dotsandboxes

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LineTest {

    @Test
    fun lineDirectionDoesntMatter() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val firstLine = Line(Point(1, 1, appContext), Point(1, 2, appContext))
        val secondLine = Line(Point(1, 2, appContext), Point(1, 1, appContext))

        assertThat(firstLine.equals(secondLine)).isTrue()
    }

    @Test
    fun lineOrientationIsVertical() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val line = Line(Point(1, 1, appContext), Point(1, 2, appContext))

        assertThat(line.orientation).isEqualTo(line.VERTICAL)
    }
}