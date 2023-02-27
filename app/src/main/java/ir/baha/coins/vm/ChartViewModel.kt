package ir.baha.coins.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.baha.coin_domain.entity.Chart
import ir.baha.coin_domain.entity.Resource
import ir.baha.coin_domain.use_case.GetChartUseCase
import ir.baha.coins.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val getChartUseCase: GetChartUseCase
) : BaseViewModel<ChartIntent, ChartAction, ChartState>() {
    override fun createInitialState(): ChartState = ChartState.Loading

    override fun handleIntent(intent: ChartIntent): ChartAction = when (intent) {
        is ChartIntent.GetChart -> ChartAction.Chart(intent.id)
    }

    override fun handleAction(action: ChartAction) {
        when (action) {
            is ChartAction.Chart -> {
                setState { ChartState.Loading }
                viewModelScope.launch {
                    when (val res = getChartUseCase(action.id)) {
                        is Resource.Error -> setState { ChartState.Error(res.exception?.message) }
                        is Resource.Success -> setState { ChartState.ShowChart(res.data) }
                    }
                }
            }
        }
    }
}

sealed class ChartIntent {
    class GetChart(val id: String) : ChartIntent()
}

sealed class ChartAction {
    class Chart(val id: String) : ChartAction()
}

sealed class ChartState {
    object Loading : ChartState()
    data class Error(val error: String?) : ChartState()
    data class ShowChart(val chart: Chart?) : ChartState()
}