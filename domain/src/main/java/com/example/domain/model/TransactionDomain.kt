package com.example.domain.model

import com.example.domain.constant.TransactionType
import java.math.BigDecimal

data class TransactionDomain(
    val id: Int = 0,
    val dateTime: String,
    val type: TransactionType,
    val categoryId: Int? = null,
    val fromWalletId: Int,
    val toWalletId: Int? = null,
    val amount: BigDecimal = BigDecimal.ZERO,
    val transferFee: BigDecimal = BigDecimal.ZERO,
    val note: String? = null,
    val receipt: ByteArray? = null
)