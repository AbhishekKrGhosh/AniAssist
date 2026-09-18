package abhishek.aniassist.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.ui.components.AniImage
import abhishek.aniassist.data.model.AnimalFoundInfo
import abhishek.aniassist.data.model.AnimalLostInfo
import abhishek.aniassist.data.model.AnimalPostInfo
import abhishek.aniassist.data.repository.FirebaseRepository
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

internal enum class FeedFilter(val label: String) {
    ALL("All"), INJURED("Injured"), LOST("Lost"), FOUND("Found")
}

/** Unified feed item so Post + Lost + Found can share one list. */
internal sealed class FeedItem {
    abstract val title: String
    abstract val subtitle: String
    abstract val badge: String
    abstract val badgeColor: Color
    abstract val badgeBg: Color
    abstract val imageUri: String
    abstract val dateTime: String
    abstract val filter: FeedFilter
    abstract fun onOpen(appViewModel: AppViewModel, nav: NavHostController)

    class Post(private val a: AnimalPostInfo) : FeedItem() {
        override val title    = "Injured ${a.category ?: "animal"} — ${a.problem ?: "needs help"}"
        override val subtitle = a.address ?: a.decription ?: ""
        override val badge    = a.condition?.takeIf { it.isNotEmpty() } ?: "Needs Help"
        override val badgeColor = Coral
        override val badgeBg    = CoralSoft
        override val imageUri = a.uri ?: ""
        override val dateTime = a.dateTime ?: ""
        override val filter   = FeedFilter.INJURED
        override fun onOpen(vm: AppViewModel, nav: NavHostController) {
            vm.selectedPostAnimal = a; nav.navigate(Screen.PARTICULAR_POST)
        }
    }

    class Lost(private val a: AnimalLostInfo) : FeedItem() {
        override val title    = "${a.animName?.takeIf { it.isNotEmpty() } ?: "Pet"} missing — ${a.category ?: "animal"}"
        override val subtitle = "Last seen: ${a.lastSighted ?: "unknown"}"
        override val badge    = "Missing"
        override val badgeColor = Amber
        override val badgeBg    = AmberSoft
        override val imageUri = a.uri ?: ""
        override val dateTime = a.dateTime ?: ""
        override val filter   = FeedFilter.LOST
        override fun onOpen(vm: AppViewModel, nav: NavHostController) {
            vm.selectedLostAnimal = a; nav.navigate(Screen.PARTICULAR_LOST)
        }
    }

    class Found(private val a: AnimalFoundInfo) : FeedItem() {
        override val title    = "Found ${a.category ?: "animal"}"
        override val subtitle = a.description ?: ""
        override val badge    = "Found"
        override val badgeColor = Sky
        override val badgeBg    = SkySoft
        override val imageUri = a.uri ?: ""
        override val dateTime = a.dateTime ?: ""
        override val filter   = FeedFilter.FOUND
        override fun onOpen(vm: AppViewModel, nav: NavHostController) {
            vm.selectedFoundAnimal = a; nav.navigate(Screen.PARTICULAR_FOUND)
        }
    }

    companion object {
        private val fmt = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        fun sortKey(dateTime: String): Long =
            runCatching { fmt.parse(dateTime)?.time ?: 0L }.getOrDefault(0L)
    }
}

/** Merged Post + Lost + Found feed for a city, newest first. */
internal class CaseFeedState(
    val items: List<FeedItem>,
    val isLoading: Boolean
)

@Composable
internal fun rememberCaseFeed(city: String): CaseFeedState {
    var posts     by remember { mutableStateOf<List<AnimalPostInfo>>(emptyList()) }
    var lostList  by remember { mutableStateOf<List<AnimalLostInfo>>(emptyList()) }
    var foundList by remember { mutableStateOf<List<AnimalFoundInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(city) {
        if (city.isEmpty()) return@LaunchedEffect
        launch { FirebaseRepository.getPostAnimals(city).collect { posts = it; isLoading = false } }
        launch { FirebaseRepository.getLostAnimals(city).collect { lostList = it } }
        launch { FirebaseRepository.getFoundAnimals(city).collect { foundList = it } }
    }

    val items = remember(posts, lostList, foundList) {
        (posts.map { FeedItem.Post(it) } +
         lostList.map { FeedItem.Lost(it) } +
         foundList.map { FeedItem.Found(it) })
            .sortedByDescending { FeedItem.sortKey(it.dateTime) }
    }
    return CaseFeedState(items, isLoading)
}

/** Equal-width filter chip row — All / Injured / Lost / Found. */
@Composable
internal fun FeedFilterRow(
    selected: FeedFilter,
    onSelect: (FeedFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FeedFilter.entries.forEach { f ->
            val sel = selected == f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (sel) Forest else Color.White)
                    .clickable { onSelect(f) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    f.label, fontSize = 13.sp,
                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (sel) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
internal fun CaseCard(item: FeedItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AniImage(
                imageRef = item.imageUri,
                modifier = Modifier.size(84.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.title,
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Ink,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(item.badgeBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(item.badge, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                             color = item.badgeColor)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    item.subtitle,
                    fontSize = 12.sp, color = Sage,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Sage,
                         modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(
                        item.dateTime.substringBefore(" "),
                        fontSize = 11.sp, color = Sage
                    )
                }
            }
        }
    }
}
