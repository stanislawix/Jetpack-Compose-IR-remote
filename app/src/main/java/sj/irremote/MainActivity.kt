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
import sj.irremote.ui.theme.ComposeIRRemoteTheme

class MainActivity : ComponentActivity() {

    private var irManager: ConsumerIrManager? = null
    private var vibrator: Vibrator? = null
    private var vibratorManager: VibratorManager? = null


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
        Log.d("Compose IR remote app", "Sending command: $enumCommand")
        val commandHexInt: String
        val commandInt: Int
        val commandBinary: String
        val commandHex: Long
        val commandEz: Int

        when (enumCommand) {
            EnumCommand.OFF -> {
                commandHexInt = "46"
                commandInt = -1186529536
                commandBinary = "10111001010001101111111100000000"
                commandHex = 0xB946FF00L
                commandEz = 98
            }

            EnumCommand.ON_SPEED -> {
                commandHexInt = "44"
                commandInt = -1153106176
                commandBinary = "10111011010001001111111100000000"
                commandHex = 0xBB44FF00L
                commandEz = 34
            }

            EnumCommand.MODE -> {
                commandHexInt = "15"
                commandInt = -367657216
                commandBinary = "11101010000101011111111100000000"
                commandHex = 0xEA15FF00L
                commandEz = 168
            }

            EnumCommand.TIMER -> {
                commandHexInt = "16"
                commandInt = -384368896
                commandBinary = "11101001000101101111111100000000"
                commandHex = 0xE916FF00L
                commandEz = 104
            }

            EnumCommand.SWING -> {
                commandHexInt = "8"
                commandInt = -150405376
                commandBinary = "11110111000010001111111100000000"
                commandHex = 0xF708FF00L
                commandEz = 16
            }
        }

        val necCommand = IRNecFactory.create(commandEz, 0x0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager?.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            )
        } else {
            vibrator?.vibrate(100)
        }
        irManager?.transmit(necCommand.frequency, necCommand.message)

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
        // A surface container using the 'background' color from the theme
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
//                FanImage()
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
        contentDescription = "MPM fan photo",
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