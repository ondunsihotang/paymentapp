package com.samuelsihotang.android.pin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samuelsihotang.android.home.ui.PaymentViewModel
import com.samuelsihotang.android.home.ui.ViewState

@Composable
fun PinConfirmation(qrData: String, navController: NavController, viewModel: PaymentViewModel) {
    val state = viewModel.viewState.collectAsState()

    if (state.value is ViewState.Navigate) {
        val _navigate = (state.value as ViewState.Navigate).target
        LaunchedEffect(_navigate) {
            if (_navigate.isNotEmpty()) {
                navController.navigate(_navigate)
            }
        }
    }

    val maxLength: Int = 6
    val pattern = remember { Regex("^\\d+\$") }
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = text,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            onValueChange = {
                if ((it.isEmpty() || it.matches(pattern)) && it.length <= maxLength) {
                    text = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
        Button(modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth(),
            onClick = {
                if (state.value is ViewState.ParsedQrResult) {
                    val qr = (state.value as ViewState.ParsedQrResult).qrData
                    qr.amount?.let { viewModel.pay(qrData) }
                }
            }) {
            Text(text = "Confirm")
        }
    }
}