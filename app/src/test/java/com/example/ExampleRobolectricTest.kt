package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SmartWatchRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Your Own Doctor", appName)
  }

  @Test
  fun `verify smartwatch repository and bioscience insights`() {
    val repository = SmartWatchRepository()
    val connectedDevice = repository.connectedDevice.value
    assertNotNull(connectedDevice)
    assertEquals("Galaxy Watch Ultra", connectedDevice?.name)

    val insights = repository.getBioscienceInsights()
    assertTrue(insights.isNotEmpty())
    assertTrue(insights.any { it.title.contains("Vagal Nerve", ignoreCase = true) })
  }
}
