package com.example.domain.model

import java.math.BigDecimal

data class WalletDomain(
    val id: Int = 0,
    val name: String,
    val balance: BigDecimal,
    val walletGroupId: Int
)