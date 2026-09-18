package abhishek.aniassist.ui.screens.cases

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.ui.screens.home.CaseCard
import abhishek.aniassist.ui.screens.home.FeedFilter
import abhishek.aniassist.ui.screens.home.FeedFilterRow
import abhishek.aniassist.ui.screens.home.rememberCaseFeed
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyCasesScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val city by appViewModel.city.collectAsStateWithLifecycle()

    var filter by remember { mutableStateOf(FeedFilter.ALL) }
    var query  by remember { mutableStateOf("") }

    val feed = rememberCaseFeed(city)
    val visibleItems = feed.items
        .filter { filter == FeedFilter.ALL || it.filter == filter }
        .filter {
            query.isBlank() ||
            it.title.contains(query, true) || it.subtitle.contains(query, true)
        }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nearby Cases", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Forest,
                                 modifier = Modifier.size(13.dp))
                            Text(city, fontSize = 12.sp, color = Forest)
                        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            // Search
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search cases, animals…", color = Sage, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Sage) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Forest,
                    unfocusedBorderColor = Color(0xFFE3DDD0),
                    cursorColor = Forest
                )
            )

            Spacer(Modifier.height(12.dp))

            // Filter chips — equal width
            Box(Modifier.padding(horizontal = 20.dp)) {
                FeedFilterRow(selected = filter) { filter = it }
            }

            Spacer(Modifier.height(8.dp))

            // Feed
            when {
                feed.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Forest) }

                visibleItems.isEmpty() -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.empty_feed),
                        contentDescription = null,
                        modifier = Modifier.size(140.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (query.isNotBlank()) "No matches for \"$query\""
                        else "No cases in ${city.ifEmpty { "your city" }} yet",
                        color = Sage, fontSize = 14.sp
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibleItems) { item ->
                        CaseCard(item) { item.onOpen(appViewModel, navController) }
                    }
                }
            }
        }
    }
}
