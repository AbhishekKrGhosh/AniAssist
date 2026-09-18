package abhishek.aniassist.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import abhishek.aniassist.R
import abhishek.aniassist.ui.theme.*

/**
 * Shared auth-page layout — cream background, foliage frame at the bottom,
 * animal art + paw logo on top, and a white rounded card for the form.
 */
@Composable
fun AuthScaffold(
    backgroundRes: Int,
    artRes: List<Int>,
    quoteRes: Int?,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        // Faint scattered paws behind the card
        Image(
            painter = painterResource(R.drawable.logo_paw),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 90.dp)
                .size(30.dp)
                .rotate(-25f)
                .alpha(0.12f),
            colorFilter = ColorFilter.tint(Forest)
        )
        Image(
            painter = painterResource(R.drawable.logo_paw),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 30.dp, top = 150.dp)
                .size(24.dp)
                .rotate(20f)
                .alpha(0.12f),
            colorFilter = ColorFilter.tint(Forest)
        )

        // Bottom foliage frame
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        // Quote sticker bottom-left over the foliage
        if (quoteRes != null) {
            Image(
                painter = painterResource(quoteRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 18.dp, bottom = 26.dp)
                    .height(64.dp)
            )
        }

        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 110.dp),  // bias content up so it clears the foliage
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(28.dp))

            // Animal art (one or more) + logo + wordmark
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                artRes.forEach { res ->
                    Image(
                        painter = painterResource(res),
                        contentDescription = null,
                        modifier = Modifier.height(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Image(
                painter = painterResource(R.drawable.logo_paw),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("AniAssist", color = Forest, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text("Small help. Big impact.", color = Sage, fontSize = 13.sp)
            Spacer(Modifier.height(24.dp))

            // White form card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(title, color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(subtitle, color = Sage, fontSize = 13.sp)
                    Spacer(Modifier.height(20.dp))
                    content()
                }
            }

        }
    }
}
