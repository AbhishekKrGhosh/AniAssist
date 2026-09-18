package abhishek.aniassist.ui.screens.found

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import abhishek.aniassist.R
import abhishek.aniassist.data.model.AnimalFoundInfo
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

private enum class FoundTab { LIST, REPORT }
private val ANIMAL_TYPES = listOf(
    "Dog" to abhishek.aniassist.R.drawable.ic_dog,
    "Cat" to abhishek.aniassist.R.drawable.ic_cat,
    "Bird" to abhishek.aniassist.R.drawable.ic_bird,
    "Cow" to abhishek.aniassist.R.drawable.ic_cow,
    "Other" to abhishek.aniassist.R.drawable.ic_paw
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val city by appViewModel.city.collectAsStateWithLifecycle()

    var activeTab by rememberSaveable { mutableStateOf(FoundTab.LIST) }
    var foundList by remember { mutableStateOf<List<AnimalFoundInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var noData by remember { mutableStateOf(false) }

    var category by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var contact by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var isUploading by rememberSaveable { mutableStateOf(false) }
    var formMessage by rememberSaveable { mutableStateOf("") }
    var showPhotoSheet by remember { mutableStateOf(false) }

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

    LaunchedEffect(city) {
        if (city.isEmpty()) return@LaunchedEffect
        isLoading = true
        FirebaseRepository.getFoundAnimals(city).collect { data ->
            isLoading = false
            foundList = data
            noData = data.isEmpty()
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Found Animals", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
        ) {
            // Segmented tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(4.dp)
            ) {
                FoundTab.entries.forEach { tab ->
                    val sel = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) Forest else Color.Transparent)
                            .clickable { activeTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (tab == FoundTab.LIST) "Browse Found" else "Report Found",
                            fontSize = 14.sp,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                            color = if (sel) Color.White else Sage
                        )
                    }
                }
            }

            when (activeTab) {
                FoundTab.LIST -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            isLoading -> CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center), color = Forest
                            )
                            noData -> Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.empty_feed),
                                    contentDescription = null,
                                    modifier = Modifier.size(140.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("No found animals in $city", color = Sage, fontSize = 15.sp)
                                Text("Shake phone to change city", color = Forest, fontSize = 13.sp)
                            }
                            else -> Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 20.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                foundList.forEach { animal ->
                                    FoundCard(animal) {
                                        appViewModel.selectedFoundAnimal = animal
                                        navController.navigate(Screen.PARTICULAR_FOUND)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }

                FoundTab.REPORT -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Encouragement banner — sky (matches the Found home card)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SkySoft)
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
                                Text("You may be reuniting a family.",
                                     fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Sky)
                                Text("Please share as much accurate information as possible.",
                                     fontSize = 10.sp, color = Sage)
                            }
                            Image(
                                painter = painterResource(abhishek.aniassist.R.drawable.ic_leaf),
                                contentDescription = null,
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        // Photo picker — illustrated drop zone
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
                                            .background(SkySoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.PhotoCamera, null,
                                            tint = Sky, modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text("Add photos", fontWeight = FontWeight.Bold, color = Ink, fontSize = 15.sp)
                                    Spacer(Modifier.height(2.dp))
                                    Text("Helps the owner identify their animal.",
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

                        FormSection(
                            iconRes = abhishek.aniassist.R.drawable.ic_paw,
                            iconBg = SkySoft,
                            title = "Animal type",
                            subtitle = "Select the animal"
                        ) {
                            IconChipRow(ANIMAL_TYPES, category) { category = it }
                        }

                        FormSection(
                            iconRes = abhishek.aniassist.R.drawable.ic_description,
                            iconBg = SkySoft,
                            title = "Description / where found",
                            subtitle = "Any collar, marks, or behaviour?"
                        ) {
                            ModernField(
                                value = description,
                                placeholder = "Where did you find it? Any collar or marks?",
                                multiline = true,
                                onValueChange = { description = it }
                            )
                        }

                        FormSection(
                            iconRes = abhishek.aniassist.R.drawable.ic_pin_location,
                            iconBg = SkySoft,
                            title = "Where found",
                            subtitle = "Pin the exact spot on the map"
                        ) {
                            MapPickRow(
                                pickedAddress = appViewModel.pickedAddress.takeIf { it.isNotEmpty() },
                                onPick = { navController.navigate(Screen.MAP_PICKER) },
                                onClear = {
                                    appViewModel.pickedLat = null
                                    appViewModel.pickedLng = null
                                    appViewModel.pickedAddress = ""
                                }
                            )
                        }

                        FormSection(
                            iconRes = abhishek.aniassist.R.drawable.ic_message,
                            iconBg = SkySoft,
                            title = "Your contact info",
                            subtitle = "So the owner can reach you"
                        ) {
                            ModernField(
                                value = contact,
                                placeholder = "Phone or email so the owner can reach you",
                                onValueChange = { contact = it }
                            )
                        }

                        if (formMessage.isNotEmpty()) {
                            Text(formMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                if (category.isBlank() || contact.isBlank()) {
                                    formMessage = "Please select animal type and enter your contact info"
                                    return@Button
                                }
                                isUploading = true; formMessage = ""
                                val aniId = FirebaseRepository.generateId()
                                scope.launch {
                                    var picUri = ""
                                    if (selectedImageUri != null) {
                                        FirebaseRepository.uploadImage("Location/$city/Found/$aniId", selectedImageUri!!, context)
                                            .onSuccess { picUri = it }
                                            .onFailure { isUploading = false; formMessage = "Image upload failed"; return@launch }
                                    }
                                    val info = AnimalFoundInfo(
                                        category = category, uri = picUri, description = description,
                                        contact = contact, animalId = aniId, dateTime = FirebaseRepository.currentDateTime(),
                                        latitude = appViewModel.pickedLat, longitude = appViewModel.pickedLng
                                    )
                                    FirebaseRepository.uploadFoundAnimal(city, info)
                                        .onSuccess {
                                            isUploading = false
                                            appViewModel.pickedLat = null
                                            appViewModel.pickedLng = null
                                            appViewModel.pickedAddress = ""
                                            navController.navigate(Screen.successRoute("found")) {
                                                popUpTo(Screen.HOME)
                                            }
                                        }
                                        .onFailure { isUploading = false; formMessage = "Upload failed: ${it.message}" }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Forest),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isUploading
                        ) {
                            if (isUploading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            else Text("Report Found Animal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FoundCard(animal: AnimalFoundInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = animal.uri?.takeIf { it.isNotEmpty() },
                contentDescription = null,
                placeholder = painterResource(R.drawable.placeholder_animal),
                error = painterResource(R.drawable.placeholder_animal),
                fallback = painterResource(R.drawable.placeholder_animal),
                modifier = Modifier.size(84.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Found ${animal.category?.ifEmpty { "animal" } ?: "animal"}",
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Ink,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SkySoft)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Found", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Sky)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    animal.description ?: "", fontSize = 12.sp, color = Sage,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(animal.dateTime ?: "", fontSize = 11.sp, color = Sage)
            }
        }
    }
}
