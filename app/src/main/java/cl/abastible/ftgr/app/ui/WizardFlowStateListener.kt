package cl.abastible.ftgr.app.ui

import android.content.Intent
import com.sap.cloud.mobile.flowv2.ext.FlowStateListener
import com.sap.cloud.mobile.foundation.model.AppConfig

class WizardFlowStateListener(private val application: SAPWizardApplication) :
    FlowStateListener() {

    private var userSwitchFlag = false

    override fun onAppConfigRetrieved(appConfig: AppConfig) {
    }

    override fun onApplicationReset() {
        this.application.resetApplication()
    }

    override fun onApplicationLocked() {
        super.onApplicationLocked()
        application.isApplicationUnlocked = false
    }

    override fun onFlowFinished(flowName: String?) {
        flowName?.let{
            application.isApplicationUnlocked = true
        }

        if (userSwitchFlag) {
            Intent(application, MainBusinessActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                application.startActivity(it)
            }
        }
    }



}
