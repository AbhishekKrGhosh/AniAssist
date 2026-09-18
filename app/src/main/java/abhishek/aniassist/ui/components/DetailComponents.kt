package abhishek.aniassist.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import abhishek.aniassist.R
import abhishek.aniassist.ui.theme.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Shared detail-page layout matching the mockups:
 * full-width hero photo, floating back button, badge chip,
 * info rows in white cards, and a primary action button at the bottom.
 */
@Composable
fun DetailScaffold(
    imageUri: String,
    title: String,
    badge: String,
    badgeColor: Color,
    badgeBg: Color,
    onBack: () -> Unit,
    onImageClick: () -> Unit,
    actionLabel: String,
    onAction: () -> Unit,
    infoRows: List<Pair<String, String>>,
    latitude: Double? = null,
    longitude: Double? = null,
    secondaryAction: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().background(Cream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero photo — scrolls away with the content ───────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().clickable(onClick = onImageClick)) {
                    AniImage(imageRef = imageUri, modifier = Modifier.fillMaxSize())
                }
                // "Animals need us" pill — bottom-left, clears the card overlap
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(R.drawable.animals_needs_us),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 32.dp)
                        .height(46.dp)
                )
            }

            // ── Content card — overlaps the photo slightly ───────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Cream)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Title + badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title,
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(badgeBg)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(badge, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                    }
                }

                Spacer(Modifier.height(16.dp))

                infoRows.forEach { (label, value) ->
                    DetailInfoRow(label, value)
                    Spacer(Modifier.height(10.dp))
                }

                // ── Location map + Navigate (only when coords were pinned) ───
                if (latitude != null && longitude != null) {
                    val pinPos = LatLng(latitude, longitude)
                    val camState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(pinPos, 15f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = camState,
                            uiSettings = MapUiSettings(
                                scrollGesturesEnabled = false,
                                zoomGesturesEnabled = false,
                                tiltGesturesEnabled = false,
                                rotationGesturesEnabled = false,
                                mapToolbarEnabled = false
                            )
                        ) {
                            Marker(state = MarkerState(position = pinPos), title = title)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Free navigation — fires the Google Maps app, no Directions API
                    OutlinedButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("google.navigation:q=$latitude,$longitude")
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White, contentColor = Forest
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFC8DED2))
                        )
                    ) {
                        Icon(Icons.Default.Navigation, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Navigate to this location", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(actionLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                secondaryAction?.let {
                    Spacer(Modifier.height(10.dp))
                    it()
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        // ── Floating back button over the photo ──────────────────────────
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x99000000))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, "Back",
                tint = Color.White, modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** Pastel card style per info-row type — matches the detail mockup:
 *  3D icon on the left, faint trail/accent art on the right. */
private data class DetailRowStyle(
    val cardBg: Color,
    val iconRes: Int,
    val accentRes: Int
)

private fun styleFor(label: String): DetailRowStyle = when {
    label.contains("problem", true) -> DetailRowStyle(
        Color(0xFFF1F6F1), R.drawable.ic3_report_alert, R.drawable.ic3_paw_trail)
    label.contains("animal", true) || label.contains("type", true) -> DetailRowStyle(
        Color(0xFFF1F6F1), R.drawable.ic3_dog, R.drawable.ic3_paw_trail)
    label.contains("location", true) || label.contains("seen", true) ||
        label.contains("found", true) -> DetailRowStyle(
        Color(0xFFFCF0F0), R.drawable.ic3_location, R.drawable.ic3_location_trail)
    label.contains("contact", true) || label.contains("phone", true) ||
        label.contains("reach", true) -> DetailRowStyle(
        Color(0xFFE7EEF9), R.drawable.ic3_contact, R.drawable.ic3_accent_squiggle)
    label.contains("owner", true) || label.contains("name", true) -> DetailRowStyle(
        Color(0xFFEFE9F7), R.drawable.ic3_owner_user, R.drawable.ic3_heart_trail)
    label.contains("reported", true) || label.contains("posted", true) ||
        label.contains("date", true) -> DetailRowStyle(
        Color(0xFFEFE9F7), R.drawable.ic3_date_posted, R.drawable.ic3_accent_purple)
    label.contains("description", true) || label.contains("where", true) -> DetailRowStyle(
        Color(0xFFFBF1DC), R.drawable.ic3_details, R.drawable.ic3_accent_yellow)
    else -> DetailRowStyle(
        Color(0xFFF1F6F1), R.drawable.ic3_document, R.drawable.ic3_accent_squiggle)
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String
) {
    val style = styleFor(label)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = style.cardBg),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(style.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        label.uppercase(), fontSize = 11.sp, color = Sage,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(value, fontSize = 14.sp, color = Ink, fontWeight = FontWeight.SemiBold)
                }
                // Reserve space so text never runs under the accent art
                Spacer(Modifier.width(64.dp))
            }
            // Faint accent art, right edge — matches the mockup cards
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(style.accentRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .height(44.dp)
            )
        }
    }
}
