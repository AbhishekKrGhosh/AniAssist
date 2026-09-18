package abhishek.aniassist.ui.screens.report

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportChooserScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Ink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                "What do you want\nto report?",
                fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Ink, lineHeight = 38.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Choose the type of report you'd like to create.",
                fontSize = 15.sp, color = Sage
            )

            Spacer(Modifier.height(32.dp))

            ReportTypeCard(
                title = "Injured Animal",
                subtitle = "Request help for a hurt or sick animal",
                iconRes = R.drawable.icon_injured,
                bg = CoralSoft
            ) { navController.navigate(Screen.POST) }

            Spacer(Modifier.height(16.dp))

            ReportTypeCard(
                title = "Lost Pet",
                subtitle = "Report your missing pet to nearby helpers",
                iconRes = R.drawable.icon_lost,
                bg = AmberSoft
            ) { navController.navigate(Screen.LOST) }

            Spacer(Modifier.height(16.dp))

            ReportTypeCard(
                title = "Found Animal",
                subtitle = "Found an animal? Help reunite it",
                iconRes = R.drawable.icon_found,
                bg = SkySoft
            ) { navController.navigate(Screen.FOUND) }

            Spacer(Modifier.weight(1f))

            // Emergency hint — matches mockup footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CoralSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Vibration, null, tint = Coral, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Emergency?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text(
                        "Shake your phone anytime to quickly open your profile & change city.",
                        fontSize = 12.sp, color = Sage, textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReportTypeCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    bg: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = Sage)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Sage)
    }
}
