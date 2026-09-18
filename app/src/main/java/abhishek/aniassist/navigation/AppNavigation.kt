package abhishek.aniassist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import abhishek.aniassist.ui.screens.SplashScreen
import abhishek.aniassist.ui.screens.auth.ForgotPasswordScreen
import abhishek.aniassist.ui.screens.auth.LoginScreen
import abhishek.aniassist.ui.screens.auth.SignUpScreen
import abhishek.aniassist.ui.screens.camera.CameraCaptureScreen
import abhishek.aniassist.ui.screens.cases.NearbyCasesScreen
import abhishek.aniassist.ui.screens.found.FoundScreen
import abhishek.aniassist.ui.screens.found.ParticularFoundScreen
import abhishek.aniassist.ui.screens.home.HomeScreen
import abhishek.aniassist.ui.screens.image.FullScreenImageScreen
import abhishek.aniassist.ui.screens.location.GetLocationScreen
import abhishek.aniassist.ui.screens.map.MapPickerScreen
import abhishek.aniassist.ui.screens.onboarding.OnboardingScreen
import abhishek.aniassist.ui.screens.lost.LostScreen
import abhishek.aniassist.ui.screens.lost.ParticularLostScreen
import abhishek.aniassist.ui.screens.post.ParticularPostScreen
import abhishek.aniassist.ui.screens.post.PostScreen
import abhishek.aniassist.ui.screens.profile.ProfileScreen
import abhishek.aniassist.ui.screens.report.ReportChooserScreen
import abhishek.aniassist.ui.screens.report.SuccessScreen
import abhishek.aniassist.ui.screens.trivia.TriviaScreen
import abhishek.aniassist.ui.screens.verify.VerifyScreen
import abhishek.aniassist.viewmodel.AppViewModel

@Composable
fun AppNavigation(navController: NavHostController, appViewModel: AppViewModel) {
    NavHost(navController = navController, startDestination = Screen.SPLASH) {

        composable(Screen.SPLASH) {
            SplashScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.ONBOARDING) {
            OnboardingScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.LOGIN) {
            LoginScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.SIGNUP) {
            SignUpScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }

        composable(Screen.GET_LOCATION) {
            GetLocationScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.HOME) {
            HomeScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.LOST) {
            LostScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.FOUND) {
            FoundScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.POST) {
            PostScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.REPORT_CHOOSER) {
            ReportChooserScreen(navController = navController)
        }

        composable(
            route = Screen.SUCCESS,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            SuccessScreen(
                navController = navController,
                type = backStackEntry.arguments?.getString("type") ?: "post"
            )
        }

        composable(Screen.PARTICULAR_LOST) {
            ParticularLostScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.PARTICULAR_FOUND) {
            ParticularFoundScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.PARTICULAR_POST) {
            ParticularPostScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(
            route = Screen.VERIFY,
            arguments = listOf(
                navArgument("aniId") { type = NavType.StringType },
                navArgument("city")  { type = NavType.StringType },
                navArgument("type")  { type = NavType.StringType }
            )
        ) { backStackEntry ->
            VerifyScreen(
                navController = navController,
                appViewModel  = appViewModel,
                aniId = backStackEntry.arguments?.getString("aniId") ?: "",
                city  = backStackEntry.arguments?.getString("city")  ?: "",
                type  = backStackEntry.arguments?.getString("type")  ?: ""
            )
        }

        composable(Screen.PROFILE) {
            ProfileScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.NEARBY_CASES) {
            NearbyCasesScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.MAP_PICKER) {
            MapPickerScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(
            Screen.CAMERA,
            arguments = listOf(navArgument("front") { type = NavType.BoolType })
        ) { entry ->
            CameraCaptureScreen(
                navController = navController,
                appViewModel = appViewModel,
                frontCamera = entry.arguments?.getBoolean("front") ?: false
            )
        }

        composable(Screen.TRIVIA) {
            TriviaScreen(navController = navController)
        }

        composable(Screen.FULL_SCREEN) {
            FullScreenImageScreen(navController = navController, appViewModel = appViewModel)
        }
    }
}
