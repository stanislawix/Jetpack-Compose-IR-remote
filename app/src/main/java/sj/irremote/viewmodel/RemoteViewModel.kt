package sj.irremote.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * ViewModel to be implemented (work in progress)
 */
class RemoteViewModel : ViewModel() {

    private val _sendingIrCommand = mutableStateOf(false)
    val sendingIrCommand get() = _sendingIrCommand

}