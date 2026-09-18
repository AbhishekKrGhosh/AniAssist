package abhishek.aniassist.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.sensor.ShakeDetector
import abhishek.aniassist.ui.components.AniImage
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val city   by appViewModel.city.collectAsStateWithLifecycle()
    val name   by appViewModel.name.collectAsStateWithLifecycle()
    val email  by appViewModel.email.collectAsStateWithLifecycle()
    val avatar by appViewModel.avatar.collectAsStateWithLifecycle()

    var filter by remember { mutableStateOf(FeedFilter.ALL) }
    var query  by remember { mutableStateOf("") }

    val displayName = name.ifEmpty {
        email.substringBefore("@").replaceFirstChar { it.uppercase() }.ifEmpty { "there" }
    }

    // Shake → Profile (same as old app)
    val shakeDetector = remember { ShakeDetector(context) }
    DisposableEffect(Unit) {
        shakeDetector.setShakeListener(object : ShakeDetector.ShakeListener {
            override fun onShakeDetected() { navController.navigate(Screen.PROFILE) }
        })
        shakeDetector.startListening()
        onDispose { shakeDetector.stopListening() }
    }

    // Merged feed: Post + Lost + Found, newest first
    val feed = rememberCaseFeed(city)
    val visibleItems = feed.items
        .filter { filter == FeedFilter.ALL || it.filter == filter }
        .filter {
            query.isBlank() ||
            it.title.contains(query, true) || it.subtitle.contains(query, true)
        }
        .take(10) // home shows a preview; "View all" opens the full list

    Scaffold(
        containerColor = Cream
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Leaf frame around the whole screen
            Image(
                painter = painterResource(R.drawable.home_bg_frame),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.45f
            )
            // Denser foliage strip anchored to the bottom edge
            Image(
                painter = painterResource(R.drawable.home_bg_bottom),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                // ── Greeting + avatar ────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Good to see you,", fontSize = 15.sp, color = SageDeep)
                        Text(
                            "$displayName 👋",
                            fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Ink
                        )
                        Text("Let's help an animal today.", fontSize = 13.sp, color = SageDeep)
                    }

                    // profile_ring asset has leaves arranged around a clear
                    // centre — drop the circular avatar into it. The tagline
                    // text is baked into the asset.
                    Box(
                        modifier = Modifier.size(118.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.profile_ring),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        // Avatar sized/positioned as fractions of the ring —
                        // tuned visually, scales with any device density.
                        val ringBox = 118.dp
                        val avatarSize = ringBox * 0.50f
                        val avatarYOffset = ringBox * -0.065f
                        Box(
                            modifier = Modifier
                                .size(avatarSize)
                                .align(Alignment.Center)
                                .offset(y = avatarYOffset)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { navController.navigate(Screen.PROFILE) }
                        ) {
                            if (avatar.isNotEmpty()) {
                                AniImage(
                                    imageRef = avatar,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.avatar_default),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { scaleX = 1.3f; scaleY = 1.3f },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ── Search ───────────────────────────────────────────────────
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text("Search animals, cases, or places…", color = Sage, fontSize = 14.sp)
                    },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Sage) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(Modifier.height(12.dp))

                // ── City row ─────────────────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate(Screen.GET_LOCATION) }
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = Ink, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        city.replaceFirstChar { it.uppercase() }.ifEmpty { "Select your city" },
                        fontSize = 15.sp, color = Ink, fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = Sage,
                         modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text("Change", fontSize = 12.sp, color = Forest,
                             fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(18.dp))

                // ── Action cards ─────────────────────────────────────────────
                // Full-width injured card
                ActionCardWide(
                    title = "Report Injured Animal",
                    subtitle = "Help an animal in need",
                    iconRes = R.drawable.btn_injured_icon,
                    imageRes = R.drawable.btn_injured_img,
                    bg = CoralSoft,
                    arrowColor = Coral,
                    onClick = { navController.navigate(Screen.POST) }
                )

                Spacer(Modifier.height(12.dp))

                // Two half-width cards: lost + found
                Row(
                    Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCardHalf(
                        Modifier.weight(1f).fillMaxHeight(),
                        title = "Report Lost Pet",
                        subtitle = "Help them find\ntheir way home",
                        iconRes = R.drawable.btn_lost_icon,
                        imageRes = R.drawable.btn_lost_img,
                        bg = Color(0xFFDFF2E4),
                        arrowColor = Forest,
                        onClick = { navController.navigate(Screen.LOST) }
                    )
                    ActionCardHalf(
                        Modifier.weight(1f).fillMaxHeight(),
                        title = "Report Found Animal",
                        subtitle = "Found an animal?\nLet's reunite them",
                        iconRes = R.drawable.btn_found_icon,
                        imageRes = R.drawable.btn_found_img,
                        bg = SkySoft,
                        arrowColor = Sky,
                        onClick = { navController.navigate(Screen.FOUND) }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Full-width trivia card
                ActionCardWide(
                    title = "Trivia & Fun",
                    subtitle = "Learn, play and be a\nbetter friend to animals",
                    iconRes = R.drawable.btn_trivia_icon,
                    imageRes = R.drawable.btn_trivia_img,
                    bg = LilacSoft,
                    arrowColor = Lilac,
                    onClick = { navController.navigate(Screen.TRIVIA) }
                )

                Spacer(Modifier.height(16.dp))

                // ── Banner — text baked into the asset ───────────────────────
                Image(
                    painter = painterResource(R.drawable.banner_report),
                    contentDescription = "Every report counts",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.FillWidth
                )

                Spacer(Modifier.height(22.dp))

                // ── Nearby cases — frosted gradient panel, fades at bottom ───
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Cream.copy(alpha = 0.95f), Cream.copy(alpha = 0.65f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nearby Cases", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                             color = Ink, modifier = Modifier.weight(1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { navController.navigate(Screen.NEARBY_CASES) }
                        ) {
                            Text("View all", fontSize = 13.sp, color = Forest,
                                 fontWeight = FontWeight.SemiBold)
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                                 tint = Forest, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Filter chips
                    FeedFilterRow(selected = filter) { filter = it }

                    // Empty-state art lives inside the panel too
                    if (feed.isLoading) {
                        Box(
                            Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Forest) }
                    } else if (visibleItems.isEmpty()) {
                        Column(
                            Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.empty_feed),
                                contentDescription = null,
                                modifier = Modifier.height(130.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "No nearby cases yet",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "You're all caught up! Check another area\nto help animals nearby.",
                                fontSize = 12.sp, color = SageDeep,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = { navController.navigate(Screen.GET_LOCATION) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White, contentColor = Forest
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Forest)
                                )
                            ) {
                                Icon(Icons.Default.LocationOn, null,
                                     tint = Forest, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Explore nearby areas", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // ── Feed — below the panel ───────────────────────────────────
                if (visibleItems.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    visibleItems.forEach { item ->
                        CaseCard(item) { item.onOpen(appViewModel, navController) }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ─── Components ──────────────────────────────────────────────────────────────

/** Full-width action card: icon tile + title/subtitle left, animal art right,
 *  arrow circle bottom-right. */
@Composable
private fun ActionCardWide(
    title: String,
    subtitle: String,
    iconRes: Int,
    imageRes: Int,
    bg: Color,
    arrowColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x59000000), spotColor = Color(0x66000000))
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon PNG already has its coloured tile baked in — render as-is
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(54.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Ink)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = SageDeep)
            }
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.height(88.dp),
                contentScale = ContentScale.Fit
            )
        }

        // 3D bottom edge — darker shade of the card colour
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(7.dp)
                .background(bg.darkened(0.18f))
        )

        // Arrow circle
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(arrowColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward, null,
                tint = Color.White, modifier = Modifier.size(16.dp)
            )
        }
    }
}

/** Half-width action card: icon top-left, text, animal art bottom-left,
 *  arrow circle bottom-right. */
@Composable
private fun ActionCardHalf(
    modifier: Modifier,
    title: String,
    subtitle: String,
    iconRes: Int,
    imageRes: Int,
    bg: Color,
    arrowColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x59000000), spotColor = Color(0x66000000))
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
            }
            Spacer(Modifier.height(6.dp))
            Text(subtitle, fontSize = 10.sp, color = SageDeep)
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .height(72.dp)
                    .align(Alignment.Start),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(8.dp))
        }

        // 3D bottom edge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(7.dp)
                .background(bg.darkened(0.18f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(30.dp)
                .clip(CircleShape)
                .background(arrowColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward, null,
                tint = Color.White, modifier = Modifier.size(15.dp)
            )
        }
    }
}

/** Darker muted text for this screen — Sage is too light over the foliage. */
private val SageDeep = Color.Black

/** Darken a colour by [fraction] — used for the 3D bottom edge on cards. */
private fun Color.darkened(fraction: Float): Color = Color(
    red = red * (1f - fraction),
    green = green * (1f - fraction),
    blue = blue * (1f - fraction),
    alpha = alpha
)
