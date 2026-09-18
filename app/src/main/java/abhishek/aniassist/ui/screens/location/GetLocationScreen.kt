package abhishek.aniassist.ui.screens.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.data.repository.FirebaseRepository
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

@Composable
fun GetLocationScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var cityInput by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val settingsClient = remember { LocationServices.getSettingsClient(context) }

    fun fetchAndApplyLocation() {
        isLoadingLocation = true
        getLocation(context, fusedLocationClient, scope) { city, address, lat, lng ->
            isLoadingLocation = false
            if (city.isEmpty()) {
                isError = true
                statusMessage = "Couldn't detect location. Enter your city manually."
            } else {
                cityInput = city
                appViewModel.saveCity(city, address, lat, lng)
                isError = false
                statusMessage = "Location detected: $city"
            }
        }
    }

    // System "Turn on location?" dialog — like other apps show
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            fetchAndApplyLocation()
        } else {
            isError = true
            statusMessage = "Location is off. Turn it on or enter your city manually."
        }
    }

    // Ask the system whether location is on — shows the native
    // "Turn on location?" dialog instead of just an error message.
    fun runSettingsCheck() {
        val request = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10_000
        ).build()
        settingsClient.checkLocationSettings(
            com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(request).build()
        ).addOnSuccessListener {
            fetchAndApplyLocation()
        }.addOnFailureListener { e ->
            if (e is com.google.android.gms.common.api.ResolvableApiException) {
                locationSettingsLauncher.launch(
                    androidx.activity.result.IntentSenderRequest.Builder(e.resolution).build()
                )
            } else {
                isError = true
                statusMessage = "Location unavailable. Enter your city manually."
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            // Permission granted — still need to verify device location is on,
            // which shows the "Turn on location?" dialog when it isn't.
            runSettingsCheck()
        } else {
            isError = true
            statusMessage = "Location permission denied. Please enter city manually."
        }
    }

    fun checkAndRequestLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted && !coarseGranted) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            runSettingsCheck()
        }
    }

    // Auto-detect the city when the screen opens — falls back to manual entry
    // if permission is denied or detection fails.
    LaunchedEffect(Unit) { checkAndRequestLocation() }

    Box(modifier = Modifier.fillMaxSize().background(Cream)) {
        // Illustrated background — bird/leaves top, sign + dog scene bottom
        Image(
            painter = painterResource(R.drawable.select_location_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(bottom = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Map + paw-pin hero image
            Image(
                painter = painterResource(R.drawable.select_location_map),
                contentDescription = null,
                modifier = Modifier.height(170.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )

            Spacer(Modifier.height(20.dp))
            Text("Set Your City", color = Ink, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("We show reports near you", color = Ink, fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))

            OutlinedButton(
                onClick = { checkAndRequestLocation() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White, contentColor = Forest
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, Forest),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoadingLocation
            ) {
                if (isLoadingLocation) {
                    CircularProgressIndicator(color = Forest, modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.MyLocation, null, tint = Forest, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Detect My Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("— or enter city manually —", color = Ink, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it; statusMessage = "" },
                placeholder = { Text("City name", color = Sage, fontSize = 15.sp) },
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Sage, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (cityInput.isNotEmpty()) {
                        IconButton(onClick = { cityInput = ""; statusMessage = "" }) {
                            Icon(Icons.Default.Close, "Clear", tint = Sage, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Forest,
                    unfocusedBorderColor = Color(0xFFE3DDD0),
                    cursorColor = Forest,
                    focusedTextColor = Ink,
                    unfocusedTextColor = Ink
                )
            )
            Spacer(Modifier.height(12.dp))

            // Fixed-height status slot — reserved whether or not a message is
            // showing, so the Continue button never shifts when it appears
            Box(
                modifier = Modifier.height(28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (statusMessage.isNotEmpty()) {
                    Text(
                        statusMessage,
                        color = if (isError) MaterialTheme.colorScheme.error else Forest,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val sanitized = FirebaseRepository.sanitizeCity(cityInput)
                    if (sanitized.isEmpty()) {
                        isError = true
                        statusMessage = "Please enter a valid city name"
                        return@Button
                    }
                    scope.launch {
                        appViewModel.saveCity(sanitized)
                        navController.navigate(Screen.HOME) {
                            popUpTo(Screen.GET_LOCATION) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward,
                     null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Suppress("MissingPermission")
private fun getLocation(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (city: String, address: String, lat: Double, lng: Double) -> Unit
) {
    scope.launch {
        // getCurrentLocation actively requests a fresh fix — lastLocation
        // returns null on devices with no cached location, which is what
        // made detection hang forever on real devices.
        val location = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            runCatching {
                kotlinx.coroutines.withTimeoutOrNull(15_000) {
                    fusedLocationClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        com.google.android.gms.tasks.CancellationTokenSource().token
                    ).await()
                }
            }.getOrNull()
        } ?: runCatching { fusedLocationClient.lastLocation.await() }.getOrNull()

        if (location == null) { onResult("", "", 0.0, 0.0); return@launch }

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    val city = addresses.firstOrNull()?.locality ?: ""
                    val address = addresses.firstOrNull()?.getAddressLine(0) ?: ""
                    onResult(
                        FirebaseRepository.sanitizeCity(city), address,
                        location.latitude, location.longitude
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val city = addresses?.firstOrNull()?.locality ?: ""
                val address = addresses?.firstOrNull()?.getAddressLine(0) ?: ""
                onResult(
                    FirebaseRepository.sanitizeCity(city), address,
                    location.latitude, location.longitude
                )
            }
        } catch (_: Exception) {
            onResult("", "", 0.0, 0.0)
        }
    }
}
