package com.example.data

import com.example.data.dataSource.local.room.entity.Category
import com.example.data.dataSource.local.room.entity.Transaction
import com.example.data.dataSource.local.room.entity.Wallet
import com.example.data.dataSource.local.room.entity.WalletGroup
import com.example.domain.model.CategoryDomain
import com.example.domain.model.TransactionDomain
import com.example.domain.model.WalletDomain
import com.example.domain.model.WalletGroupDomain

fun Transaction.toDomain(): TransactionDomain =
    TransactionDomain(
        id = id,
        dateTime = dateTime,
        type = type,
        categoryId = categoryId,
        fromWalletId = fromWalletId,
        toWalletId = toWalletId,
        amount = amount,
        transferFee = transferFee,
        note = note,
        receipt = receipt
    )

fun TransactionDomain.toData(): Transaction =
    Transaction(
        id = id,
        dateTime = dateTime,
        type = type,
        categoryId = categoryId,
        fromWalletId = fromWalletId,
        toWalletId = toWalletId,
        amount = amount,
        transferFee = transferFee,
        note = note,
        receipt = receipt
    )

fun Category.toDomain(): CategoryDomain =
    CategoryDomain(
        id = id,
        name = name,
        type = type
    )

fun Wallet.toDomain(): WalletDomain =
    WalletDomain(
        id = id,
        name = name,
        balance = balance,
        walletGroupId = walletGroupId
    )

fun WalletGroup.toDomain(): WalletGroupDomain =
    WalletGroupDomain(
        id = id,
        name = name
    )