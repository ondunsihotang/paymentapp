package com.samuelsihotang.android.home.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LandingPage(viewModel: PaymentViewModel, navController: NavController){
    val state = viewModel.userBalance.collectAsState()
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        Saldo(state.value)
        Button(modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth(), onClick = { navController.navigate("home/pay-qr") }) {
            Text(text = "QRIS Payment")
        }
    }
}

@Composable
fun Saldo(nominal: Int, modifier: Modifier = Modifier){
    Text(
        text = "Your Balance Rp.$nominal!",
        modifier = modifier
    )
}