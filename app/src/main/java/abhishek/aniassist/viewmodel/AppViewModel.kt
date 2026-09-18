package abhishek.aniassist.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import abhishek.aniassist.data.model.AnimalFoundInfo
import abhishek.aniassist.data.model.AnimalLostInfo
import abhishek.aniassist.data.model.AnimalPostInfo
import abhishek.aniassist.data.preferences.UserPreferences
import abhishek.aniassist.data.preferences.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application.dataStore)

    val email: StateFlow<String> = prefs.email.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ""
    )

    val name: StateFlow<String> = prefs.name.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ""
    )

    val city: StateFlow<String> = prefs.city.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ""
    )

    val address: StateFlow<String> = prefs.address.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ""
    )

    val onboardingDone: StateFlow<Boolean> = prefs.onboardingDone.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), false
    )

    val avatar: StateFlow<String> = prefs.avatar.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ""
    )

    // Detected city coordinates — used as the map picker's default camera
    val savedLat: StateFlow<Double?> = prefs.lat.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null
    )
    val savedLng: StateFlow<Double?> = prefs.lng.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null
    )

    // Shared selection state for detail screens (no need for a DB round-trip)
    var selectedLostAnimal: AnimalLostInfo? = null
    var selectedFoundAnimal: AnimalFoundInfo? = null
    var selectedPostAnimal: AnimalPostInfo? = null
    var selectedImageUri: String = ""

    // Location picked on the map — set by MapPickerScreen, consumed by report forms
    var pickedLat by mutableStateOf<Double?>(null)
    var pickedLng by mutableStateOf<Double?>(null)
    var pickedAddress by mutableStateOf("")

    // Photo captured in-app — set by CameraCaptureScreen, consumed by forms/profile
    var capturedPhotoUri by mutableStateOf<android.net.Uri?>(null)

    fun saveEmail(email: String) = viewModelScope.launch {
        prefs.saveEmail(email)
    }

    fun saveName(name: String) = viewModelScope.launch {
        prefs.saveName(name)
    }

    fun saveAvatar(ref: String) = viewModelScope.launch {
        prefs.saveAvatar(ref)
    }

    fun saveCity(city: String, address: String = "", lat: Double? = null, lng: Double? = null) =
        viewModelScope.launch {
            prefs.saveCity(city, address, lat, lng)
        }

    fun setOnboardingDone() = viewModelScope.launch {
        prefs.setOnboardingDone()
    }

    fun clearSession() = viewModelScope.launch {
        prefs.clearSession()
    }
}
