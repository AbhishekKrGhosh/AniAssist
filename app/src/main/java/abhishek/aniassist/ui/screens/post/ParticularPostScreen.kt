package abhishek.aniassist.ui.screens.post

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.components.DetailScaffold
import abhishek.aniassist.ui.theme.Coral
import abhishek.aniassist.ui.theme.CoralSoft
import abhishek.aniassist.viewmodel.AppViewModel

@Composable
fun ParticularPostScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val animal = appViewModel.selectedPostAnimal ?: run {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }
    val city by appViewModel.city.collectAsStateWithLifecycle()

    DetailScaffold(
        imageUri = animal.uri ?: "",
        title = "Injured ${animal.category?.ifEmpty { "animal" } ?: "animal"}",
        badge = animal.condition?.ifEmpty { "Needs Help" } ?: "Needs Help",
        badgeColor = Coral,
        badgeBg = CoralSoft,
        onBack = { navController.popBackStack() },
        onImageClick = {
            appViewModel.selectedImageUri = animal.uri ?: ""
            navController.navigate(Screen.FULL_SCREEN)
        },
        actionLabel = "I Can Help — Mark as Resolved",
        onAction = {
            navController.navigate(Screen.verifyRoute(animal.animalId ?: "", city, "Post"))
        },
        infoRows = listOf(
            "Problem"    to (animal.problem ?: "Not specified"),
            "Location"   to (animal.address ?: "Not specified"),
            "Description" to (animal.decription ?: "No description"),
            "Reported on" to (animal.dateTime ?: "")
        ),
        latitude = animal.latitude,
        longitude = animal.longitude
    )
}
