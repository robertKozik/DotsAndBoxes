package com.dotsandboxes

import android.preference.PreferenceManager
import androidx.core.content.res.ResourcesCompat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat

import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//@RunWith(AndroidJUnit4::class)
class PointTest {

    @Test
    fun afterCLickPointChangesColorAndRadius() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val testPoint = Point(1,1, appContext);
        val expectedColor = ResourcesCompat.getColor(appContext.resources, R.color.nodeSelected, null)
        testPoint.clicked();
        assertThat(testPoint.paint.color).isEqualTo(expectedColor).also {
            assertThat(testPoint.pointRadius).isEqualTo(30F)
        }
    }

    @Test
    fun twoPointsCannotBeConnected() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val firstPoint = Point(1,1, appContext);
        val secondPoint = Point(2,2, appContext);

        assertThat(firstPoint.isLegalNeighbour(secondPoint)).isFalse()
    }

    @Test
    fun twoPointsCanBeConnected() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val firstPoint = Point(1,1, appContext);
        val secondPoint = Point(1,2, appContext);

        assertThat(firstPoint.isLegalNeighbour(secondPoint)).isTrue()
    }

    @Test
    fun equalsFunChecksOnlyCoordinates(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val firstPoint = Point(1,1, appContext);
        val secondPoint = Point(1,1, appContext);

        assertThat(firstPoint.equals(secondPoint)).isTrue()
    }
}