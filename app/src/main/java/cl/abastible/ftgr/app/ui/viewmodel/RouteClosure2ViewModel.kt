package cl.abastible.ftgr.app.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteClosure2ViewModel @Inject constructor() : ViewModel() {
    val textFromEditText1 = MutableLiveData<String>("")
    val textFromEditText2 = MutableLiveData<String>("")
}