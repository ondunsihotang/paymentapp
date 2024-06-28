package com.samuelsihotang.android.home.ui

import androidx.lifecycle.ViewModel
import com.samuelsihotang.android.qr.domain.ParseQrUseCase
import com.samuelsihotang.android.qr.domain.QrData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PaymentViewModel : ViewModel() {
    val parseQrUseCase = ParseQrUseCase()
    var userBalance = MutableStateFlow<Int>(500000)
    private val _viewState : MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Default)
    val viewState = _viewState.asStateFlow()

    fun processQr(qr: String){
        val parseResult = parseQrUseCase.execute(qr)

        if(parseResult == null){
            _viewState.value = ViewState.Navigate("home")
        } else{
            _viewState.value = ViewState.ParsedQrResult(parseResult)
        }
    }

    fun pay(qr: String){
        val parseResult = parseQrUseCase.execute(qr)
        parseResult?.let {
            if(it.amount > userBalance.value){
                _viewState.value = ViewState.Navigate("home")
            } else {
                userBalance.value -= parseResult.amount
                _viewState.value = ViewState.Navigate("home/payment-successful")
            }
        }

    }
}

sealed interface ViewState{
    data class Navigate(val target: String) : ViewState
    data class ParsedQrResult(val qrData : QrData) : ViewState
    object Default: ViewState
}