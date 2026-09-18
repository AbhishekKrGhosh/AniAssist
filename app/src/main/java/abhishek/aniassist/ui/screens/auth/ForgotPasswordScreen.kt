package abhishek.aniassist.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import abhishek.aniassist.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AuthScaffold(
        backgroundRes = R.drawable.auth_bg_forgot,
        artRes = listOf(R.drawable.auth_art_cat),
        quoteRes = R.drawable.auth_quote1,
        title = "Reset Password",
        subtitle = "We'll send a reset link to your email"
    ) {
        AuthField(
            value = email,
            placeholder = "Email",
            keyboardType = KeyboardType.Email,
            onValueChange = { email = it; message = "" }
        )
        Spacer(Modifier.height(12.dp))

        if (message.isNotEmpty()) {
            Text(
                message,
                color = if (isError) MaterialTheme.colorScheme.error else Forest,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (email.isBlank()) { isError = true; message = "Please enter your email"; return@Button }
                isLoading = true
                scope.launch {
                    val result = FirebaseRepository.sendPasswordReset(email.trim())
                    isLoading = false
                    result.onSuccess {
                        isError = false
                        message = "Reset link sent to $email"
                    }.onFailure {
                        isError = true
                        message = "Failed to send reset email"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else {
                Image(
                    painter = painterResource(R.drawable.auth_send),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )
                Spacer(Modifier.width(8.dp))
                Text("Send Reset Link", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Forest),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE3DDD0))
            )
        ) {
            Text("Cancel", fontWeight = FontWeight.SemiBold)
        }
    }
}
