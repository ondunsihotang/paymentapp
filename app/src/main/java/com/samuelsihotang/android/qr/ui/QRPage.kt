package com.samuelsihotang.android.qr.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun QRPay(qrData: String, navController: NavController){
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
        Button(modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth(), onClick = { navController.navigate("home/confirm-pin/$qrData") }) {
            Text(text = "Bayar")
        }
    }
}