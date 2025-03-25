package com.example.moi_catatan_keuangan.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.constant.TransactionType
import com.example.moi_catatan_keuangan.utils.getDayName
import com.example.moi_catatan_keuangan.utils.reformatDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionListItemHome(
    modifier: Modifier = Modifier,
    dateTime: String,
    type: TransactionType,
    category: String?,
    fromWalletGroupWallet: String,
    toWalletGroupWallet: String?,
    amount: String,
    note: String?,
    isLatestTimeOfDate: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.height(136.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DateTrack(
            modifier = Modifier.weight(1F),
            date = reformatDateTime(dateTime).substring(startIndex = 0, endIndex = 13),
            isLatestTimeOfDate = isLatestTimeOfDate
        )
        TransactionCardHome(
            modifier = Modifier
                .weight(5F)
                .padding(0.dp, 8.dp)
                .clickable { onClick() },
            time = reformatDateTime(dateTime).substring(startIndex = 15),
            category = category,
            type = type,
            fromWalletGroupWallet = fromWalletGroupWallet,
            toWalletGroupWallet = toWalletGroupWallet,
            amount = amount,
            note = note,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DateTrack(
    modifier: Modifier = Modifier,
    date: String,
    isLatestTimeOfDate: Boolean
) {
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxHeight()) {
            drawLine(
                color = Color.Black,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 8f
            )
        }

        if (isLatestTimeOfDate) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(0.dp, 0.dp, 0.dp, 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.substring(startIndex = 0, endIndex = 2),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getDayName(date),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun TransactionListItemHomePreview() {
    TransactionListItemHome(
        isLatestTimeOfDate = true,
        dateTime = "02 Oktober 2023, 12:00",
        type = TransactionType.Expense,
        category = "Makanan",
        fromWalletGroupWallet = "Bank/Mandiri",
        toWalletGroupWallet = null,
        amount = "5000",
        note = "",
        onClick = {}
    )
    TransactionListItemHome(
        isLatestTimeOfDate = false,
        dateTime = "02 Oktober 2023, 12:00",
        type = TransactionType.Transfer,
        category = null,
        fromWalletGroupWallet = "Bank/Mandiri",
        toWalletGroupWallet = "Tunai/Tunai",
        amount = "10000",
        note = "",
        onClick = {}
    )
}