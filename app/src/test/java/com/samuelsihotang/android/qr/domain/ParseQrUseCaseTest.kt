package com.samuelsihotang.android.qr.domain

import org.junit.Assert.*

import org.junit.Test

class ParseQrUseCaseTest {
    val parseQrUseCase = ParseQrUseCase()
    @Test
    fun runParseQr_dataCorrect_returnQrData() {
        var result = parseQrUseCase.execute(qr = "BNI.ID12345678.MERCHANT MOCK TEST.50000")
        assertNotNull(result)
    }

    @Test
    fun runParseQr_dataIncorrect_returnNull() {
        var result = parseQrUseCase.execute(qr = "BNI.ID12345678.MERCHANT MOCK TEST.50000.988")
        assertNull(result)
    }
}