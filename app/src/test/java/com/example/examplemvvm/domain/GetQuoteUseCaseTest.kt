package com.example.examplemvvm.domain

import com.example.examplemvvm.data.QuoteRepository
import com.example.examplemvvm.domain.model.Quote
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetQuoteUseCaseTest {

    @RelaxedMockK // Generate responses that we haven't controlled
    // @MockK // It doesn't do the above (We need control everything) / Recommended
    private lateinit var quoteRepository: QuoteRepository
    private lateinit var getQuoteUseCase: GetQuoteUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getQuoteUseCase = GetQuoteUseCase(quoteRepository)
    }

    @Test
    fun `when the api doesn't return anything then get values from database`() = runBlocking {
        // Given
        coEvery { quoteRepository.getAllQuotesFromApi() } returns emptyList()

        // When
        getQuoteUseCase()

        // Then
        coVerify(exactly = 1) { quoteRepository.getAllQuotesFromDatabase() }
    }

    @Test
    fun `when api return something then get values from api`() = runBlocking {
        // Given
        val myList = listOf<Quote>(Quote("My quote", "Mike"))
        coEvery { quoteRepository.getAllQuotesFromApi() } returns myList

        // When
        val response = getQuoteUseCase()

        // Then
        coVerify(exactly = 1) { quoteRepository.clearQuotes() }
        coVerify(exactly = 1) { quoteRepository.insertQuotes(any()) }
        coVerify(exactly = 0) { quoteRepository.getAllQuotesFromDatabase() }
        assert(myList == response)
    }

}