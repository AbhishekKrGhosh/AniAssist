package abhishek.aniassist.ui.screens.report

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.theme.*

@Composable
fun SuccessScreen(navController: NavHostController, type: String) {
    val (title, subtitle) = when (type) {
        "lost"  -> "Report Submitted!" to "Your missing pet report is now live. Helpers nearby will see it right away."
        "found" -> "Report Submitted!" to "Your found-animal report is live. Hopefully we can reunite it soon."
        else    -> "Request Submitted!" to "Your help request is now live. Helpers nearby can see it and respond."
    }

    Scaffold(containerColor = Cream) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.success_dog),
                contentDescription = null,
                modifier = Modifier.size(220.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(10.dp))
            Text(
                subtitle,
                fontSize = 14.sp, color = Sage,
                textAlign = TextAlign.Center, lineHeight = 20.sp
            )
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.HOME) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Back to Home", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Submit another report",
                color = Forest, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { navController.navigate(Screen.REPORT_CHOOSER) }
                    .padding(8.dp)
            )
        }
    }
}
