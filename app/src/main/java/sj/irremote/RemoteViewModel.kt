package sj.irremote

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RemoteViewModel : ViewModel() {

    private val _sendingIrCommand = mutableStateOf(false)
    val sendingIrCommand get() = _sendingIrCommand

}