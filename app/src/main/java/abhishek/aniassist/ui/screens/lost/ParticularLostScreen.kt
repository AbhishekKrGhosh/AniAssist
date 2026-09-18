package abhishek.aniassist.ui.screens.lost

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.components.DetailScaffold
import abhishek.aniassist.ui.theme.Amber
import abhishek.aniassist.ui.theme.AmberSoft
import abhishek.aniassist.viewmodel.AppViewModel

@Composable
fun ParticularLostScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val animal = appViewModel.selectedLostAnimal ?: run {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }
    val city by appViewModel.city.collectAsStateWithLifecycle()

    DetailScaffold(
        imageUri = animal.uri ?: "",
        title = "${animal.animName?.ifEmpty { "Pet" } ?: "Pet"} is missing",
        badge = "Missing",
        badgeColor = Amber,
        badgeBg = AmberSoft,
        onBack = { navController.popBackStack() },
        onImageClick = {
            appViewModel.selectedImageUri = animal.uri ?: ""
            navController.navigate(Screen.FULL_SCREEN)
        },
        actionLabel = "I Found This Animal!",
        onAction = {
            navController.navigate(Screen.verifyRoute(animal.animalId ?: "", city, "Lost"))
        },
        infoRows = listOf(
            "Animal type"   to (animal.category ?: "Unknown"),
            "Last seen at"  to (animal.lastSighted ?: "Unknown"),
            "Owner"         to (animal.ownerName ?: "Unknown"),
            "Owner contact" to (animal.ownerNumber ?: "Unknown"),
            "Description"   to (animal.description ?: "No description"),
            "Posted on"     to (animal.dateTime ?: "")
        ),
        latitude = animal.latitude,
        longitude = animal.longitude
    )
}
