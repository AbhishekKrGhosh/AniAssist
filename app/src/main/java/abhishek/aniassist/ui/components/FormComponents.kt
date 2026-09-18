package abhishek.aniassist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import abhishek.aniassist.ui.theme.*

/**
 * Bottom sheet offering "Open camera" / "Choose from gallery" — shared by
 * every photo picker in the app (report forms, proof upload, profile).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSourceSheet(
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                "Add a photo", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = Ink, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            PhotoSourceRow(Icons.Default.PhotoCamera, "Open camera", onCamera)
            PhotoSourceRow(Icons.Default.AddPhotoAlternate, "Choose from gallery", onGallery)
        }
    }
}

@Composable
private fun PhotoSourceRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Forest, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Ink)
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Ink)
}

@Composable
fun ChipRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { opt ->
            val sel = selected == opt
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (sel) Forest else Color.White)
                    .clickable { onSelect(opt) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    opt, fontSize = 13.sp,
                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (sel) Color.White else Sage,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun ModernField(
    value: String,
    placeholder: String,
    multiline: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Sage, fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        modifier = Modifier.fillMaxWidth(),
        minLines = if (multiline) 3 else 1,
        maxLines = if (multiline) 5 else 1,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Forest,
            unfocusedBorderColor = Color(0xFFE3DDD0),
            cursorColor = Forest
        )
    )
}

/**
 * "Pin location on map" row for report forms.
 * - Nothing picked → outlined button that opens the picker
 * - Location picked → white card showing the resolved address, tap to re-pick, ✕ to clear
 */
@Composable
fun MapPickRow(
    pickedAddress: String?,
    onPick: () -> Unit,
    onClear: () -> Unit
) {
    if (pickedAddress.isNullOrEmpty()) {
        OutlinedButton(
            onClick = onPick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Forest),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFC8DED2))
            )
        ) {
            Icon(Icons.Default.Map, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Pin exact location on map (optional)", fontWeight = FontWeight.SemiBold)
        }
    } else {
        // Pinned state — replaces the location text field in the form, so this
        // IS the location control: map icon, resolved address, ✕ removes pin.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPick),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9EBE1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Map, null, tint = Forest, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Pinned on map — tap to change", fontSize = 11.sp, color = Sage)
                    Text(
                        pickedAddress,
                        fontSize = 13.sp, color = Ink, fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                }
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, "Remove pinned location", tint = Sage,
                         modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/** Dashed rounded border — used by the photo-upload drop zone. */
fun Modifier.dashedBorder(color: Color, radius: Dp, width: Dp = 1.5.dp) = drawBehind {
    val stroke = Stroke(
        width = width.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(radius.toPx())
    )
}

/**
 * Form section card — soft icon circle + bold title + subtitle on top,
 * [content] (fields/chips) below. Matches the report-form mockup.
 */
@Composable
fun FormSection(
    iconRes: Int,
    iconBg: Color,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text(subtitle, fontSize = 12.sp, color = Sage)
            }
        }
        Spacer(Modifier.height(14.dp))
        content()
    }
}

/**
 * Chip row with PNG icons — selected chip is filled Forest with white
 * text + white-tinted icon; unselected is white with dark text.
 */
@Composable
fun IconChipRow(
    options: List<Pair<String, Int>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (opt, iconRes) ->
            val sel = selected == opt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (sel) Forest else Color(0xFFF1F5F0))
                    .clickable { onSelect(opt) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = if (sel) ColorFilter.tint(Color.White) else null
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    opt, fontSize = 13.sp,
                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (sel) Color.White else Color.Black
                )
            }
        }
    }
}
