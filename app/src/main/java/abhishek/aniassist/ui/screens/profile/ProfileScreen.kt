package abhishek.aniassist.ui.screens.profile

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import abhishek.aniassist.R
import abhishek.aniassist.data.repository.FirebaseRepository
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.components.AniImage
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentCity by appViewModel.city.collectAsStateWithLifecycle()
    val name by appViewModel.name.collectAsStateWithLifecycle()
    val email by appViewModel.email.collectAsStateWithLifecycle()
    val avatar by appViewModel.avatar.collectAsStateWithLifecycle()

    var newCity by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showCityEditor by remember { mutableStateOf(false) }
    var showNameEditor by remember { mutableStateOf(false) }
    var isAvatarUploading by remember { mutableStateOf(false) }
    var showAvatarOptions by remember { mutableStateOf(false) }

    fun uploadAvatar(uri: Uri) {
        isAvatarUploading = true
        val oldRef = avatar   // same path each time would leave the ref unchanged
        scope.launch {
            // Unique path per upload → the image ref changes → UI refetches
            val path = "Profile/" + email.replace(Regex("[^A-Za-z0-9]"), "_") +
                "_" + System.currentTimeMillis()
            FirebaseRepository.uploadImage(
                path, uri, context, maxDim = FirebaseRepository.AVATAR_MAX_DIM
            )
                .onSuccess {
                    appViewModel.saveAvatar(it)
                    FirebaseRepository.updateUserAvatar(email, it)   // persist to DB
                    FirebaseRepository.deleteImage(oldRef)           // drop the old blob
                }
            isAvatarUploading = false
        }
    }

    // Picked photo → circular crop UI first, then upload
    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) result.uriContent?.let(::uploadAvatar)
    }
    fun launchCrop(uri: Uri) {
        cropLauncher.launch(
            CropImageContractOptions(
                uri = uri,
                cropImageOptions = CropImageOptions(
                    cropShape = CropImageView.CropShape.OVAL,
                    fixAspectRatio = true,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    guidelines = CropImageView.Guidelines.ON
                )
            )
        )
    }
    val avatarPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(::launchCrop) }

    // Selfie captured in-app (front camera) → crop → upload
    LaunchedEffect(appViewModel.capturedPhotoUri) {
        appViewModel.capturedPhotoUri?.let {
            launchCrop(it)
            appViewModel.capturedPhotoUri = null
        }
    }
    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) navController.navigate(Screen.cameraRoute(true)) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    DisposableEffect(Unit) {
        mediaPlayer = try { MediaPlayer.create(context, R.raw.dogbarking) } catch (_: Exception) { null }
        onDispose { mediaPlayer?.release() }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Cream, titleContentColor = Ink
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header: leaves span the whole profile block ──────────────────
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Leaves cover the full header height, fading out at the bottom
                Image(
                    painter = painterResource(R.drawable.profile_leaves),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .align(Alignment.TopCenter),
                    contentScale = ContentScale.Crop,
                    alpha = 0.9f
                )
                // Soft cream fade at the bottom so it blends into the page
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Cream.copy(alpha = 0.4f),
                                    Cream
                                )
                            )
                        )
                )

                // Avatar + name + email + badge, all sitting on the leaves
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 40.dp, bottom = 20.dp)
                ) {
                    // Tap avatar → options sheet (gallery / selfie / remove)
                    Box(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showAvatarOptions = true }
                    ) {
                        if (avatar.isNotEmpty()) {
                            AniImage(
                                imageRef = avatar,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.avatar_default),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (isAvatarUploading) {
                            CircularProgressIndicator(
                                color = Forest,
                                modifier = Modifier.size(96.dp).padding(30.dp)
                            )
                        }
                        // Camera badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Forest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera, "Change photo",
                                tint = Color.White, modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        name.ifEmpty { email.substringBefore("@") },
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink
                    )
                    Text(email, fontSize = 13.sp, color = Sage)
                    Spacer(Modifier.height(8.dp))
                    // Animal Lover badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFD9EBE1))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.logo_paw),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Animal Lover", fontSize = 12.sp, color = Forest, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── City display card (read-only) ────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Forest, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Your City", fontSize = 12.sp, color = Sage)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        currentCity.ifEmpty { "Not set" },
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Menu items ───────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        title = "Edit Name",
                        subtitle = "Update the name shown on your profile",
                        expanded = showNameEditor,
                        onClick = {
                            showNameEditor = !showNameEditor
                            if (showNameEditor && newName.isEmpty()) newName = name
                        }
                    )

                    if (showNameEditor) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 14.dp)
                        ) {
                            OutlinedTextField(
                                value = newName,
                                onValueChange = { newName = it; message = "" },
                                placeholder = { Text("Your name", color = Sage, fontSize = 14.sp) },
                                trailingIcon = {
                                    if (newName.isNotEmpty()) {
                                        IconButton(onClick = { newName = ""; message = "" }) {
                                            Icon(Icons.Default.Close, "Clear", tint = Sage, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Cream,
                                    unfocusedContainerColor = Cream,
                                    focusedBorderColor = Forest,
                                    unfocusedBorderColor = Color(0xFFE3DDD0)
                                )
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (newName.isBlank()) { message = "Name can't be empty"; return@Button }
                                    val trimmed = newName.trim()
                                    appViewModel.saveName(trimmed)
                                    showNameEditor = false
                                    message = ""
                                    scope.launch {
                                        FirebaseRepository.updateUserName(email, trimmed)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save Name", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF0EBE0), thickness = 1.dp)
                    ProfileMenuItem(
                        icon = Icons.Default.EditLocation,
                        title = "Change City",
                        subtitle = "Update the city shown in your feed",
                        expanded = showCityEditor,
                        onClick = { showCityEditor = !showCityEditor }
                    )

                    // Inline editor expands directly below the "Change City" row
                    if (showCityEditor) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 14.dp)
                        ) {
                            OutlinedTextField(
                                value = newCity,
                                onValueChange = { newCity = it; message = "" },
                                placeholder = { Text("Enter new city", color = Sage, fontSize = 14.sp) },
                                trailingIcon = {
                                    if (newCity.isNotEmpty()) {
                                        IconButton(onClick = { newCity = ""; message = "" }) {
                                            Icon(Icons.Default.Close, "Clear", tint = Sage, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Cream,
                                    unfocusedContainerColor = Cream,
                                    focusedBorderColor = Forest,
                                    unfocusedBorderColor = Color(0xFFE3DDD0)
                                )
                            )
                            if (message.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                Text(message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val sanitized = FirebaseRepository.sanitizeCity(newCity)
                                    if (sanitized.isEmpty()) { message = "Enter a valid city name"; return@Button }
                                    scope.launch {
                                        appViewModel.saveCity(sanitized)
                                        showCityEditor = false
                                        newCity = ""
                                        navController.navigate(Screen.HOME) {
                                            popUpTo(Screen.HOME) { inclusive = true }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save & Search", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF0EBE0), thickness = 1.dp)
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Logout",
                        subtitle = "Sign out of AniAssist",
                        titleColor = Coral,
                        onClick = {
                            appViewModel.clearSession()
                            FirebaseRepository.signOut()
                            navController.navigate(Screen.LOGIN) {
                                popUpTo(Screen.HOME) { inclusive = true }
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Footer quote ─────────────────────────────────────────────────
            Text(
                "“The best way to find yourself\nis to help someone who can't ask.”",
                fontSize = 13.sp,
                color = Sage,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(8.dp))

            // ── Easter egg: a little dog strolling along the bottom ──────────
            var showWoof by remember { mutableStateOf(false) }
            LaunchedEffect(showWoof) {
                if (showWoof) { kotlinx.coroutines.delay(1200); showWoof = false }
            }

            val walkTransition = rememberInfiniteTransition(label = "dogWalk")
            val walkProgress by walkTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(9000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "walkProgress"
            )
            var lastProgress by remember { mutableStateOf(0f) }
            val movingRight = walkProgress >= lastProgress
            LaunchedEffect(walkProgress) { lastProgress = walkProgress }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(horizontal = 20.dp)
            ) {
                val dogComposition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("moody_dog.lottie")
                )
                val travelWidth = maxWidth - 72.dp
                Box(
                    modifier = Modifier
                        .offset(x = travelWidth * walkProgress)
                        .align(Alignment.BottomStart)
                        .width(72.dp)
                ) {
                    if (showWoof) {
                        Text(
                            "Woof!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Forest,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-14).dp)
                        )
                    }
                    LottieAnimation(
                        composition = dogComposition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .size(72.dp)
                            .graphicsLayer { scaleX = if (movingRight) 1f else -1f }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                mediaPlayer?.let { if (it.isPlaying) it.seekTo(0) else it.start() }
                                showWoof = true
                            }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    // Avatar options: selfie / gallery / remove → back to default
    if (showAvatarOptions) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarOptions = false },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Text(
                    "Profile photo", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    color = Ink, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                AvatarOptionRow(
                    icon = Icons.Default.PhotoCamera,
                    label = "Open camera"
                ) {
                    showAvatarOptions = false
                    cameraPermission.launch(android.Manifest.permission.CAMERA)
                }
                AvatarOptionRow(
                    icon = Icons.Default.AddPhotoAlternate,
                    label = "Choose from gallery"
                ) {
                    showAvatarOptions = false
                    avatarPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                if (avatar.isNotEmpty()) {
                    AvatarOptionRow(
                        icon = Icons.Default.Close,
                        label = "Remove photo",
                        tint = Coral
                    ) {
                        appViewModel.saveAvatar("")
                        showAvatarOptions = false
                        scope.launch { FirebaseRepository.updateUserAvatar(email, "") }
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarOptionRow(
    icon: ImageVector,
    label: String,
    tint: Color = Forest,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (tint == Coral) Coral else Ink)
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    titleColor: Color = Ink,
    expanded: Boolean? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Cream),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Forest, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
            Text(subtitle, fontSize = 12.sp, color = Sage)
        }
        Icon(
            when (expanded) {
                true  -> Icons.Default.KeyboardArrowUp
                false -> Icons.Default.KeyboardArrowDown
                null  -> Icons.Default.ChevronRight
            },
            null, tint = Sage, modifier = Modifier.size(18.dp)
        )
    }
}
