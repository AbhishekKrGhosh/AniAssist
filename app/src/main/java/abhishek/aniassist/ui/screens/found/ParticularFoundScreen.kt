package abhishek.aniassist.ui.screens.found

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.components.DetailScaffold
import abhishek.aniassist.ui.theme.Sky
import abhishek.aniassist.ui.theme.SkySoft
import abhishek.aniassist.viewmodel.AppViewModel

@Composable
fun ParticularFoundScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val animal = appViewModel.selectedFoundAnimal ?: run {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }
    val city by appViewModel.city.collectAsStateWithLifecycle()

    DetailScaffold(
        imageUri = animal.uri ?: "",
        title = "Found ${animal.category?.ifEmpty { "animal" } ?: "animal"}",
        badge = "Found",
        badgeColor = Sky,
        badgeBg = SkySoft,
        onBack = { navController.popBackStack() },
        onImageClick = {
            appViewModel.selectedImageUri = animal.uri ?: ""
            navController.navigate(Screen.FULL_SCREEN)
        },
        actionLabel = "This Is My Pet!",
        onAction = {
            navController.navigate(Screen.verifyRoute(animal.animalId ?: "", city, "Found"))
        },
        infoRows = listOf(
            "Description"  to (animal.description ?: "No description"),
            "Contact info" to (animal.contact ?: "Not provided"),
            "Reported on"  to (animal.dateTime ?: "")
        ),
        latitude = animal.latitude,
        longitude = animal.longitude
    )
}
