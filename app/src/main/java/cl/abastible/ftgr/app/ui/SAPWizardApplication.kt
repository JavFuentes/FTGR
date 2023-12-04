package cl.abastible.ftgr.app.ui

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.sap.cloud.mobile.foundation.mobileservices.MobileService
import com.sap.cloud.mobile.foundation.mobileservices.SDKInitializer
import com.sap.cloud.mobile.foundation.theme.ThemeDownloadService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SAPWizardApplication: Application() {

    internal var isApplicationUnlocked = false
    lateinit var preferenceManager: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        initServices()
    }

    /**
     * Clears all user-specific data and configuration from the application, essentially resetting it to its initial
     * state.
     *
     * If client code wants to handle the reset logic of a service, here is an example:
     *
     *   SDKInitializer.resetServices { service ->
     *       return@resetServices if( service is PushService ) {
     *           PushService.unregisterPushSync(object: CallbackListener {
     *               override fun onSuccess() {
     *               }
     *
     *               override fun onError(p0: Throwable) {
     *               }
     *           })
     *           true
     *       } else {
     *           false
     *       }
     *   }
     */

    fun resetApplication() {
        preferenceManager.also {
            it.edit().clear().apply()
        }
        isApplicationUnlocked = false

    }

    private fun initServices() {
        val services = mutableListOf<MobileService>()
        services.add(ThemeDownloadService(this))

        SDKInitializer.start(this, * services.toTypedArray())
    }
}