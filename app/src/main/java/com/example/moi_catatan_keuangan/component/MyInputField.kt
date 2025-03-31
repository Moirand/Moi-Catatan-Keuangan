package com.example.moi_catatan_keuangan.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun MyInputField(
    modifier: Modifier = Modifier,
    title: String = "Judul",
    value: String = "",
    onValueChange: (String) -> Unit = {},
    minLines: Int = 1,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    prefix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocused: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsPressedAsState().value

    if (isFocused) onFocused()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 16.sp
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { onValueChange(it) },
            minLines = minLines,
            readOnly = readOnly,
            singleLine = singleLine,
            prefix = prefix,
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = keyboardType
            ),
            supportingText = supportingText
        )
    }
}