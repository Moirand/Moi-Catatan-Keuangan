package com.example.moi_catatan_keuangan.home

import androidx.lifecycle.ViewModel
import com.example.domain.UiState
import com.example.domain.model.CategoryDomain
import com.example.domain.model.TransactionDetailDomain
import com.example.domain.model.TransactionDomain
import com.example.domain.model.WalletDomain
import com.example.domain.model.WalletGroupDomain
import com.example.domain.repository.CategoryRepository
import com.example.domain.repository.TransactionRepository
import com.example.domain.repository.WalletGroupRepository
import com.example.domain.repository.WalletRepository
import com.example.moi_catatan_keuangan.utils.QueryUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val walletRepository: WalletRepository,
    private val walletGroupRepository: WalletGroupRepository
) : ViewModel() {
    var selectedTransaction: TransactionDetailDomain? = null

    val incomeAmount = MutableStateFlow(BigDecimal.ZERO)
    val expenseAmount = MutableStateFlow(BigDecimal.ZERO)
    val totalAmount = combine(incomeAmount, expenseAmount) { income, expense ->
        income.minus(expense)
    }

    fun calculateIncomeAmount(month: Int, year: Int): Flow<UiState<BigDecimal>> =
        transactionRepository.calculateAmounts(
            QueryUtils.calculateIncomeAmounts(month = month, year = year)
        )

    fun calculateExpenseAmount(month: Int, year: Int): Flow<UiState<BigDecimal>> =
        transactionRepository.calculateAmounts(
            QueryUtils.calculateExpenseAmounts(month = month, year = year)
        )

    private val category: (Int) -> Flow<UiState<CategoryDomain?>> = { categoryId ->
        categoryRepository.getCategoryById(categoryId)
    }

    private val wallet: (Int) -> Flow<UiState<WalletDomain?>> = { walletId ->
        walletRepository.getWalletById(walletId)
    }

    private val walletGroup: (Int) -> Flow<UiState<WalletGroupDomain?>> = { walletGroupId ->
        walletGroupRepository.getWalletGroupById(walletGroupId)
    }

    val transactionList = MutableStateFlow(listOf<TransactionDomain?>())

    fun getTransactionList(month: Int, year: Int): Flow<UiState<List<TransactionDomain?>>> =
        transactionRepository.getTransactions(
            QueryUtils.getTransactionsByMonthYear(month = month, year = year)
        )

    val transactionListDetail: Flow<List<TransactionDetailDomain>> =
        transactionList.flatMapConcat { transactions ->
            flow {
                val transactionDetailList = mutableListOf<TransactionDetailDomain>()
                transactions.forEach { transaction ->

                    var categoryData: CategoryDomain? = null
                    transaction?.categoryId?.let { categoryId ->
                        category(categoryId).collect { categoryState ->
                            when (categoryState) {
                                is UiState.Loading -> {}
                                is UiState.Error -> throw Throwable(categoryState.message)
                                is UiState.Success -> {
                                    categoryData = categoryState.data
                                }
                            }
                        }
                    }

                    var fromWalletData: WalletDomain? = null
                    wallet(transaction?.fromWalletId!!).collect { fromWalletState ->
                        when (fromWalletState) {
                            is UiState.Loading -> {}
                            is UiState.Error -> throw Throwable(fromWalletState.message)
                            is UiState.Success -> {
                                fromWalletData = fromWalletState.data
                            }
                        }
                    }

                    var toWalletData: WalletDomain? = null
                    transaction.toWalletId?.let { toWalletId ->
                        wallet(toWalletId).collect { toWalletState ->
                            when (toWalletState) {
                                is UiState.Loading -> {}
                                is UiState.Error -> throw Throwable(toWalletState.message)
                                is UiState.Success -> {
                                    toWalletData = toWalletState.data
                                }
                            }
                        }
                    }

                    var fromWalletGroupData: WalletGroupDomain? = null
                    walletGroup(fromWalletData?.walletGroupId!!).collect { fromWalletGroupState ->
                        when (fromWalletGroupState) {
                            is UiState.Loading -> {}
                            is UiState.Error -> throw Throwable(fromWalletGroupState.message)
                            is UiState.Success -> {
                                fromWalletGroupData = fromWalletGroupState.data
                            }
                        }
                    }

                    var toWalletGroupData: WalletGroupDomain? = null
                    toWalletData?.let {
                        walletGroup(it.walletGroupId).collect { toWalletGroupState ->
                            when (toWalletGroupState) {
                                is UiState.Loading -> {}
                                is UiState.Error -> throw Throwable(toWalletGroupState.message)
                                is UiState.Success -> {
                                    toWalletGroupData = toWalletGroupState.data
                                }
                            }
                        }
                    }

                    val transactionDetail = TransactionDetailDomain(
                        transaction = transaction,
                        category = categoryData,
                        fromWallet = fromWalletData!!,
                        toWallet = toWalletData,
                        fromWalletGroup = fromWalletGroupData!!,
                        toWalletGroup = toWalletGroupData
                    )
                    transactionDetailList.add(transactionDetail)
                }
                emit(transactionDetailList.toList())
            }
        }
}