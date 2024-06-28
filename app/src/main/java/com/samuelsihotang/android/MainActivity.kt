package com.samuelsihotang.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.camera.view.PreviewView
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.camera.core.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.samuelsihotang.android.home.ui.LandingPage
import com.samuelsihotang.android.home.ui.PaymentViewModel
import com.samuelsihotang.android.home.ui.ViewState
import com.samuelsihotang.android.pin.ui.PinConfirmation
import com.samuelsihotang.android.qr.ui.BarcodeAnalyzer
import com.samuelsihotang.android.ui.theme.PaymentApplicationTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {
    var userBalance: Int = 500000
    val cameraExecutor = Executors.newSingleThreadExecutor()
    private val viewModel by viewModels<PaymentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navigationState = viewModel.viewState.collectAsState()

            PaymentApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.fillMaxHeight().fillMaxWidth().padding(innerPadding), verticalArrangement = Arrangement.Center) {
                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
                                LandingPage(viewModel, navController)
                            }

                            composable("home/pay-qr") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth().fillMaxHeight()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                )
                                {
                                    requestCameraPermission()
                                    CameraScanBarcode(
                                        cameraExecutor = cameraExecutor,
                                        navController
                                    )
                                }

                            }
                            composable(
                                "home/payment-detail/{qrData}",
                                arguments = listOf(navArgument("qrData") {
                                    type = NavType.StringType
                                })
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp), verticalArrangement = Arrangement.Center
                                ) {
                                    val data = it.arguments?.getString("qrData")
                                    PaymentDetail(qrData = data.orEmpty(), viewModel, navController)
                                }
                            }
                            composable(
                                "home/confirm-pin/{qrData}",
                                arguments = listOf(navArgument("qrData") {
                                    type = NavType.StringType
                                })
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp), verticalArrangement = Arrangement.Center
                                ) {
                                    val data = it.arguments?.getString("qrData")
                                    PinConfirmation(
                                        qrData = data.orEmpty(),
                                        navController,
                                        viewModel
                                    )
                                }
                            }
                            composable("home/payment-successful") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                                ) {
                                    paymentSuccess(navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PaymentDetail(
    qrData: String,
    viewModel: PaymentViewModel,
    navHostController: NavHostController
) {
    viewModel.processQr(qrData)
    val state = viewModel.viewState.collectAsState()

    if (state.value is ViewState.ParsedQrResult) {
        val qr = (state.value as ViewState.ParsedQrResult).qrData
        Text(text = "Merchant: " + qr.merchant.orEmpty())
        Text(text = "Price: " + qr.amount.toString().orEmpty())
        Text(text = "id: " + qr.id.orEmpty())
    }
    Text(text = "Pay?")

    Button(modifier = Modifier
        .padding(30.dp)
        .fillMaxWidth(), onClick = { navHostController.navigate("home/confirm-pin/$qrData") }) {
        Text(text = "Pay")
    }
}

@Composable
fun paymentSuccess(navHostController: NavHostController) {
    Text(text = "Payment Successful")
    Button(onClick = { navHostController.navigate("home") }) {
        Text(text = "Back to Home")
    }
}

@Composable
fun CameraScanBarcode(cameraExecutor: ExecutorService, navHostController: NavHostController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    AndroidView(factory = {
        val view = LayoutInflater.from(it).inflate(R.layout.camera_host, null, false)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        view
    }) { inflatedLayout ->

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            var preview: Preview = Preview.Builder().build()
            val previewView = inflatedLayout as PreviewView

            var cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageAnalysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                    cameraProvider.unbindAll()
                    navHostController.navigate("home/payment-detail/$barcode")
                })
            }
            try {
                cameraProvider.unbindAll()
                var camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun requestCameraPermission() {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    when (cameraPermissionState.status) {
        PermissionStatus.Granted -> {
            Text("Camera permission Granted")
        }

        is PermissionStatus.Denied -> {
            Column {
                val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                    "The camera is important for this app."
                } else {
                    "Camera permission required for this feature to be available"
                }
                Text(textToShow)
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Request Permission")
                }
            }
        }
    }
}