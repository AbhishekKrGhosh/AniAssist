package abhishek.aniassist.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object UserPreferencesKeys {
    val EMAIL = stringPreferencesKey("email")
    val NAME = stringPreferencesKey("name")
    val CITY = stringPreferencesKey("city")
    val ADDRESS = stringPreferencesKey("address")
    val LAT = doublePreferencesKey("lat")
    val LNG = doublePreferencesKey("lng")
    val AVATAR = stringPreferencesKey("avatar")
    val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
}

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    val email: Flow<String> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.EMAIL] ?: ""
    }

    val name: Flow<String> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.NAME] ?: ""
    }

    val city: Flow<String> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.CITY] ?: ""
    }

    val address: Flow<String> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.ADDRESS] ?: ""
    }

    val lat: Flow<Double?> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.LAT]
    }

    val lng: Flow<Double?> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.LNG]
    }

    val avatar: Flow<String> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.AVATAR] ?: ""
    }

    val onboardingDone: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.ONBOARDING_DONE] ?: false
    }

    suspend fun setOnboardingDone() {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.ONBOARDING_DONE] = true
        }
    }

    suspend fun saveEmail(email: String) {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.EMAIL] = email
        }
    }

    suspend fun saveName(name: String) {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.NAME] = name
        }
    }

    suspend fun saveAvatar(ref: String) {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.AVATAR] = ref
        }
    }

    suspend fun saveCity(city: String, address: String = "", lat: Double? = null, lng: Double? = null) {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.CITY] = city
            prefs[UserPreferencesKeys.ADDRESS] = address
            if (lat != null && lng != null) {
                prefs[UserPreferencesKeys.LAT] = lat
                prefs[UserPreferencesKeys.LNG] = lng
            } else {
                prefs.remove(UserPreferencesKeys.LAT)
                prefs.remove(UserPreferencesKeys.LNG)
            }
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.EMAIL] = ""
            prefs[UserPreferencesKeys.NAME] = ""
            prefs[UserPreferencesKeys.CITY] = ""
            prefs[UserPreferencesKeys.ADDRESS] = ""
            prefs[UserPreferencesKeys.AVATAR] = ""
            prefs.remove(UserPreferencesKeys.LAT)
            prefs.remove(UserPreferencesKeys.LNG)
        }
    }
}
