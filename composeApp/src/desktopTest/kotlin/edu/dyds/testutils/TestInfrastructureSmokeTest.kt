package edu.dyds.testutils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TestInfrastructureSmokeTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when unit tests run with MainDispatcherRule, the main dispatcher can be controlled`() = runTest {
        val executed = withContext(Dispatchers.Main) { true }

        assertTrue(executed)
    }
}
