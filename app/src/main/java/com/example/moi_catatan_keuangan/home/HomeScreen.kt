package com.example.moi_catatan_keuangan.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.UiState
import com.example.domain.constant.TransactionType
import com.example.domain.model.TransactionDetailDomain
import com.example.moi_catatan_keuangan.component.TransactionListItemHome
import com.example.moi_catatan_keuangan.utils.reformatDateTime
import com.example.moi_catatan_keuangan.utils.toDecimalFormat
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val currentMonthYear by remember { mutableStateOf(LocalDate.now()) }

    val transactionListState by remember {
        viewModel.getTransactionList(currentMonthYear.monthValue, currentMonthYear.year)
    }.collectAsState(UiState.Loading())

    val transactionDetailList by remember { viewModel.transactionListDetail }.collectAsState(listOf())

    val incomeAmountState by remember {
        viewModel.calculateIncomeAmount(currentMonthYear.monthValue, currentMonthYear.year)
    }.collectAsState(UiState.Loading())

    val expenseAmountState by remember {
        viewModel.calculateExpenseAmount(currentMonthYear.monthValue, currentMonthYear.year)
    }.collectAsState(UiState.Loading())

    val totalAmount by remember { viewModel.totalAmount }.collectAsState(BigDecimal.ZERO)

    LaunchedEffect(transactionListState) {
        when (transactionListState) {
            is UiState.Loading -> {}
            is UiState.Error -> {
                Log.e("Andre", "transactionListState: ${transactionListState.message}")
            }

            is UiState.Success -> {
                viewModel.transactionList.value = transactionListState.data!!
            }
        }
    }

    LaunchedEffect(incomeAmountState) {
        when (incomeAmountState) {
            is UiState.Loading -> {}
            is UiState.Error -> {
                Log.e("Andre", "incomeAmountState: ${incomeAmountState.message}")
            }

            is UiState.Success -> {
                viewModel.incomeAmount.value = incomeAmountState.data
            }
        }
    }

    LaunchedEffect(expenseAmountState) {
        when (expenseAmountState) {
            is UiState.Loading -> {}
            is UiState.Error -> {
                Log.e("Andre", "expenseAmountState: ${expenseAmountState.message}")
            }

            is UiState.Success -> {
                viewModel.expenseAmount.value = expenseAmountState.data
            }
        }
    }

    // ModalBottomSheet setup
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openBottomSheet by remember { mutableStateOf(false) }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = modalBottomSheetState,
            containerColor = Color(0xFFFFFAFA),
            content = { ModalBottomSheetContent(viewModel.selectedTransaction) },
        )
    }

    MainContent(
        currentMonthYear = currentMonthYear,
        transactionList = transactionDetailList,
        incomeAmount = viewModel.incomeAmount.collectAsState().value.toDecimalFormat(),
        expenseAmount = viewModel.expenseAmount.collectAsState().value.toDecimalFormat(),
        totalAmount = totalAmount.toDecimalFormat(),
        onClick = { transaction ->
            viewModel.selectedTransaction = transaction
            openBottomSheet = true
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MainContent(
    currentMonthYear: LocalDate,
    transactionList: List<TransactionDetailDomain>,
    incomeAmount: String,
    expenseAmount: String,
    totalAmount: String,
    onClick: (TransactionDetailDomain) -> Unit
) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp),
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 16.dp),
                    text = "${currentMonthYear.month} ${currentMonthYear.year}",
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    TypeAndAmount(
                        modifier = Modifier.weight(1F),
                        title = "Total",
                        amount = totalAmount
                    )

                    Row(
                        modifier = Modifier.weight(1F)
                    ) {
                        TypeAndAmount(
                            modifier = Modifier.weight(1F),
                            title = "Income",
                            amount = incomeAmount
                        )
                        TypeAndAmount(
                            modifier = Modifier.weight(1F),
                            title = "Expense",
                            amount = expenseAmount
                        )
                    }
                }
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(8.dp, 0.dp)
        ) {
            var date = ""
            items(
                items = transactionList,
                key = { transaction -> transaction.transaction.id }
            ) { transaction ->
                TransactionListItemHome(
                    dateTime = transaction.transaction.dateTime,
                    type = transaction.transaction.type,
                    category = transaction.category?.name,
                    fromWalletGroupWallet = "${transaction.fromWalletGroup.name}/${transaction.fromWallet.name}",
                    toWalletGroupWallet =
                        if (transaction.toWalletGroup != null && transaction.toWallet != null) {
                            "${transaction.toWalletGroup?.name}/${transaction.toWallet?.name}"
                        } else null,
                    amount = transaction.transaction.amount.toDecimalFormat(),
                    note = transaction.transaction.note,
                    isLatestTimeOfDate = transaction.transaction.dateTime.substring(
                        startIndex = 9,
                        endIndex = 11
                    ) != date,
                    onClick = { onClick(transaction) }
                )
                date = transaction.transaction.dateTime.substring(startIndex = 9, endIndex = 11)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ModalBottomSheetContent(
    transactionData: TransactionDetailDomain? = null
) {
    transactionData?.let {
        val bitmapImage =
            if (transactionData.transaction.receipt != null) remember(transactionData.transaction.receipt) {
                BitmapFactory.decodeByteArray(
                    transactionData.transaction.receipt,
                    0,
                    transactionData.transaction.receipt!!.size
                )
            } else {
                null
            }

        Column(
            modifier = Modifier
                .padding(8.dp, 0.dp)
                .heightIn(min = 300.dp, max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Area untuk Button Icon edit, Icon bookmark, & Icon delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
            // Area untuk detail transaksi
            Column {
                Row {
                    Column(
                        modifier = Modifier.weight(2F),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TitleAndValue(
                            title = "Tanggal & Waktu",
                            value = reformatDateTime(transactionData.transaction.dateTime)
                        )
                        // Jika tipe transaksi adalah transfer
                        if (transactionData.transaction.type == TransactionType.Transfer) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(48.dp)
                            ) {
                                TitleAndValue(
                                    title = "Transfer Net",
                                    value = "Rp ${transactionData.transaction.amount.toDecimalFormat()}",
                                    subValue = "Total: Rp ${
                                        (transactionData.transaction.amount.plus(
                                            transactionData.transaction.transferFee
                                        )).toDecimalFormat()
                                    }"
                                )
                                TitleAndValue(
                                    title = "Biaya Transfer",
                                    value = "Rp ${transactionData.transaction.transferFee.toDecimalFormat()}"
                                )
                            }
                            TitleAndValue(
                                title = "Dari Dompet",
                                value = "${transactionData.fromWalletGroup.name}/${transactionData.fromWallet.name}"
                            )
                            TitleAndValue(
                                title = "Ke Dompet",
                                value = "${transactionData.toWalletGroup?.name}/${transactionData.toWallet?.name}"
                            )
                        }

                        // Jika tipe transaksi adalah pemasukan atau pengeluaran
                        else {
                            TitleAndValue(
                                title = "Total Transaksi",
                                value = "Rp ${transactionData.transaction.amount.toDecimalFormat()}"
                            )
                            TitleAndValue(
                                title = "Kategori",
                                value = transactionData.category?.name
                            )
                            TitleAndValue(
                                title = "Dompet",
                                value = "${transactionData.fromWalletGroup.name}/${transactionData.fromWallet.name}"
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1F),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TitleAndValue(
                            title = "Jenis Transaksi",
                            value = transactionData.transaction.type.toString()
                        )

                        TitleAndValue(
                            title = "Catatan Gambar",
                            receipt = bitmapImage
                        )
                    }
                }

                TitleAndValue(
                    modifier = Modifier.padding(top = 8.dp),
                    title = "Catatan",
                    value = transactionData.transaction.note ?: "Tidak ada catatan"
                )
            }
        }
    }
}

@Composable
private fun TypeAndAmount(
    modifier: Modifier = Modifier,
    title: String,
    amount: String
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = amount,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TitleAndValue(
    modifier: Modifier = Modifier,
    title: String,
    value: String? = null,
    subValue: String? = null,
    receipt: Bitmap? = null
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Gray
        )
        if (value != null) {
            Text(
                text = value,
                fontSize = 16.sp,
            )
        }
        if (receipt != null) {
            Image(
                bitmap = receipt.asImageBitmap(),
                contentDescription = "Catatan Gambar"
            )
        }
        if (subValue != null) {
            Text(
                text = subValue,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}