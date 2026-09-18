package abhishek.aniassist.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import abhishek.aniassist.data.model.AnimalFoundInfo
import abhishek.aniassist.data.model.AnimalLostInfo
import abhishek.aniassist.data.model.AnimalPostInfo
import abhishek.aniassist.data.model.VerifyInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase
        .getInstance("https://aniassist-7cef4-default-rtdb.firebaseio.com").reference

    // ─── Auth ─────────────────────────────────────────────────────────────────

    suspend fun signIn(email: String, password: String): Result<String> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        email.lowercase()
    }

    suspend fun signUp(email: String, password: String, name: String): Result<String> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
        val sanitized = sanitizeEmail(email)
        db.child("Users").child(sanitized).child("name").setValue(name).await()
        email.lowercase()
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    fun signOut() = auth.signOut()

    fun isLoggedIn(): Boolean = auth.currentUser != null

    /** Fetch the display name saved at signup: Users/{sanitizedEmail}/name */
    suspend fun getUserName(email: String): String = runCatching {
        val snap = db.child("Users").child(sanitizeEmail(email)).child("name").get().await()
        snap.getValue(String::class.java) ?: ""
    }.getOrDefault("")

    /** Update the display name: Users/{sanitizedEmail}/name */
    suspend fun updateUserName(email: String, name: String): Result<Unit> = runCatching {
        db.child("Users").child(sanitizeEmail(email)).child("name").setValue(name).await()
    }

    /** Save the avatar image ref: Users/{sanitizedEmail}/avatar */
    suspend fun updateUserAvatar(email: String, imageRef: String): Result<Unit> = runCatching {
        db.child("Users").child(sanitizeEmail(email)).child("avatar").setValue(imageRef).await()
    }

    /** Fetch the avatar image ref saved in the profile */
    suspend fun getUserAvatar(email: String): String = runCatching {
        val snap = db.child("Users").child(sanitizeEmail(email)).child("avatar").get().await()
        snap.getValue(String::class.java) ?: ""
    }.getOrDefault("")

    // ─── Realtime Feeds ───────────────────────────────────────────────────────

    fun getLostAnimals(city: String): Flow<List<AnimalLostInfo>> = callbackFlow {
        val ref = db.child("Location").child(city).child("Lost")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.reversed().mapNotNull {
                    it.getValue(AnimalLostInfo::class.java)
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getPostAnimals(city: String): Flow<List<AnimalPostInfo>> = callbackFlow {
        val ref = db.child("Location").child(city).child("Post")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.reversed().mapNotNull {
                    it.getValue(AnimalPostInfo::class.java)
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getFoundAnimals(city: String): Flow<List<AnimalFoundInfo>> = callbackFlow {
        val ref = db.child("Location").child(city).child("Found")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.reversed().mapNotNull {
                    it.getValue(AnimalFoundInfo::class.java)
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ─── Upload Data ──────────────────────────────────────────────────────────

    suspend fun uploadLostAnimal(city: String, info: AnimalLostInfo): Result<Unit> = runCatching {
        db.child("Location").child(city).child("Lost").child(info.animalId!!).setValue(info).await()
    }

    suspend fun uploadFoundAnimal(city: String, info: AnimalFoundInfo): Result<Unit> = runCatching {
        db.child("Location").child(city).child("Found").child(info.animalId!!).setValue(info).await()
    }

    suspend fun uploadPostAnimal(city: String, info: AnimalPostInfo): Result<Unit> = runCatching {
        db.child("Location").child(city).child("Post").child(info.animalId!!).setValue(info).await()
    }

    suspend fun uploadVerifyInfo(email: String, type: String, info: VerifyInfo): Result<Unit> = runCatching {
        db.child("Users").child(email).child("Proof").child(type).child(info.dataId!!).setValue(info).await()
    }

    // ─── Image Upload (Base64 in Realtime DB — no Storage/billing needed) ─────

    const val IMAGE_PREFIX = "img:"

    private const val IMAGE_MAX_DIM = 1024   // longest side — feed/detail photos
    const val AVATAR_MAX_DIM = 512           // profile pics render ≤120dp — small is fine
    private const val IMAGE_JPEG_QUALITY = 70

    suspend fun uploadImage(
        storagePath: String, uri: Uri, context: Context,
        maxDim: Int = IMAGE_MAX_DIM
    ): Result<String> = runCatching {
        val bitmap = decodeScaledBitmap(uri, context, maxDim)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_JPEG_QUALITY, baos)
        val base64 = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP)

        db.child("Images").child(storagePath).setValue(base64).await()
        IMAGE_PREFIX + storagePath
    }

    /** Decode a Uri to a Bitmap capped at [maxDim] px on the longest side —
     *  samples first for memory safety, then scales to the exact cap. */
    private fun decodeScaledBitmap(uri: Uri, context: Context, maxDim: Int): Bitmap {
        val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri).use {
            android.graphics.BitmapFactory.decodeStream(it, null, bounds)
        }
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxDim) sample *= 2
        val opts = android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }
        val decoded = context.contentResolver.openInputStream(uri).use {
            android.graphics.BitmapFactory.decodeStream(it, null, opts)
        } ?: throw IllegalStateException("Couldn't decode image")
        val scale = maxDim.toFloat() / maxOf(decoded.width, decoded.height)
        return if (scale < 1f) {
            android.graphics.Bitmap.createScaledBitmap(
                decoded,
                (decoded.width * scale).toInt(),
                (decoded.height * scale).toInt(),
                true
            )
        } else decoded
    }

    suspend fun fetchImage(ref: String): String =
        db.child("Images").child(ref).get().await().getValue(String::class.java) ?: ""

    /** Delete a stored base64 image (e.g. a replaced avatar). Non-fatal on failure. */
    suspend fun deleteImage(imageRef: String) = runCatching {
        if (imageRef.startsWith(IMAGE_PREFIX)) {
            db.child("Images").child(imageRef.removePrefix(IMAGE_PREFIX)).removeValue().await()
        }
    }

    private fun getFileSize(uri: Uri, context: Context): Long {
        val fd = context.contentResolver.openFileDescriptor(uri, "r")
        val size = fd?.statSize ?: 0
        fd?.close()
        return size
    }

    // ─── Delete Record (Verify flow) ──────────────────────────────────────────

    suspend fun deleteRecord(city: String, type: String, aniId: String): Result<Unit> = runCatching {
        // Try to delete image from the Images node first (non-fatal if missing)
        try {
            db.child("Images").child("Location").child(city).child(type).child(aniId)
                .removeValue().await()
        } catch (_: Exception) { /* image may not exist, continue */ }

        db.child("Location").child(city).child(type).child(aniId).removeValue().await()
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    fun generateId(): String = db.push().key ?: java.util.UUID.randomUUID().toString()

    fun sanitizeEmail(email: String): String =
        email.lowercase()
            .replace(".", "")
            .replace("#", "")
            .replace("$", "")
            .replace("[", "")
            .replace("]", "")

    fun sanitizeCity(city: String): String =
        city.lowercase()
            .replace(" ", "")
            .replace(".", "")
            .replace("#", "")
            .replace("$", "")
            .replace("[", "")
            .replace("]", "")

    fun currentDateTime(): String =
        SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
}
