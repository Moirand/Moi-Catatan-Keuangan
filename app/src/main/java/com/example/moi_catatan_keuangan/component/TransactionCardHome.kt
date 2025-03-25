package com.example.moi_catatan_keuangan.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.constant.TransactionType
import com.example.moi_catatan_keuangan.ui.theme.MoiCatatanKeuanganTheme

@SuppressLint("NewApi")
@Composable
fun TransactionCardHome(
    modifier: Modifier = Modifier,
    time: String,
    category: String?,
    type: TransactionType,
    fromWalletGroupWallet: String,
    toWalletGroupWallet: String?,
    amount: String,
    note: String?
) {
    Box(modifier = modifier) {
        SubCard(
            time = time,
            colors = cardColors(
                containerColor = colorScheme.primary,
                contentColor = Color.White,
            )
        )
        MainCard(
            modifier = Modifier.padding(top = 32.dp),
            category = category,
            type = type,
            fromWalletGroupWallet = fromWalletGroupWallet,
            toWalletGroupWallet = toWalletGroupWallet,
            amount = amount,
            note = note
        )
    }
}

@Composable
private fun MainCard(
    modifier: Modifier = Modifier,
    category: String?,
    type: TransactionType,
    fromWalletGroupWallet: String,
    toWalletGroupWallet: String?,
    amount: String,
    note: String?
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category ?: "Transfer",
                    fontSize = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                )
                if (type == TransactionType.Transfer) {
                    Text(
                        text = toWalletGroupWallet.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else if (note != null) {
                    Text(
                        text = note,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = amount,
                    fontSize = 24.sp,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = fromWalletGroupWallet,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun SubCard(
    time: String = "00:00",
    colors: CardColors = cardColors(
        containerColor = colorScheme.primary,
        contentColor = colorScheme.onPrimary,
    )
) {
    Card(
        modifier = Modifier.fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = colors
    ) {
        Text(
            modifier = Modifier
                .width(80.dp)
                .padding(16.dp, 8.dp),
            text = time,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun TransactionCardHomePreview() {
    MoiCatatanKeuanganTheme {
        LazyColumn {
            item {
                TransactionCardHome(
                    modifier = Modifier
                        .width(312.dp)
                        .height(120.dp),
                    time = "12:00",
                    category = "Makanan",
                    type = TransactionType.Expense,
                    fromWalletGroupWallet = "Bank/Mandiri",
                    toWalletGroupWallet = null,
                    amount = "5000",
                    note = "Lorem Ipsum Dolor Nit"
                )
            }
            item {
                TransactionCardHome(
                    modifier = Modifier
                        .width(312.dp)
                        .height(120.dp),
                    time = "12:00",
                    category = null,
                    type = TransactionType.Transfer,
                    fromWalletGroupWallet = "Bank/Mandiri",
                    toWalletGroupWallet = "Tunai/Tunai",
                    amount = "5000",
                    note = "Lorem Ipsum Dolor Nit"
                )
            }
        }
    }
}