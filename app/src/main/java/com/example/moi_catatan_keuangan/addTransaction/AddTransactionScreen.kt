package com.example.moi_catatan_keuangan.addTransaction

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.domain.constant.CategoryType
import com.example.domain.constant.TransactionType
import com.example.domain.model.CategoryDomain
import com.example.domain.model.TransactionDomain
import com.example.domain.model.WalletDomain
import com.example.domain.model.WalletGroupDomain
import com.example.moi_catatan_keuangan.component.MyInputField
import com.example.moi_catatan_keuangan.component.MyNestedDropdownInput
import com.example.moi_catatan_keuangan.utils.makeToast
import com.example.moi_catatan_keuangan.utils.toCurrencyFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.util.Calendar

@Preview(showBackground = true)
@Composable
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    viewModel: AddTransactionViewModel = koinViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    onSave: (isSaved: Boolean) -> Unit = {}, // Callback untuk memberitahukan bahwa transaksi berhasil disimpan
    onSaveButtonClick: (() -> Unit) -> Unit = {} // Callback untuk mengirimkan aksi klik tombol save
) {
    val activeButtonContainerColor = Color.Blue
    val activeButtonContentColor = Color.White
    val inActiveButtonContainerColor = Color.White
    val inActiveButtonContentColor = Color.Blue

    var transactionType by remember { mutableStateOf(TransactionType.Income) }
    var isSaveButtonClicked by remember { mutableStateOf(false) } // State untuk menandakan tombol save di TopBar diklik

    // Kirimkan aksi yang akan dijalankan ketika tombol save di TopBar diklik
    onSaveButtonClick {
        isSaveButtonClicked = true
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
                    containerColor = if (transactionType == TransactionType.Income) activeButtonContainerColor else inActiveButtonContainerColor,
                    contentColor = if (transactionType == TransactionType.Income) activeButtonContentColor else inActiveButtonContentColor
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = activeButtonContainerColor,
                ),
                onClick = {
                    transactionType = TransactionType.Income
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
                    containerColor = if (transactionType == TransactionType.Expense) activeButtonContainerColor else inActiveButtonContainerColor,
                    contentColor = if (transactionType == TransactionType.Expense) activeButtonContentColor else inActiveButtonContentColor
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = activeButtonContainerColor,
                ),
                onClick = {
                    transactionType = TransactionType.Expense
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
                    containerColor = if (transactionType == TransactionType.Transfer) activeButtonContainerColor else inActiveButtonContainerColor,
                    contentColor = if (transactionType == TransactionType.Transfer) activeButtonContentColor else inActiveButtonContentColor
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = activeButtonContainerColor,
                ),
                onClick = {
                    transactionType = TransactionType.Transfer
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

        if (transactionType == TransactionType.Transfer) TransferContent()
        else IncomExpenseContent(
            viewModel = viewModel,
            transactionType = transactionType,
            isSaveButtonClicked = isSaveButtonClicked,
            onSaveSuccess = { toast ->
                toast()
                onSave(true)
                isSaveButtonClicked = false
            },
            onSaveFailed = { toast ->
                toast()
                onSave(false)
                isSaveButtonClicked = false
            }
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun IncomExpenseContent(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: AddTransactionViewModel,
    transactionType: TransactionType,
    isSaveButtonClicked: Boolean = false,
    onSaveSuccess: (() -> Unit) -> Unit = {},
    onSaveFailed: (() -> Unit) -> Unit = {}
) {
    val categoryList: List<CategoryDomain> = listOf(
        CategoryDomain(
            id = 1,
            name = "Makanan",
            type = CategoryType.Expense
        ),
        CategoryDomain(
            id = 2,
            name = "Minuman",
            type = CategoryType.Expense
        ),
        CategoryDomain(
            id = 3,
            name = "Gaji",
            type = CategoryType.Income
        )
    )
    val walletGroupList: List<WalletGroupDomain> = listOf(
        WalletGroupDomain(
            id = 1,
            name = "Tunai",
        ),
        WalletGroupDomain(
            id = 2,
            name = "Bank",
        ),
        WalletGroupDomain(
            id = 3,
            name = "E-Wallet",
        )
    )
    val walletList: List<WalletDomain> = listOf(
        WalletDomain(
            id = 1,
            name = "Mandiri",
            balance = 0.toBigDecimal(),
            walletGroupId = 1
        ),
        WalletDomain(
            id = 2,
            name = "BCA",
            balance = 0.toBigDecimal(),
            walletGroupId = 1
        ),
        WalletDomain(
            id = 3,
            name = "BNI",
            balance = 0.toBigDecimal(),
            walletGroupId = 1
        )
    )

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var transactionAmount by remember { mutableStateOf(BigDecimal.ZERO) }
    var selectedCategory by remember { mutableStateOf(Pair<Int?, String>(null, "")) }
    var selectedWalletGroup by remember { mutableStateOf(Pair<Int?, String>(null, "")) }
    var selectedWallet by remember { mutableStateOf(Pair<Int?, String>(null, "")) }
    var note by remember { mutableStateOf("") }

    fun saveTransaction() {
        if (selectedDate.isNotEmpty()
            && selectedTime.isNotEmpty()
            && transactionAmount > BigDecimal.ZERO
            && selectedCategory.first != null
            && selectedWallet.first != null
        ) {
            scope.launch {
                val transaction = TransactionDomain(
                    dateTime = "$selectedDate $selectedTime",
                    type = transactionType,
                    categoryId = selectedCategory.first,
                    fromWalletId = selectedWallet.first!!,
                    amount = transactionAmount,
                    note = note
                )

                viewModel.insertTransaction(transaction).collect { state ->
                    when (state) {
                        is UiState.Loading -> {}
                        is UiState.Error -> {
                            onSaveFailed { context.makeToast("Transaksi Gagal Ditambahkan") }
                        }

                        is UiState.Success -> {
                            onSaveSuccess { context.makeToast("Transaksi Berhasil Ditambahkan") }
                        }
                    }
                }
            }
        } else {
            onSaveFailed { context.makeToast("Mohon lengkapi data transaksi") }
        }
    }

    LaunchedEffect(isSaveButtonClicked) {
        if (isSaveButtonClicked) {
            saveTransaction()
        }
    }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            selectedTime = formattedTime
        },
        hour, minute, true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate =
                String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
            selectedDate = formattedDate
        },
        year, month, day
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Tanggal & Waktu
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyInputField(
                modifier = Modifier
                    .weight(1F)
                    .clickable { datePickerDialog.show() },
                title = "Tanggal",
                value = selectedDate,
                onValueChange = { selectedDate = it },
                readOnly = true
            )
            MyInputField(
                modifier = Modifier
                    .weight(1F)
                    .clickable { timePickerDialog.show() },
                title = "Waktu",
                value = selectedTime,
                onValueChange = { selectedTime = it },
                readOnly = true
            )
        }

        // Input Nominal Transaksi
        MyInputField(
            title = "Total Transaksi",
            value = if (transactionAmount == BigDecimal.ZERO) "" else transactionAmount.toString(),
            onValueChange = { transactionAmount = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
            prefix = { Text("Rp ") },
            keyboardType = KeyboardType.Number
        )

        // Input Kategori
        MyNestedDropdownInput(
            title = "Kategori",
            menuItemList1 = categoryList.map { Pair(it.id, it.name) },
            onItemSelected1 = { (categoryId, categoryName) ->
                selectedCategory = Pair(categoryId, categoryName)
            },
            value = selectedCategory.second
        )

        // Input Dompet
        MyNestedDropdownInput(
            title = "Dompet",
            menuItemList1 = walletGroupList.map { Pair(it.id, it.name) },
            menuItemList2 = walletList.map { Pair(it.id, it.name) },
            onItemSelected1 = { (walletGroupId, walletGroupName) ->
                selectedWalletGroup = Pair(walletGroupId, walletGroupName)
            },
            onItemSelected2 = { (walletId, walletGroupName) ->
                selectedWallet = Pair(walletId, walletGroupName)
            },
            value = if (selectedWalletGroup.first == null) selectedWalletGroup.second else "${selectedWalletGroup.second}/${selectedWallet.second}"
        )

        // Input Catatan
        MyInputField(
            title = "Catatan",
            value = note,
            onValueChange = { note = it },
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

@SuppressLint("DefaultLocale")
@Composable
private fun TransferContent(
    context: Context = LocalContext.current,
    onSaveButtonClick: (() -> Unit) -> Unit = {}
) {
    val walletGroupList: List<WalletGroupDomain> = listOf(
        WalletGroupDomain(
            id = 1,
            name = "Tunai",
        ),
        WalletGroupDomain(
            id = 2,
            name = "Bank",
        ),
        WalletGroupDomain(
            id = 3,
            name = "E-Wallet",
        )
    )
    val walletList: List<WalletDomain> = listOf(
        WalletDomain(
            id = 1,
            name = "Mandiri",
            balance = 0.toBigDecimal(),
            walletGroupId = 1
        ),
        WalletDomain(
            id = 2,
            name = "BCA",
            balance = 0.toBigDecimal(),
            walletGroupId = 1
        ),
        WalletDomain(
            id = 3,
            name = "BNI",
            balance = 0.toBigDecimal(),
            walletGroupId = 1
        )
    )

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var transactionAmount by remember { mutableStateOf(BigDecimal.ZERO) }
    var transferFee by remember { mutableStateOf(BigDecimal.ZERO) }
    var selectedFromWalletGroup by remember { mutableStateOf(Pair<Int?, String>(null, "")) }
    var selectedFromWallet by remember { mutableStateOf(Pair<Int?, String>(null, "")) }
    var selectedToWalletGroup by remember { mutableStateOf(Pair<Int?, String>(null, "")) }
    var selectedToWallet by remember { mutableStateOf(Pair<Int?, String>(null, "")) }
    var note by remember { mutableStateOf("") }

    onSaveButtonClick {
    }

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
            selectedDate = formattedDate
        },
        year, month, day
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            selectedTime = formattedTime
        },
        hour, minute, true
    )


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
                value = selectedDate,
                onValueChange = { selectedDate = it },
                readOnly = true,
                onFocused = { datePickerDialog.show() }
            )
            MyInputField(
                modifier = Modifier
                    .weight(1F),
                title = "Waktu",
                value = selectedTime,
                onValueChange = { selectedTime = it },
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
                value = transactionAmount.toCurrencyFormat(),
                onValueChange = {
                    val cleanString = it.replace(Regex("\\D"), "")
                    transactionAmount = BigDecimal(cleanString).movePointLeft(2)
                },
                prefix = { Text("Rp ") },
                keyboardType = KeyboardType.Number,
                supportingText = {
                    Text(
                        "Rp ${
                            transactionAmount.plus(transferFee).toCurrencyFormat()
                        }"
                    )
                }
            )

            MyInputField(
                modifier = Modifier.weight(1F),
                title = "Biaya Transfer",
                value = transferFee.toCurrencyFormat(),
                onValueChange = {
                    val cleanString = it.replace(Regex("\\D"), "")
                    transferFee = BigDecimal(cleanString).movePointLeft(2)
                },
                prefix = { Text("Rp ") },
                keyboardType = KeyboardType.Number
            )
        }
        // Input Dari & Ke Dompet
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyNestedDropdownInput(
                modifier = Modifier.weight(1F),
                title = "Dari Dompet",
                menuItemList1 = walletGroupList.map { Pair(it.id, it.name) },
                menuItemList2 = walletList.map { Pair(it.id, it.name) },
                onItemSelected1 = { (walletGroupId, walletGroupName) ->
                    selectedFromWalletGroup = Pair(walletGroupId, walletGroupName)
                },
                onItemSelected2 = { (walletId, walletName) ->
                    selectedFromWallet = Pair(walletId, walletName)
                },
                value = if (selectedFromWalletGroup.first == null) selectedFromWalletGroup.second else "${selectedFromWalletGroup.second}/${selectedFromWallet.second}"
            )

            MyNestedDropdownInput(
                modifier = Modifier.weight(1F),
                title = "Ke Dompet",
                menuItemList1 = walletGroupList.map { Pair(it.id, it.name) },
                menuItemList2 = walletList.map { Pair(it.id, it.name) },
                onItemSelected1 = { (walletGroupId, walletGroupName) ->
                    selectedToWalletGroup = Pair(walletGroupId, walletGroupName)
                },
                onItemSelected2 = { (walletId, walletName) ->
                    selectedToWallet = Pair(walletId, walletName)
                },
                value = if (selectedToWalletGroup.first == null) selectedToWalletGroup.second else "${selectedToWalletGroup.second}/${selectedToWallet.second}"
            )
        }

        // Input Catatan
        MyInputField(
            title = "Catatan",
            value = note,
            onValueChange = { note = it },
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