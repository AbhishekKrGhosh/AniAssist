package abhishek.aniassist.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.data.repository.FirebaseRepository
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.viewmodel.AppViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, appViewModel: AppViewModel) {

    LaunchedEffect(Unit) {
        delay(2_000)
        if (!FirebaseRepository.isLoggedIn()) {
            // First install → onboarding, otherwise straight to login
            val dest = if (appViewModel.onboardingDone.value) Screen.LOGIN else Screen.ONBOARDING
            navController.navigate(dest) {
                popUpTo(Screen.SPLASH) { inclusive = true }
            }
            return@LaunchedEffect
        }
        // Refresh the cached name in the background (existing sessions won't have it)
        val email = appViewModel.email.value
        if (appViewModel.name.value.isEmpty() && email.isNotEmpty()) {
            appViewModel.saveName(FirebaseRepository.getUserName(email))
        }
        val city = appViewModel.city.value
        navController.navigate(if (city.isNotEmpty()) Screen.HOME else Screen.GET_LOCATION) {
            popUpTo(Screen.SPLASH) { inclusive = true }
        }
    }

    // Full-bleed splash artwork — the image already contains the logo & tagline
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.splash_full),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
