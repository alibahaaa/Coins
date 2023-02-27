package ir.baha.repository.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import ir.baha.coin_domain.entity.Resource
import ir.baha.coin_domain.repository.CoinsRepository
import ir.baha.local.database.CoinsDao
import ir.baha.local.dto.CoinsDto
import ir.baha.local.mapper.toEntity
import ir.baha.local.mapper.toLocal
import ir.baha.remote.api.CoinsApiService
import ir.baha.remote.mapper.toDomain
import ir.baha.remote.response.CoinMarketResponse
import ir.baha.remote.response.MarketChartResponse
import ir.baha.repository.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoinsRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var repo: CoinsRepository
    private lateinit var api: CoinsApiService
    private lateinit var dao: CoinsDao

    @Before
    fun setUp() {
        api = mockk()
        dao = mockk()
        repo = CoinsRepositoryImpl(api, dao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCoins returns expected flow of data`() = runTest {
        // given
        val mockCoins = listOf(
            CoinsDto("1", "Bitcoin", "", 2000.0, "")
        )
        coEvery { dao.getAllCoins() } returns flowOf(mockCoins)

        // when
        val flow = repo.getCoins()

        // then
        flow.toList().apply {
            assertThat(size).isEqualTo(1)
            assertThat(first()).isEqualTo(mockCoins.map { it.toEntity() })
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchCoins() should return success when api call is successful`() = runTest {
        // given
        val coinsApiRes = listOf(
            CoinMarketResponse("1", "Bitcoin", "", 2000.0, 2000.0, 2000.0, 2000.0, "")
        )
        val coinsDto = coinsApiRes.map {
            it.toDomain().toLocal()
        }
        coEvery { api.getMarketData(any(), any(), any(), any(), any()) } returns coinsApiRes
        coEvery { dao.deleteCoins() } just Runs
        coEvery { dao.insertCoins(coinsDto) } just Runs

        // when
        val result = repo.fetchCoins()

        // then
        assertThat(result.data).isTrue()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchCoins() should return error when api call fails`() = runTest {
        // given
        val exception = Exception("API call failed")
        coEvery { api.getMarketData(any(), any(), any(), any(), any()) } throws exception

        // when
        val result = repo.fetchCoins()

        // then
        assertThat(result.exception?.message).isEqualTo(exception.message)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCoinSize should return the size of the coins list`() = runTest {
        // given
        val size = 10
        coEvery { dao.coinsListSize() } returns size

        // when
        val result = repo.getCoinSize()

        // then
        assertThat(result).isEqualTo(size)
        coVerify(exactly = 1) { dao.coinsListSize() }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchChart returns Success with chart data when api call is successful`() = runTest {
        // Given
        val id = "btc"
        val chartDto = MarketChartResponse(
            prices = listOf(listOf(2.2, 2.2, 2.2)),
            marketCaps = listOf(listOf(2.2, 2.2, 2.2)),
            totalVolumes = listOf(listOf(2.2, 2.2, 2.2))
        )
        val expectedChart = chartDto.toDomain()
        coEvery { api.getMarketChart(id, "usd", 1) } returns chartDto

        // When
        val result = repo.fetchChart(id)

        // Then
        assertThat((result as Resource.Success<*>).data).isEqualTo(expectedChart)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchChart returns Error with exception when api call throws an exception`() = runTest {
        // Given
        val id = "btc"
        val expectedException = Exception("API call failed")
        coEvery { api.getMarketChart(id, "usd", 1) } throws expectedException

        // When
        val result = repo.fetchChart(id)

        // Then
        assertThat((result as Resource.Error).exception).isEqualTo(expectedException)
    }

}