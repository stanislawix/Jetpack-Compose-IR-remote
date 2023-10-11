# Jetpack-Compose-IR-remote

A simple IR remote app for an MPM MWP-19 room fan. Or a JVC MX-S20 audio system (not in the UI yet).
This app is yet to be somehow modularized.

# Credits
**Substantial amounts of code used for generating IR messages were taken from [ZoserLock's Android-IR-remote](https://github.com/ZoserLock/android-ir-remote).**

Thanks to the code from ZoserLock's repository, messages using NEC and Sony SIRC infrared protocols can be generated.

I have added support for the JVC infrared protocol, based on the mentioned ZoserLock's code and with the help of the incredibly well-documented [Arduino-IRremote](https://github.com/Arduino-IRremote/Arduino-IRremote)'s code.

# Features
- GUI for remote control of the MPM MWP-19 room fan,
- built-in support for NEC, SIRC and JVC infrared protocols - the code can be changed in order to control a device other than the fan.

# Screenshots

