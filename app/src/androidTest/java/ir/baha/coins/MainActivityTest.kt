package ir.baha.coins

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.mockk.every
import io.mockk.mockk
import ir.baha.coins.vm.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test


class MainActivityTest {


    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Composable
    fun View(vm: CoinViewModel) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(false),
            onRefresh = {

            },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                when (val uiState = vm.uiState.collectAsState().value) {

                    CoinsState.Idle -> vm.sendIntent(CoinsIntent.GetCoins(false))

                    CoinsState.Loading -> LoadingView()

                    CoinsState.Empty -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(500.dp)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Empty list, Swipe down to fetch new data.",
                                    )
                                }

                            }
                        }
                    }

                    is CoinsState.Error -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(500.dp)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${uiState.error ?: "ERROR"}, Swipe down to retry."
                                    )
                                }
                            }
                        }
                    }

                    is CoinsState.ShowCoins -> {
                        CoinsList(
                            uiState.coins,
                            onCoinClicked = { coin ->

                            }
                        )
                    }

                }
            }
        }
    }

    @Test
    fun testMovieListScreen_displaysLoadingIndicator() {
        // Arrange
        val vm = mockk<CoinViewModel>()
        every { vm.uiState } returns MutableStateFlow(CoinsState.Loading)

        // Act
        composeTestRule.setContent {
            View(vm = vm)
        }

        // Assert
        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

}