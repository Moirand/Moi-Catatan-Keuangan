package com.example.moi_catatan_keuangan.addTransaction

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.domain.UiState
import com.example.domain.constant.CategoryType
import com.example.domain.constant.TransactionType
import com.example.domain.model.CategoryDomain
import com.example.domain.model.TransactionDomain
import com.example.domain.model.WalletDomain
import com.example.domain.model.WalletGroupDomain
import com.example.domain.repository.CategoryRepository
import com.example.domain.repository.TransactionRepository
import com.example.domain.repository.WalletGroupRepository
import com.example.domain.repository.WalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val walletRepository: WalletRepository,
    private val walletGroupRepository: WalletGroupRepository
) : ViewModel() {
    var categoryList = listOf<CategoryDomain?>()
    var walletGroupList = listOf<WalletGroupDomain?>()
    var walletList = MutableStateFlow<List<WalletDomain?>>(listOf())

    var selectedTransactionType: MutableStateFlow<TransactionType> =
        MutableStateFlow(TransactionType.Income)
    var selectedDate: MutableState<String> =
        mutableStateOf("")
    var selectedTime: MutableState<String> =
        mutableStateOf("")
    var transactionAmount: MutableState<BigDecimal> =
        mutableStateOf(BigDecimal.ZERO)
    var transferFeeAmount: MutableState<BigDecimal> =
        mutableStateOf(BigDecimal.ZERO)
    var selectedCategory: MutableState<Pair<Int?, String>> =
        mutableStateOf(Pair(null, ""))
    var selectedFromWalletGroup: MutableStateFlow<Pair<Int?, String>> =
        MutableStateFlow(Pair(null, ""))
    var selectedFromWallet: MutableState<Pair<Int?, String>> =
        mutableStateOf(Pair(null, ""))
    var selectedToWalletGroup: MutableStateFlow<Pair<Int?, String>> =
        MutableStateFlow(Pair(null, ""))
    var selectedToWallet: MutableState<Pair<Int?, String>> =
        mutableStateOf(Pair(null, ""))
    var note: MutableState<String> =
        mutableStateOf("")

    fun insertTransaction(): Flow<UiState<Unit>> = flow {
        if (selectedTransactionType.value == TransactionType.Transfer) {
            if (selectedDate.value.isNotEmpty()
                && selectedTime.value.isNotEmpty()
                && transactionAmount.value > BigDecimal.ZERO
                && selectedFromWallet.value.first != null
                && selectedToWallet.value.first != null
            ) {
                val transaction = TransactionDomain(
                    dateTime = "${selectedDate.value} ${selectedTime.value}",
                    type = selectedTransactionType.value,
                    fromWalletId = selectedFromWallet.value.first!!,
                    toWalletId = selectedToWallet.value.first!!,
                    amount = transactionAmount.value,
                    transferFee = transferFeeAmount.value,
                    note = note.value
                )
                emitAll(transactionRepository.insertTransaction(transaction))
            } else {
                emit(UiState.Error("Mohon Lengkapi Data Transaksi"))
            }
        } else {
            if (selectedDate.value.isNotEmpty()
                && selectedTime.value.isNotEmpty()
                && transactionAmount.value > BigDecimal.ZERO
                && selectedCategory.value.first != null
                && selectedFromWallet.value.first != null
            ) {
                val transaction = TransactionDomain(
                    dateTime = "${selectedDate.value} ${selectedTime.value}",
                    type = selectedTransactionType.value,
                    categoryId = selectedCategory.value.first,
                    fromWalletId = selectedFromWallet.value.first!!,
                    amount = transactionAmount.value,
                    note = note.value
                )
                emitAll(transactionRepository.insertTransaction(transaction))
            } else {
                emit(UiState.Error("Mohon Lengkapi Data Transaksi"))
            }
        }
    }

    fun getCategories(): Flow<UiState<List<CategoryDomain?>>> =
        selectedTransactionType.flatMapConcat { transactionType ->
            selectedCategory.value = Pair(null, "")
            categoryRepository.getCategoriesByType(
                if (transactionType == TransactionType.Income) CategoryType.Income else CategoryType.Expense
            )
        }

    fun getWalletGroups(): Flow<UiState<List<WalletGroupDomain?>>> =
        walletGroupRepository.getWalletGroups()

    fun getWallets(): Flow<UiState<List<WalletDomain?>>> =
        walletRepository.getWallets()
}