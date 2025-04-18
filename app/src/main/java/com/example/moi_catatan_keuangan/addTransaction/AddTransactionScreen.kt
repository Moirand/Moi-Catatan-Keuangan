package com.example.moi_catatan_keuangan.addTransaction

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.UiState
import com.example.domain.constant.TransactionType
import com.example.moi_catatan_keuangan.component.MyInputField
import com.example.moi_catatan_keuangan.component.MyNestedDropdownInput
import com.example.moi_catatan_keuangan.utils.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.util.Calendar

@SuppressLint("StateFlowValueCalledInComposition", "DefaultLocale")
@Preview(showBackground = true)
@Composable
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: AddTransactionViewModel = koinViewModel(),
    onSave: (isSaved: Boolean) -> Unit = {},
    onSaveButtonClick: (() -> Unit) -> Unit = {}
) {
    val activeButtonContainerColor = Color.Blue
    val activeButtonContentColor = Color.White
    val inActiveButtonContainerColor = Color.White
    val inActiveButtonContentColor = Color.Blue

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate =
                String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
            viewModel.selectedDate.value = formattedDate
        },
        year, month, day
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            viewModel.selectedTime.value = formattedTime
        },
        hour, minute, true
    )

    val categoryListState by remember { viewModel.getCategories() }.collectAsState(UiState.Loading())
    val walletGroupListState by remember { viewModel.getWalletGroups() }.collectAsState(UiState.Loading())
    val walletListState by remember { viewModel.getWallets() }.collectAsState(UiState.Loading())

    LaunchedEffect(categoryListState) {
        when (categoryListState) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                viewModel.categoryList = categoryListState.data!!
            }
        }
    }

    LaunchedEffect(walletGroupListState) {
        when (walletGroupListState) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                viewModel.walletGroupList = walletGroupListState.data!!
            }
        }
    }

    LaunchedEffect(walletListState) {
        when (walletListState) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                viewModel.walletList.value = walletListState.data!!
            }
        }
    }

    onSaveButtonClick {
        scope.launch {
            viewModel.insertTransaction().collect { state ->
                when (state) {
                    is UiState.Loading -> {}
                    is UiState.Error -> {
                        context.makeToast(state.message.toString())
                        onSave(false)
                    }

                    is UiState.Success -> {
                        context.makeToast("Transaksi Berhasil Ditambahkan")
                        onSave(true)
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Jenis Transaksi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (viewModel.selectedTransactionType.value == TransactionType.Income) activeButtonContainerColor else inActiveButtonContainerColor,
                    contentColor = if (viewModel.selectedTransactionType.value == TransactionType.Income) activeButtonContentColor else inActiveButtonContentColor
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = activeButtonContainerColor,
                ),
                onClick = {
                    viewModel.selectedTransactionType.value = TransactionType.Income
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(
                    text = "Income"
                )
            }

            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (viewModel.selectedTransactionType.value == TransactionType.Expense) activeButtonContainerColor else inActiveButtonContainerColor,
                    contentColor = if (viewModel.selectedTransactionType.value == TransactionType.Expense) activeButtonContentColor else inActiveButtonContentColor
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = activeButtonContainerColor,
                ),
                onClick = {
                    viewModel.selectedTransactionType.value = TransactionType.Expense
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(
                    text = "Expense"
                )
            }

            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (viewModel.selectedTransactionType.value == TransactionType.Transfer) activeButtonContainerColor else inActiveButtonContainerColor,
                    contentColor = if (viewModel.selectedTransactionType.value == TransactionType.Transfer) activeButtonContentColor else inActiveButtonContentColor
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = activeButtonContainerColor,
                ),
                onClick = {
                    viewModel.selectedTransactionType.value = TransactionType.Transfer
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(
                    text = "Transfer"
                )
            }
        }

        if (viewModel.selectedTransactionType.value == TransactionType.Transfer)
            TransferContent(
                datePickerDialog = datePickerDialog,
                timePickerDialog = timePickerDialog
            )
        else
            IncomExpenseContent(
                datePickerDialog = datePickerDialog,
                timePickerDialog = timePickerDialog
            )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun IncomExpenseContent(
    viewModel: AddTransactionViewModel = koinViewModel(),
    timePickerDialog: TimePickerDialog,
    datePickerDialog: DatePickerDialog
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Tanggal & Waktu
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyInputField(
                modifier = Modifier
                    .weight(1F),
                title = "Tanggal",
                value = viewModel.selectedDate.value,
                onValueChange = { viewModel.selectedDate.value = it },
                readOnly = true,
                onFocused = { datePickerDialog.show() }
            )
            MyInputField(
                modifier = Modifier
                    .weight(1F),
                title = "Waktu",
                value = viewModel.selectedTime.value,
                onValueChange = { viewModel.selectedTime.value = it },
                readOnly = true,
                onFocused = { timePickerDialog.show() }
            )
        }

        // Input Nominal Transaksi
        MyInputField(
            title = "Total Transaksi",
            value = if (viewModel.transactionAmount.value == BigDecimal.ZERO) "0" else viewModel.transactionAmount.value.toString(),
            onValueChange = {
                viewModel.transactionAmount.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO
            },
            prefix = { Text("Rp ") },
            keyboardType = KeyboardType.Number
        )

        // Input Kategori
        MyNestedDropdownInput(
            title = "Kategori",
            menuItemList1 = viewModel.categoryList.map { Pair(it!!.id, it.name) },
            onItemSelected1 = { (categoryId, categoryName) ->
                viewModel.selectedCategory.value = Pair(categoryId, categoryName)
            },
            value = viewModel.selectedCategory.value.second
        )

        // Input Dompet
        MyNestedDropdownInput(
            title = "Dompet",
            menuItemList1 = viewModel.walletGroupList.map { Pair(it!!.id, it.name) },
            menuItemList2 = viewModel.walletList.value.filter { it!!.walletGroupId == viewModel.selectedFromWalletGroup.value.first }
                .map { Pair(it!!.id, it.name) },
            onItemSelected1 = { (walletGroupId, walletGroupName) ->
                viewModel.selectedFromWalletGroup.value = Pair(walletGroupId, walletGroupName)
            },
            onItemSelected2 = { (walletId, walletGroupName) ->
                viewModel.selectedFromWallet.value = Pair(walletId, walletGroupName)
            },
            value = if (viewModel.selectedFromWalletGroup.value.first == null) viewModel.selectedFromWalletGroup.value.second else "${viewModel.selectedFromWalletGroup.value.second}/${viewModel.selectedFromWallet.value.second}"
        )

        // Input Catatan
        MyInputField(
            title = "Catatan",
            value = viewModel.note.value,
            onValueChange = { viewModel.note.value = it },
            minLines = 5,
            singleLine = false
        )

        // Input Gambar
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        style = Stroke(
                            width = 4F,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20F, 20F), 0F)
                        )
                    )
                },
            shape = RoundedCornerShape(8.dp),
            border = null,
            onClick = {}
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null
                )
                Text(
                    text = "Upload Gambar"
                )
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun TransferContent(
    viewModel: AddTransactionViewModel = koinViewModel(),
    timePickerDialog: TimePickerDialog,
    datePickerDialog: DatePickerDialog,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Tanggal & Waktu
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyInputField(
                modifier = Modifier
                    .weight(1F),
                title = "Tanggal",
                value = viewModel.selectedDate.value,
                onValueChange = { viewModel.selectedDate.value = it },
                readOnly = true,
                onFocused = { datePickerDialog.show() }
            )
            MyInputField(
                modifier = Modifier
                    .weight(1F),
                title = "Waktu",
                value = viewModel.selectedTime.value,
                onValueChange = { viewModel.selectedTime.value = it },
                readOnly = true,
                onFocused = { timePickerDialog.show() }
            )
        }

        // Input Transaksi Bersih & Biaya Transfer
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyInputField(
                modifier = Modifier.weight(1F),
                title = "Transaksi Bersih",
                value = if (viewModel.transactionAmount.value == BigDecimal.ZERO) "0" else viewModel.transactionAmount.value.toString(),
                onValueChange = {
                    viewModel.transactionAmount.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO
                },
                prefix = { Text("Rp ") },
                keyboardType = KeyboardType.Number,
                supportingText = {
                    Text("Rp ${viewModel.transactionAmount.value.plus(viewModel.transferFeeAmount.value)}")
                }
            )

            MyInputField(
                modifier = Modifier.weight(1F),
                title = "Biaya Transfer",
                value = if (viewModel.transferFeeAmount.value == BigDecimal.ZERO) "0" else viewModel.transferFeeAmount.value.toString(),
                onValueChange = {
                    viewModel.transferFeeAmount.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO
                },
                prefix = { Text("Rp ") },
                keyboardType = KeyboardType.Number,
            )
        }

        // Input Dari & Ke Dompet
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyNestedDropdownInput(
                modifier = Modifier.weight(1F),
                title = "Dari Dompet",
                menuItemList1 = viewModel.walletGroupList.map { Pair(it!!.id, it.name) },
                menuItemList2 = viewModel.walletList.value.filter { it!!.walletGroupId == viewModel.selectedFromWalletGroup.value.first }
                    .map { Pair(it!!.id, it.name) },
                onItemSelected1 = { (walletGroupId, walletGroupName) ->
                    viewModel.selectedFromWalletGroup.value = Pair(walletGroupId, walletGroupName)
                },
                onItemSelected2 = { (walletId, walletName) ->
                    viewModel.selectedFromWallet.value = Pair(walletId, walletName)
                },
                value = if (viewModel.selectedFromWalletGroup.value.first == null) viewModel.selectedFromWalletGroup.value.second else "${viewModel.selectedFromWalletGroup.value.second}/${viewModel.selectedFromWallet.value.second}"
            )

            MyNestedDropdownInput(
                modifier = Modifier.weight(1F),
                title = "Ke Dompet",
                menuItemList1 = viewModel.walletGroupList.map { Pair(it!!.id, it.name) },
                menuItemList2 = viewModel.walletList.value.filter { it!!.walletGroupId == viewModel.selectedToWalletGroup.value.first }
                    .map { Pair(it!!.id, it.name) },
                onItemSelected1 = { (walletGroupId, walletGroupName) ->
                    viewModel.selectedToWalletGroup.value = Pair(walletGroupId, walletGroupName)
                },
                onItemSelected2 = { (walletId, walletName) ->
                    viewModel.selectedToWallet.value = Pair(walletId, walletName)
                },
                value = if (viewModel.selectedToWalletGroup.value.first == null) viewModel.selectedToWalletGroup.value.second else "${viewModel.selectedToWalletGroup.value.second}/${viewModel.selectedToWallet.value.second}"
            )
        }

        // Input Catatan
        MyInputField(
            title = "Catatan",
            value = viewModel.note.value,
            onValueChange = { viewModel.note.value = it },
            minLines = 5,
            singleLine = false
        )

        // Input Gambar
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        style = Stroke(
                            width = 4F,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20F, 20F), 0F)
                        )
                    )
                },
            shape = RoundedCornerShape(8.dp),
            border = null,
            onClick = {}
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null
                )
                Text(
                    text = "Upload Gambar"
                )
            }
        }
    }
}