package abhishek.aniassist.ui.screens.trivia

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.imageLoader
import coil3.request.ImageRequest
import abhishek.aniassist.R
import abhishek.aniassist.data.trivia.AnimalKingdomData
import abhishek.aniassist.data.trivia.Habitat
import abhishek.aniassist.data.trivia.TriviaAnimal
import abhishek.aniassist.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriviaScreen(navController: NavHostController) {
    var selectedHabitat by remember { mutableStateOf(Habitat.AIR) }
    var expandedAnimal by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }

    // Prefetch every image in the selected category so scrolling feels instant
    val context = LocalContext.current
    LaunchedEffect(selectedHabitat) {
        AnimalKingdomData.forHabitat(selectedHabitat).forEach { animal ->
            context.imageLoader.enqueue(
                ImageRequest.Builder(context).data(animal.imageUrl).build()
            )
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Animal Kingdom", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Learn about the animals around us", fontSize = 12.sp, color = Sage)
                    }
                },
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
                .padding(horizontal = 20.dp)
        ) {
            // ── Habitat cards: AIR / LAND / WATER ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HabitatCard(
                    modifier = Modifier.weight(1f),
                    label = "AIR",
                    icon = Icons.Default.Air,
                    selected = selectedHabitat == Habitat.AIR,
                    accent = Sky
                ) { selectedHabitat = Habitat.AIR; expandedAnimal = null }

                HabitatCard(
                    modifier = Modifier.weight(1f),
                    label = "LAND",
                    icon = Icons.Default.Terrain,
                    selected = selectedHabitat == Habitat.LAND,
                    accent = Amber
                ) { selectedHabitat = Habitat.LAND; expandedAnimal = null }

                HabitatCard(
                    modifier = Modifier.weight(1f),
                    label = "WATER",
                    icon = Icons.Default.Water,
                    selected = selectedHabitat == Habitat.WATER,
                    accent = Color(0xFF5B9BD5)
                ) { selectedHabitat = Habitat.WATER; expandedAnimal = null }
            }

            Spacer(Modifier.height(16.dp))

            // ── Search — searches across all habitats ────────────────────────
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search animals…", color = Sage, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Sage) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, "Clear", tint = Sage, modifier = Modifier.size(18.dp))
                        }
                    }
                },
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

            Spacer(Modifier.height(20.dp))

            // ── Animal cards ─────────────────────────────────────────────────
            val animals = remember(selectedHabitat, query) {
                AnimalKingdomData.forHabitat(selectedHabitat).filter {
                    query.isBlank() ||
                        it.name.contains(query, true) ||
                        it.scientificName.contains(query, true)
                }
            }
            animals.forEach { animal ->
                AnimalCard(
                    animal = animal,
                    expanded = expandedAnimal == animal.name,
                    onToggle = {
                        expandedAnimal =
                            if (expandedAnimal == animal.name) null else animal.name
                    }
                )
                Spacer(Modifier.height(14.dp))
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Components ──────────────────────────────────────────────────────────────

@Composable
private fun HabitatCard(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Forest else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (selected) Color.White.copy(alpha = 0.2f) else Cream),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon, null,
                tint = if (selected) Color.White else accent,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            label,
            fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else Ink
        )
    }
}

@Composable
private fun AnimalCard(
    animal: TriviaAnimal,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            // Photo — fades in over the placeholder instead of popping
            val painter = rememberAsyncImagePainter(model = animal.imageUrl)
            val painterState by painter.state.collectAsState()
            val imageAlpha by animateFloatAsState(
                targetValue = if (painterState is AsyncImagePainter.State.Success) 1f else 0f,
                animationSpec = tween(400),
                label = "imageFade"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.placeholder_animal),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = painter,
                    contentDescription = animal.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = imageAlpha
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Name + scientific name + expand arrow
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(animal.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text(
                            animal.scientificName,
                            fontSize = 12.sp, color = Sage, fontStyle = FontStyle.Italic
                        )
                    }
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        null, tint = Sage
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Info chips: habitat / diet / lifespan
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip("⏳ ${animal.lifespan}", Forest)
                }
                Spacer(Modifier.height(8.dp))
                InfoRow("Habitat", animal.habitat)
                InfoRow("Diet", animal.diet)

                // Expanded: description + varieties
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF0EBE0))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            animal.description,
                            fontSize = 13.sp, color = Ink, lineHeight = 19.sp
                        )

                        if (animal.varieties.isNotEmpty()) {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                "Breeds & Varieties",
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Forest
                            )
                            Spacer(Modifier.height(8.dp))
                            animal.varieties.forEach { v ->
                                VarietyRow(v.name, v.info, v.temperament, v.description)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            "$label: ",
            fontSize = 12.sp, color = Sage, fontWeight = FontWeight.SemiBold
        )
        Text(value, fontSize = 12.sp, color = Ink)
    }
}

@Composable
private fun VarietyRow(name: String, info: String, temperament: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Cream)
            .padding(12.dp)
    ) {
        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(info, fontSize = 11.sp, color = Sage)
        if (temperament.isNotEmpty()) {
            Spacer(Modifier.height(2.dp))
            Text(temperament, fontSize = 11.sp, color = Forest, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(4.dp))
        Text(description, fontSize = 12.sp, color = Ink, lineHeight = 17.sp)
    }
}
