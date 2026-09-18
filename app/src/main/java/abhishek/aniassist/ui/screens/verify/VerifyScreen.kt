package abhishek.aniassist.ui.screens.verify

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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import abhishek.aniassist.data.model.VerifyInfo
import abhishek.aniassist.data.repository.FirebaseRepository
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.components.FormSection
import abhishek.aniassist.ui.components.ModernField
import abhishek.aniassist.ui.components.PhotoSourceSheet
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    navController: NavHostController,
    appViewModel: AppViewModel,
    aniId: String,
    city: String,
    type: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val email by appViewModel.email.collectAsStateWithLifecycle()
    val sanitizedEmail = remember(email) { FirebaseRepository.sanitizeEmail(email) }

    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
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

    val title = when (type) {
        "Lost"  -> "Confirm Found"
        "Found" -> "Confirm Reunion"
        else    -> "Confirm Resolved"
    }
    val buttonLabel = when (type) {
        "Lost"  -> "Submit Proof & Mark Found"
        "Found" -> "Submit Proof & Mark Reunited"
        else    -> "Submit Proof & Mark Resolved"
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
            // Encouragement banner — forest green
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFDFF0E4))
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
                    Text("Almost done — one last step.",
                         fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Forest)
                    Text("Upload a proof photo and a brief note to close this case.",
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
                                .background(Color(0xFFDFF0E4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera, null,
                                tint = Forest, modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Upload proof photo", fontWeight = FontWeight.Bold, color = Ink, fontSize = 15.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("Required to close this case.", color = Sage, fontSize = 11.sp)
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

            // Description
            FormSection(
                iconRes = abhishek.aniassist.R.drawable.ic_description,
                iconBg = Color(0xFFDFF0E4),
                title = "Description",
                subtitle = "How was it resolved? Where is the animal now?"
            ) {
                ModernField(
                    value = description,
                    placeholder = "How was it resolved? Where is the animal now?",
                    multiline = true,
                    onValueChange = { description = it }
                )
            }

            if (message.isNotEmpty()) {
                Text(message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    if (selectedImageUri == null) {
                        message = "Please upload a proof photo first"
                        return@Button
                    }
                    isUploading = true; message = ""
                    val proofId = FirebaseRepository.generateId()
                    scope.launch {
                        val uploadResult = FirebaseRepository.uploadImage(
                            "Users/$sanitizedEmail/Proof/$type/$proofId", selectedImageUri!!, context
                        )
                        uploadResult.onFailure {
                            isUploading = false
                            message = "Image upload failed: ${it.message}"
                            return@launch
                        }
                        val picUri = uploadResult.getOrDefault("")

                        val verifyInfo = VerifyInfo(
                            dataId = proofId, description = description,
                            uri = picUri, dateTime = FirebaseRepository.currentDateTime()
                        )
                        FirebaseRepository.uploadVerifyInfo(sanitizedEmail, type, verifyInfo)
                        FirebaseRepository.deleteRecord(city, type, aniId)
                        isUploading = false
                        navController.navigate(Screen.HOME) {
                            popUpTo(Screen.HOME) { inclusive = false }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                shape = RoundedCornerShape(16.dp),
                enabled = !isUploading
            ) {
                if (isUploading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                else Text(buttonLabel, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
