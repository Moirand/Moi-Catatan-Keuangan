package com.example.moi_catatan_keuangan.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNestedDropdownInput(
    modifier: Modifier = Modifier,
    title: String,
    menuItemList1: List<Pair<Int, String>>,
    menuItemList2: List<Pair<Int, String>>? = null,
    onItemSelected1: (Pair<Int?, String>) -> Unit = {},
    onItemSelected2: (Pair<Int?, String>) -> Unit = {},
    value: String,
) {
    var expanded by remember { mutableStateOf(false) }

    var selectedItem1 by remember { mutableStateOf("") }
    var selectedItem2 by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 16.sp
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Pilih opsi") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
            )

            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                if (menuItemList2.isNullOrEmpty()) {
                    menuItemList1.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.second) },
                            onClick = {
                                onItemSelected1(item)
                                selectedItem1 = item.second
                                expanded = false
                            }
                        )
                    }
                } else {
                    if (selectedItem1.isEmpty()) {
                        menuItemList1.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.second) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = ""
                                    )
                                },
                                onClick = {
                                    onItemSelected1(item)
                                    selectedItem1 = item.second
                                }
                            )
                        }
                    } else {
                        DropdownMenuItem(
                            text = { Text("Kembali") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                onItemSelected1(Pair(null, ""))
                                onItemSelected2(Pair(null, ""))
                                selectedItem1 = ""
                                selectedItem2 = ""
                            }
                        )
                        menuItemList2.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.second) },
                                onClick = {
                                    onItemSelected2(item)
                                    selectedItem2 = item.second
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}