package abhishek.aniassist.ui.screens.map

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun MapPickerScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Start on the user's detected city coords (saved during location setup);
    // fall back to India centre if location was never detected.
    val savedLat by appViewModel.savedLat.collectAsStateWithLifecycle()
    val savedLng by appViewModel.savedLng.collectAsStateWithLifecycle()
    val defaultPos = if (savedLat != null && savedLng != null)
        LatLng(savedLat!!, savedLng!!) else LatLng(28.6139, 77.2090)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 15f)
    }

    var resolvedAddress by remember { mutableStateOf("Move the map to choose a location") }
    var hasUserLocation by remember { mutableStateOf(false) }

    // Geocode sequence — only the newest request may write the address.
    // Fixes the race where the default-position geocode lands after the
    // GPS-jump geocode and overwrites the real address.
    var geocodeSeq by remember { mutableIntStateOf(0) }

    // Jump to the user's real location once — free FusedLocationProvider, no API.
    // A direct `position =` set doesn't toggle isMoving, so geocode it here too.
    LaunchedEffect(Unit) {
        fetchCurrentLocation(context)?.let { latLng ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 16f)
            hasUserLocation = true
            val seq = ++geocodeSeq
            val addr = reverseGeocode(context, latLng)
            if (seq == geocodeSeq) resolvedAddress = addr ?: "Dropped pin"
        }
    }

    // Reverse-geocode wherever the camera stops — android.location.Geocoder is
    // the free OS-level geocoder, NOT the paid Google Geocoding API.
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val target = cameraPositionState.position.target
            val seq = ++geocodeSeq
            val addr = reverseGeocode(context, target)
            if (seq == geocodeSeq) resolvedAddress = addr ?: "Dropped pin"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasUserLocation),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        )

        // Fixed centre pin — the map moves underneath it
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-28).dp), // pin tip lands on the map centre
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.map_pin_paw),
                contentDescription = "Picked location",
                modifier = Modifier.size(56.dp)
            )
        }

        // Back button
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Ink)
            }
        }

        // Bottom stack: recenter button sits directly above the address card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier.align(Alignment.End),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Forest),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            fetchCurrentLocation(context)?.let { latLng ->
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(latLng, 16f)
                                val seq = ++geocodeSeq
                                val addr = reverseGeocode(context, latLng)
                                if (seq == geocodeSeq) resolvedAddress = addr ?: "Dropped pin"
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.MyLocation, "Go to my location", tint = Color.White)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Resolved address + confirm
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Selected location", fontSize = 12.sp, color = Sage, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(
                    resolvedAddress,
                    fontSize = 14.sp, color = Ink, fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        val target = cameraPositionState.position.target
                        appViewModel.pickedLat = target.latitude
                        appViewModel.pickedLng = target.longitude
                        appViewModel.pickedAddress = resolvedAddress
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Confirm this location", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
            }
        }
    }
}

@SuppressLint("MissingPermission") // app already requests location permission upstream
private suspend fun fetchCurrentLocation(context: android.content.Context): LatLng? =
    withContext(Dispatchers.IO) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        // Active fix first (lastLocation is often null on fresh devices)
        val loc = runCatching {
            kotlinx.coroutines.withTimeoutOrNull(15_000) {
                client.getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    com.google.android.gms.tasks.CancellationTokenSource().token
                ).await()
            }
        }.getOrNull() ?: runCatching { client.lastLocation.await() }.getOrNull()
        loc?.let { LatLng(it.latitude, it.longitude) }
    }

private suspend fun reverseGeocode(context: android.content.Context, latLng: LatLng): String? =
    withContext(Dispatchers.IO) {
        runCatching {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var out: String? = null
                var done = false
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                    out = addresses.firstOrNull()?.getAddressLine(0)
                    done = true
                }
                while (!done) kotlinx.coroutines.delay(50)
                out
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    ?.firstOrNull()?.getAddressLine(0)
            }
        }.getOrNull()
    }
