package ir.baha.coins.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.baha.coin_domain.entity.Coins
import ir.baha.coin_domain.entity.Resource
import ir.baha.coin_domain.use_case.GetCoinsUseCase
import ir.baha.coins.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsUseCase
) : BaseViewModel<CoinsIntent, CoinsAction, CoinsState>() {
    override fun createInitialState(): CoinsState = CoinsState.Idle

    override fun handleIntent(intent: CoinsIntent): CoinsAction = when (intent) {
        is CoinsIntent.GetCoins -> CoinsAction.Coins(intent.forceRefresh)
    }

    override fun handleAction(action: CoinsAction) {
        when (action) {
            is CoinsAction.Coins -> {
                viewModelScope.launch {
                    when (val res = getCoinsUseCase(action.forceRefresh)) {
                        is Resource.Error -> setState { CoinsState.Error(res.exception?.message) }
                        is Resource.Success -> {
                            res.data!!
                                .onEach {
                                    setState {
                                        if (it.isEmpty()) {
                                            CoinsState.Empty
                                        } else {
                                            CoinsState.ShowCoins(it)
                                        }
                                    }
                                }
                                .catch {
                                    setState { CoinsState.Error(it.message) }
                                }
                                .launchIn(viewModelScope)
                        }
                    }
                }
            }
        }
    }
}


sealed class CoinsIntent {
    class GetCoins(val forceRefresh: Boolean) : CoinsIntent()
}

sealed class CoinsAction {
    class Coins(var forceRefresh: Boolean) : CoinsAction()
}

sealed class CoinsState {
    object Idle : CoinsState()
    object Loading : CoinsState()
    object Empty : CoinsState()
    data class Error(val error: String?) : CoinsState()
    data class ShowCoins(val coins: List<Coins>) : CoinsState()
}