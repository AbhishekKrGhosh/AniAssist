package abhishek.aniassist.ui.screens.post

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import abhishek.aniassist.data.model.AnimalPostInfo
import abhishek.aniassist.data.repository.FirebaseRepository
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.components.FormSection
import abhishek.aniassist.ui.components.IconChipRow
import abhishek.aniassist.ui.components.MapPickRow
import abhishek.aniassist.ui.components.ModernField
import abhishek.aniassist.ui.components.PhotoSourceSheet
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel
import kotlinx.coroutines.launch

private val ANIMAL_TYPES = listOf(
    "Dog" to abhishek.aniassist.R.drawable.ic_dog,
    "Cat" to abhishek.aniassist.R.drawable.ic_cat,
    "Bird" to abhishek.aniassist.R.drawable.ic_bird,
    "Cow" to abhishek.aniassist.R.drawable.ic_cow,
    "Other" to abhishek.aniassist.R.drawable.ic_paw
)
private val CONDITIONS = listOf(
    "Critical" to abhishek.aniassist.R.drawable.ic_critical,
    "Serious" to abhishek.aniassist.R.drawable.ic_serious,
    "Needs Help" to abhishek.aniassist.R.drawable.ic_needs_help,
    "Unknown" to abhishek.aniassist.R.drawable.ic_unknown
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val city by appViewModel.city.collectAsStateWithLifecycle()
    val address by appViewModel.address.collectAsStateWithLifecycle()

    var problem by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var condition by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var locationText by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var isUploading by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("") }
    var showPhotoSheet by remember { mutableStateOf(false) }

    LaunchedEffect(address) { if (locationText.isEmpty()) locationText = address }
    // Pinning on the map fills the location field — one place to see/edit it
    LaunchedEffect(appViewModel.pickedAddress) {
        if (appViewModel.pickedAddress.isNotEmpty()) locationText = appViewModel.pickedAddress
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        selectedImageUri = uri
    }

    // Camera capture returns through the viewModel
    LaunchedEffect(appViewModel.capturedPhotoUri) {
        appViewModel.capturedPhotoUri?.let {
            selectedImageUri = it
            appViewModel.capturedPhotoUri = null
        }
    }
    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) navController.navigate(Screen.cameraRoute(false)) }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Report Injured Animal", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Encouragement banner ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CoralSoft)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDF0E4)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(abhishek.aniassist.R.drawable.ic_heart),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Your report can help save a life.",
                         fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Forest)
                    Text("Please share as much accurate information as possible.",
                         fontSize = 10.sp, color = Sage)
                }
                Image(
                    painter = painterResource(abhishek.aniassist.R.drawable.ic_leaf),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            }

            // ── Photo picker — illustrated drop zone ─────────────────────────
            // ic_upload_photo_bg already contains the dashed border + leaf/paw
            // decorations; we overlay the camera icon, text and gallery pill
            // centered in its empty middle band.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { showPhotoSheet = true },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri, contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(abhishek.aniassist.R.drawable.ic_upload_photo_bg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(CoralSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera, null,
                                tint = Coral, modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Add photos", fontWeight = FontWeight.Bold, color = Ink, fontSize = 15.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("A clear photo helps responders act faster.",
                             color = Sage, fontSize = 11.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFDFF0E4))
                                .clickable {
                                    showPhotoSheet = false
                                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(abhishek.aniassist.R.drawable.ic_photos),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(7.dp))
                            Text("Choose from gallery", fontSize = 12.sp,
                                 fontWeight = FontWeight.SemiBold, color = Forest)
                        }
                    }
                }
            }
            if (showPhotoSheet) {
                PhotoSourceSheet(
                    onDismiss = { showPhotoSheet = false },
                    onCamera = {
                        showPhotoSheet = false
                        cameraPermission.launch(android.Manifest.permission.CAMERA)
                    },
                    onGallery = {
                        showPhotoSheet = false
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )
            }

            // ── Problem ──────────────────────────────────────────────────────
            FormSection(
                iconRes = abhishek.aniassist.R.drawable.ic_message,
                iconBg = Color(0xFFDFF0E4),
                title = "What happened?",
                subtitle = "Briefly describe what you noticed"
            ) {
                ModernField(
                    value = problem,
                    placeholder = "e.g. Hit by vehicle, bleeding leg, unable to move…",
                    onValueChange = { problem = it }
                )
            }

            // ── Animal type chips ────────────────────────────────────────────
            FormSection(
                iconRes = abhishek.aniassist.R.drawable.ic_paw,
                iconBg = Color(0xFFDFF0E4),
                title = "Animal type",
                subtitle = "Select the animal"
            ) {
                IconChipRow(ANIMAL_TYPES, category) { category = it }
            }

            // ── Condition chips ──────────────────────────────────────────────
            FormSection(
                iconRes = abhishek.aniassist.R.drawable.ic_critical,
                iconBg = Color(0xFFF9DEDC),
                title = "Condition",
                subtitle = "How urgent is it?"
            ) {
                IconChipRow(CONDITIONS, condition) { condition = it }
            }

            // ── Location — text field OR pinned card, never both ─────────────
            FormSection(
                iconRes = abhishek.aniassist.R.drawable.ic_pin_location,
                iconBg = Color(0xFFDFF0E4),
                title = "Location",
                subtitle = "Where is the animal?"
            ) {
                if (appViewModel.pickedAddress.isEmpty()) {
                    ModernField(
                        value = locationText,
                        placeholder = "Where is the animal?",
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Sage, modifier = Modifier.size(18.dp)) },
                        onValueChange = { locationText = it }
                    )
                    Spacer(Modifier.height(10.dp))
                }
                MapPickRow(
                    pickedAddress = appViewModel.pickedAddress.takeIf { it.isNotEmpty() },
                    onPick = { navController.navigate(Screen.MAP_PICKER) },
                    onClear = {
                        if (locationText == appViewModel.pickedAddress) locationText = ""
                        appViewModel.pickedLat = null
                        appViewModel.pickedLng = null
                        appViewModel.pickedAddress = ""
                    }
                )
            }

            // ── Description ──────────────────────────────────────────────────
            FormSection(
                iconRes = abhishek.aniassist.R.drawable.ic_description,
                iconBg = Color(0xFFDFF0E4),
                title = "Description",
                subtitle = "Anything else responders should know"
            ) {
                ModernField(
                    value = description,
                    placeholder = "Describe the animal, injuries, behaviour…",
                    multiline = true,
                    onValueChange = { description = it }
                )
            }

            if (message.isNotEmpty()) {
                Text(message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    if (problem.isBlank() || category.isBlank()) {
                        message = "Please fill in what happened and select the animal type"
                        return@Button
                    }
                    isUploading = true; message = ""
                    val aniId = FirebaseRepository.generateId()
                    scope.launch {
                        var picUri = ""
                        if (selectedImageUri != null) {
                            FirebaseRepository.uploadImage("Location/$city/Post/$aniId", selectedImageUri!!, context)
                                .onSuccess { picUri = it }
                                .onFailure { isUploading = false; message = "Image upload failed: ${it.message}"; return@launch }
                        }
                        val info = AnimalPostInfo(
                            problem = problem, category = category, condition = condition,
                            decription = description,
                            address = locationText.ifEmpty { appViewModel.pickedAddress },
                            animalId = aniId, dateTime = FirebaseRepository.currentDateTime(), uri = picUri,
                            latitude = appViewModel.pickedLat, longitude = appViewModel.pickedLng
                        )
                        FirebaseRepository.uploadPostAnimal(city, info)
                            .onSuccess {
                                isUploading = false
                                appViewModel.pickedLat = null
                                appViewModel.pickedLng = null
                                appViewModel.pickedAddress = ""
                                navController.navigate(Screen.successRoute("post")) {
                                    popUpTo(Screen.HOME)
                                }
                            }
                            .onFailure { isUploading = false; message = "Upload failed: ${it.message}" }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                shape = RoundedCornerShape(16.dp),
                enabled = !isUploading
            ) {
                if (isUploading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                else Text("Submit Request", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
