package com.example.domain.model

data class TransactionDetailDomain(
    val transaction: TransactionDomain,
    val category: CategoryDomain?,
    val fromWallet: WalletDomain,
    val toWallet: WalletDomain?,
    val fromWalletGroup: WalletGroupDomain,
    val toWalletGroup: WalletGroupDomain?
)