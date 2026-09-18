package abhishek.aniassist.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import abhishek.aniassist.R
import abhishek.aniassist.navigation.Screen
import abhishek.aniassist.ui.theme.*
import abhishek.aniassist.viewmodel.AppViewModel
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val subtitle: String
)

private val pages = listOf(
    OnboardingPage(
        R.drawable.onboarding_help,
        "See an animal in trouble?",
        "Report it in seconds and get help from people nearby who care."
    ),
    OnboardingPage(
        R.drawable.onboarding_report,
        "Report lost & found pets",
        "Post a missing report or help reunite a found animal with its owner."
    ),
    OnboardingPage(
        R.drawable.onboarding_together,
        "Together for a kinder tomorrow",
        "Every report brings an animal one step closer to safety."
    )
)

@Composable
fun OnboardingScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    fun finish() {
        appViewModel.setOnboardingDone()
        navController.navigate(Screen.LOGIN) {
            popUpTo(Screen.ONBOARDING) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // Skip — top right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (pagerState.currentPage < pages.size - 1) {
                Text(
                    "Skip",
                    color = Sage, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { finish() }
                        .padding(8.dp)
                )
            } else {
                Spacer(Modifier.height(32.dp))
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val p = pages[page]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(p.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    p.title,
                    fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    color = Ink, textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    p.subtitle,
                    fontSize = 15.sp, color = Sage,
                    textAlign = TextAlign.Center, lineHeight = 22.sp
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        // Page dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.forEachIndexed { i, _ ->
                val selected = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (selected) Forest else Color(0xFFD9D2C4))
                )
            }
        }

        // Continue / Get Started button
        Button(
            onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    finish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                if (pagerState.currentPage < pages.size - 1) "Continue" else "Get Started",
                fontWeight = FontWeight.Bold, fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
