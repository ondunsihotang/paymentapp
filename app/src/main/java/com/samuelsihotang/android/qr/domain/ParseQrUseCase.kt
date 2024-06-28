package com.samuelsihotang.android.qr.domain

class ParseQrUseCase {
    fun execute(qr : String) : QrData?{
        val split = qr.split(".")
        return if (split.size == 4){
            QrData(split[0], split[1], split[2], split[3].toInt())
        } else {
            null
        }
    }
}