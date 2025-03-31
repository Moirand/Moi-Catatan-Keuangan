package com.example.moi_catatan_keuangan.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun reformatDateTime(input: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale("id", "ID"))

    val dateTime = LocalDateTime.parse(input, inputFormatter)
    return dateTime.format(outputFormatter)
}

fun getDayName(input: String): String {
    val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val outputFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))

    val date: Date = inputFormat.parse(input) ?: return "Tanggal tidak valid"
    return outputFormat.format(date)
}

fun BigDecimal.toCurrencyFormat(): String {
    val symbols = DecimalFormatSymbols(Locale("id", "ID"))
    val formatter = DecimalFormat("#,###.##", symbols)
    return formatter.format(this)
}

fun Context.makeToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()