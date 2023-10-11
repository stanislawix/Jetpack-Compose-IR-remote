package sj.irremote

import android.content.res.Configuration
import android.hardware.ConsumerIrManager
import android.os.Build
import android.os.Bundle
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sj.irremote.infrared.highlevel.EnumCommand
import sj.irremote.infrared.highlevel.HexCommand
import sj.irremote.infrared.highlevel.IrAddress
import sj.irremote.infrared.highlevel.JvcCommand
import sj.irremote.infrared.highlevel.MpmCommand
import sj.irremote.infrared.lowlevel.IRJvcFactory
import sj.irremote.infrared.lowlevel.IRMessage
import sj.irremote.infrared.lowlevel.IRNecFactory
import sj.irremote.ui.theme.ComposeIRRemoteTheme

/**
 * This is a mobile app that uses the ConsumerIrManager API to send IR commands to a fan. More handy than a real remote - you almost always have your phone on you.
 */

class MainActivity : ComponentActivity() {

    private var irManager: ConsumerIrManager? = null
    private var vibrator: Vibrator? = null
    private var vibratorManager: VibratorManager? = null

    val mpmAddress = IrAddress(hexAddress = 0x0, reverseAddressBits = false)
    val jvcAddress = IrAddress(hexAddress = 0xA3, reverseAddressBits = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(::sendIrCommand)
        }

//        val remoteViewModel by viewModels<RemoteViewModel>()

        irManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
        } else {
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (irManager == null) {
            Log.e("Compose IR remote app", "No IR Emitter found")
            Toast.makeText(this, "No IR Emitter found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (!irManager!!.hasIrEmitter()) {
            Log.e("Compose IR remote app", "No IR Emitter found")
            Toast.makeText(this, "No IR Emitter found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val frequencies = irManager!!.carrierFrequencies
        frequencies.forEach {
            Log.d("Compose IR remote app", "Frequency: ${it.minFrequency} - ${it.maxFrequency}")
        }
    }

    private fun sendIrCommand(enumCommand: EnumCommand) {
        val irCommand: IRMessage

        when (enumCommand) {
            EnumCommand.OFF -> {
//                irCommand = getIrCommand(JvcCommand.ON_OFF)
                irCommand = getIrCommand(MpmCommand.OFF)
            }

            EnumCommand.ON_SPEED -> {
//                irCommand = getIrCommand(JvcCommand.VOLUME_UP)
                irCommand = getIrCommand(MpmCommand.ON_SPEED)
            }

            EnumCommand.MODE -> {
//                irCommand = getIrCommand(JvcCommand.VOLUME_DOWN)
                irCommand = getIrCommand(MpmCommand.MODE)
            }

            EnumCommand.TIMER -> {
//                irCommand = getIrCommand(JvcCommand.NUMBER_10)
                irCommand = getIrCommand(MpmCommand.TIMER)
            }

            EnumCommand.SWING -> {
//                irCommand = getIrCommand(JvcCommand.NUMBER_10_PLUS)
                irCommand = getIrCommand(MpmCommand.SWING)
            }
        }

        Log.d("Compose IR remote app", "Sending command: $enumCommand")
        sendIrCommand(irCommand)

    }

    private fun bruteforceTransmitCommand(
        commandRangeStart: Int = 0,
        commandRangeEnd: Int = 255,
        address: IrAddress = IrAddress(0x0, true),
        sleepBetweenCommands: Int = 150
    ) {
        val addr = address.toInt()
        for (cmd in commandRangeStart..commandRangeEnd) {
            val irMessage = IRJvcFactory.create(cmd, addr, 3)
            irManager?.transmit(irMessage.frequency, irMessage.message)
            Log.d("Compose IR remote app", "Sending command: $cmd")
            Thread.sleep(sleepBetweenCommands.toLong())
        }
    }

    private fun sendIrCommand(irMessage: IRMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager?.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            )
        } else {
            vibrator?.vibrate(100)
        }
        irManager?.transmit(irMessage.frequency, irMessage.message)
    }

    private fun getIrCommand(cmd: HexCommand): IRMessage {
        val hexCommand = cmd.toInt()
        val hexAddress: Int
        val irCommand: IRMessage
        when (cmd) {
            is MpmCommand -> {
                hexAddress = mpmAddress.toInt()
                irCommand = IRNecFactory.create(hexCommand, hexAddress, 0)
            }

            is JvcCommand -> {
                hexAddress = jvcAddress.toInt()
                irCommand = IRJvcFactory.create(hexCommand, hexAddress, 3)
            }

            else -> throw IllegalArgumentException("Unknown command type!")
        }
        Log.d(
            "Compose IR remote app",
            "Preparing IR ${cmd.javaClass.simpleName}: ${cmd.getEnumName()} code=${cmd.toInt()} (proper: $hexCommand), address = $hexAddress (proper: $hexAddress)"
        )
        return irCommand
    }


}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark mode"
)
@Composable
private fun MainContent(onCommandSend: (EnumCommand) -> Unit = { }) {
    ComposeIRRemoteTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Header(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                )
                FanImage(
                    Modifier
                        .size(200.dp, 300.dp)
                        .fillMaxSize()
                )
                Remote(onCommandSend = onCommandSend)
            }
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.fan_header),
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium,
        softWrap = true,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun FanImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.fan_photo),
        contentDescription = "MPM MWP-19 fan photo",
        modifier = modifier
    )
    Spacer(modifier = Modifier.height(24.dp))
}


@Composable
fun Remote(modifier: Modifier = Modifier, onCommandSend: (EnumCommand) -> Unit) {
    var lastClickedButton: String? by rememberSaveable {
        mutableStateOf("None")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {

//        Text(text = "Last clicked button: $lastClickedButton")

        Spacer(modifier = Modifier.height(16.dp))

        RemoteButton(
            label = stringResource(R.string.fan_off),
            onClick = { onCommandSend(EnumCommand.OFF) }
        )

        RemoteButton(
            label = stringResource(R.string.fan_on_speed),
            onClick = { onCommandSend(EnumCommand.ON_SPEED) }
        )

        RemoteButton(
            label = stringResource(R.string.fan_mode),
            onClick = { onCommandSend(EnumCommand.MODE) }
        )

        RemoteButton(
            label = stringResource(R.string.fan_timer),
            onClick = { onCommandSend(EnumCommand.TIMER) }
        )

        RemoteButton(
            label = stringResource(R.string.fan_swing),
            onClick = { onCommandSend(EnumCommand.SWING) }
        )
    }
}

@Composable
fun RemoteButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = label)
    }
    Spacer(modifier = Modifier.height(8.dp))
}