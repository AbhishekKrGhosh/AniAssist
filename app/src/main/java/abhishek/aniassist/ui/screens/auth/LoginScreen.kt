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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun LoginScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    AuthScaffold(
        backgroundRes = R.drawable.auth_bg_login,
        artRes = listOf(R.drawable.auth_art_dog2),
        quoteRes = R.drawable.auth_quote1,
        title = "Welcome back",
        subtitle = "The animals missed you."
    ) {
        AuthField(
            value = email,
            placeholder = "Email",
            keyboardType = KeyboardType.Email,
            onValueChange = { email = it; errorMsg = "" }
        )
        Spacer(Modifier.height(14.dp))

        AuthField(
            value = password,
            placeholder = "Password",
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
                if (email.isBlank() || password.isBlank()) {
                    errorMsg = "Please fill in all fields"
                    return@Button
                }
                isLoading = true
                scope.launch {
                    val result = FirebaseRepository.signIn(email.trim(), password)
                    isLoading = false
                    result.onSuccess { emailSaved ->
                        appViewModel.saveEmail(emailSaved)
                        appViewModel.saveName(FirebaseRepository.getUserName(emailSaved))
                        appViewModel.saveAvatar(FirebaseRepository.getUserAvatar(emailSaved))
                        navController.navigate(Screen.GET_LOCATION) {
                            popUpTo(Screen.LOGIN) { inclusive = true }
                        }
                    }.onFailure {
                        errorMsg = "Login failed. Check your email or password."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = "Forgot Password?",
            color = Forest,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.clickable { navController.navigate(Screen.FORGOT_PASSWORD) }
        )
        Spacer(Modifier.height(10.dp))
        Row {
            Text("Don't have an account? ", color = Sage, fontSize = 13.sp)
            Text(
                "Sign Up",
                color = Forest,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { navController.navigate(Screen.SIGNUP) }
            )
        }
    }
}

// ─── Shared auth field — used by Login / SignUp / ForgotPassword ─────────────

@Composable
internal fun AuthField(
    value: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Sage, fontSize = 15.sp) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Forest,
            unfocusedBorderColor = Color(0xFFE3DDD0),
            cursorColor = Forest,
            focusedTextColor = Ink,
            unfocusedTextColor = Ink
        )
    )
}
