package abhishek.aniassist.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import abhishek.aniassist.R
import abhishek.aniassist.data.repository.FirebaseRepository
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * Displays a stored image reference:
 * - "img:<path>" → Base64 photo fetched from the Realtime DB "Images" node
 * - "http(s)://…" → old Storage download URL, loaded via Coil
 * - empty/anything else → paw placeholder
 */
@Composable
fun AniImage(
    imageRef: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    when {
        imageRef.startsWith(FirebaseRepository.IMAGE_PREFIX) -> {
            val bitmap by produceState<ImageBitmap?>(null, imageRef) {
                value = runCatching {
                    val b64 = FirebaseRepository.fetchImage(imageRef.removePrefix(FirebaseRepository.IMAGE_PREFIX))
                    if (b64.isEmpty()) null
                    else {
                        val bytes = Base64.decode(b64, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    }
                }.getOrNull()
            }
            // Fade the decoded image in over the placeholder — like trivia cards
            val alpha by animateFloatAsState(
                targetValue = if (bitmap != null) 1f else 0f,
                animationSpec = tween(400), label = "aniImageFade"
            )
            Box(modifier) {
                Image(
                    painter = painterResource(R.drawable.placeholder_animal),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = contentScale
                )
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!, contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = contentScale,
                        alpha = alpha
                    )
                }
            }
        }

        imageRef.startsWith("http") -> AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageRef)
                .crossfade(true)   // smooth fade-in like the trivia images
                .build(),
            contentDescription = null,
            placeholder = painterResource(R.drawable.placeholder_animal),
            error = painterResource(R.drawable.placeholder_animal),
            fallback = painterResource(R.drawable.placeholder_animal),
            modifier = modifier,
            contentScale = contentScale
        )

        else -> Image(
            painter = painterResource(R.drawable.placeholder_animal),
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
