package com.example.examplemvvm.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.examplemvvm.domain.GetQuoteUseCase
import com.example.examplemvvm.domain.GetRandomQuoteUseCase
import com.example.examplemvvm.domain.model.Quote
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class QuoteViewModelTest {

    private lateinit var quoteViewModel: QuoteViewModel

    @RelaxedMockK
    private lateinit var getQuoteUseCase: GetQuoteUseCase

    @RelaxedMockK
    private lateinit var getRandomQuoteUseCase: GetRandomQuoteUseCase

    @get:Rule
    var rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        quoteViewModel = QuoteViewModel(getQuoteUseCase, getRandomQuoteUseCase)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewmodel is created at the first time, get all quotes and set the first value`() =
        runTest {
            // Given
            val quoteList = listOf(Quote("My quote 1", "Mike"), Quote("My quote 2", "Mike"))
            coEvery { getQuoteUseCase() } returns quoteList

            // When
            quoteViewModel.onCreate()

            // Then
            assert(quoteViewModel.quoteModel.value == quoteList.first())
        }

    @Test
    fun `when randomQuoteUseCase return a quote set on the livedata`() =
        runTest {
            // Given
            val quote = Quote("My quote", "Mike")
            coEvery { getRandomQuoteUseCase() } returns quote

            // When
            quoteViewModel.randomQuote()

            // Then
            assert(quoteViewModel.quoteModel.value == quote)
        }

    @Test
    fun `when randomQuoteUseCase return null keep the last value`() =
        runTest {
            // Given
            val quote = Quote("My quote", "Mike")
            quoteViewModel.quoteModel.value = quote
            coEvery { getRandomQuoteUseCase() } returns null

            // When
            quoteViewModel.randomQuote()

            // Then
            assert(quoteViewModel.quoteModel.value == quote)
        }

}