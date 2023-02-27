package ir.baha.coins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import ir.baha.coin_domain.entity.Chart
import ir.baha.coin_domain.entity.Coins
import ir.baha.coins.ui.theme.CoinsTheme
import ir.baha.coins.vm.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm: CoinViewModel by viewModels()
    private val cvm: ChartViewModel by viewModels()
    private var selectedCoin: Coins? = null

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinsTheme {
                val coroutineScope = rememberCoroutineScope()

                var isRefreshing by remember {
                    mutableStateOf(false)
                }

                val refreshState = rememberSwipeRefreshState(isRefreshing)

                val modalSheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
                    skipHalfExpanded = false,
                )

                ModalBottomSheetLayout(
                    sheetState = modalSheetState,
                    sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    sheetContent = {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = selectedCoin?.icon),
                                        contentDescription = selectedCoin?.name,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    )
                                    Column {
                                        Text(
                                            text = selectedCoin?.name ?: "",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = selectedCoin?.price?.toString() ?: "",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                when (val uiState = cvm.uiState.collectAsState().value) {
                                    is ChartState.Error -> Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = uiState.error ?: "ERROR"
                                        )
                                    }

                                    ChartState.Loading -> Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }

                                    is ChartState.ShowChart -> QuadLineChart(
                                        uiState.chart!!.prices,
                                    )
                                }

                            }
                        }
                    }
                ) {
                    SwipeRefresh(
                        state = refreshState,
                        onRefresh = {
                            isRefreshing = true
                            vm.sendIntent(CoinsIntent.GetCoins(true))
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
                                    isRefreshing = false
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
                                    isRefreshing = false
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
                                    isRefreshing = false
                                    CoinsList(
                                        uiState.coins,
                                        onCoinClicked = { coin ->
                                            selectedCoin = coin
                                            cvm.sendIntent(ChartIntent.GetChart(selectedCoin!!.id))
                                            coroutineScope.launch {
                                                if (modalSheetState.isVisible)
                                                    modalSheetState.hide()
                                                else
                                                    modalSheetState.show()
                                            }
                                        }
                                    )
                                }

                            }
                        }
                    }

                }
            }
        }
    }

}

@Composable
fun LoadingView() {
    LazyColumn {
        items(20) {
            Card(
                modifier = Modifier
                    .testTag("loading")
                    .wrapContentHeight()
                    .padding(8.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    contentColor = Color.Gray,
                    containerColor = Color.Gray
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                    )
                }
            }
        }
    }
}

@Composable
fun CoinsList(
    coins: List<Coins>,
    onCoinClicked: (coin: Coins) -> Unit,
) {
    LazyColumn {
        items(coins) {
            CoinItem(it, onCoinClicked)
        }
    }
}

@Composable
fun CoinItem(
    coin: Coins,
    onCoinClicked: (coin: Coins) -> Unit,
) {

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .padding(8.dp)
            .clickable { onCoinClicked.invoke(coin) },
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = coin.icon),
                contentDescription = coin.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Column {
                Text(text = coin.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "$${coin.price}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}