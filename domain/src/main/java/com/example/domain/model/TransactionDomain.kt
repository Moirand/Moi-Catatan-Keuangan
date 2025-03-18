package com.example.domain.model

import com.example.domain.constant.TransactionType
import java.math.BigDecimal

data class TransactionDomain(
    val id: Int = 0,
    val dateTime: String,
    val type: TransactionType,
    val categoryId: Int?,
    val fromWalletId: Int,
    val toWalletId: Int?,
    val amount: BigDecimal,
    val transferFee: BigDecimal,
    val note: String?,
    val receipt: ByteArray?
)