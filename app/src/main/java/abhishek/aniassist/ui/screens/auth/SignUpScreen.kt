package abhishek.aniassist.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.data.repository.FirebaseRepository
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    AuthScaffold(
        backgroundRes = R.drawable.auth_bg_signup,
        artRes = listOf(R.drawable.auth_art_dog),
        quoteRes = R.drawable.auth_quote2,
        title = "Create Account",
        subtitle = "Join AniAssist — help animals near you"
    ) {
        AuthField(
            value = name,
            placeholder = "Full name",
            onValueChange = { name = it; errorMsg = "" }
        )
        Spacer(Modifier.height(14.dp))

        AuthField(
            value = email,
            placeholder = "Email",
            keyboardType = KeyboardType.Email,
            onValueChange = { email = it; errorMsg = "" }
        )
        Spacer(Modifier.height(14.dp))

        AuthField(
            value = password,
            placeholder = "Password (min 6 chars)",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            onValueChange = { password = it; errorMsg = "" }
        )
        Spacer(Modifier.height(8.dp))

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                when {
                    name.isBlank()      -> { errorMsg = "Please enter your name"; return@Button }
                    email.isBlank()     -> { errorMsg = "Please enter your email"; return@Button }
                    password.length < 6 -> { errorMsg = "Password must be at least 6 characters"; return@Button }
                }
                isLoading = true
                scope.launch {
                    val result = FirebaseRepository.signUp(email.trim(), password, name.trim())
                    isLoading = false
                    result.onSuccess { emailSaved ->
                        appViewModel.saveEmail(emailSaved)
                        appViewModel.saveName(name.trim())
                        navController.navigate(Screen.GET_LOCATION) {
                            popUpTo(Screen.SIGNUP) { inclusive = true }
                        }
                    }.onFailure {
                        errorMsg = "Sign up failed: ${it.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("Sign Up", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(14.dp))
        Row {
            Text("Already have an account? ", color = Sage, fontSize = 13.sp)
            Text(
                "Login",
                color = Forest,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { navController.navigate(Screen.LOGIN) }
            )
        }
    }
}
