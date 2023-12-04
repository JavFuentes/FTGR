package cl.abastible.ftgr.app.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// Esta clase es un Singleton para manejar las preferencias compartidas a lo largo de la aplicación.
@Singleton
class SharedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {

        // Constantes para los nombres de las preferencias y las claves.
        private const val PREFS_NAME = "VTGR_Prefs"
        private const val NAME_KEY = "NameKey"
        private const val USERNAME_KEY = "UsernameKey"
        private const val SYNC_DATE_KEY = "SyncDateKey"
    }

    // Inicialización de las preferencias compartidas.
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Propiedad para obtener o establecer el nombre en las preferencias compartidas.
    var name: String?
        get() = sharedPreferences.getString(NAME_KEY, null)
        set(value) = sharedPreferences.edit().putString(NAME_KEY, value).apply()

    // Propiedad para obtener o establecer el nombre de usuario en las preferencias compartidas.
    var username: String?
        get() = sharedPreferences.getString(USERNAME_KEY, null)
        set(value) = sharedPreferences.edit().putString(USERNAME_KEY, value).apply()

    // Método para obtener la última fecha de sincronización de las preferencias compartidas.
    fun getLastSyncDate(): String? {
        return sharedPreferences.getString(SYNC_DATE_KEY, "")
    }

    // Método para establecer la última fecha de sincronización en las preferencias compartidas.
    fun setLastSyncDate(date: String) {
        sharedPreferences.edit().putString(SYNC_DATE_KEY, date).apply()
    }

    // Método para obtener la fecha actual como una cadena en el formato "yyyy-MM-dd".
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
