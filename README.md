# Simmer
Tells your home theater to simmer down, now! 🍲  
Automatic volume leveling for IP-controlled receivers (Marantz, Denon, & more).

> 🚧 **Status: Pre-Alpha / Active Development**  
> Architecture is still in flux. Please avoid submitting PRs until the project stabilizes.

## What is Simmer?

Simmer is an Android-based, real-time loudness controller that keeps your home theater from getting too loud — or too quiet — without relying on built-in “Night Mode” or Dynamic Volume features.

Instead of analyzing the audio **inside the AVR**, Simmer analyzes **real room loudness** using your Android device’s microphone.  
It then adjusts AVR volume automatically using IP control (starting with Marantz/Denon).

**Core pipeline:**

graph TD
A[RoomAudioSampler] --> B[LoudnessEngine]
B --> C[LoudnessController]
C --> D[AVR Client]

*(Smoothing, Silence Detection → Attack/Release, Targets → IP Control)*

Simmer runs as an Android foreground service and will later include a UI for monitoring, tuning, and profile control.

---

## Current Features (Pre-Alpha)

- ✔ LoudnessEngine with time-based smoothing + silence detection  
- ✔ LoudnessController with attack/release logic, target following, safety caps  
- ✔ Nudge vs SetVolume logic with big-mismatch and fade-to-safe behavior  
- ✔ Core logging system (`SimmerLog`)  
- ✔ Android Logcat bridge for observing real-time behavior  
- ✔ Foreground service wiring: mic → engine → controller → debug logs
- ✔ Runtime Microphone Permission handling (Gate)

---

## Roadmap

Planned features include:

- Marantz/Denon AVR control (full command set, power state detection)
- Log Screen (replace placeholder demo data with real events)
- Profile presets: Movie, Late Night, Dialogue Boost, Custom
- Quiet Hours scheduling
- Sensitivity calibration wizard
- Web/local control panel
- Network auto-discovery for AVRs
- Plugin API for receivers beyond Marantz/Denon

---

## Build & Run (Developers)

Simmer uses modern Android tooling.

**Requirements:**
- **Android Studio Koala or newer** (Ladybug recommended)
- Android SDK 33–34
- Kotlin + Compose Material 3
- Gradle + AGP current stable versions

**To run:**
1. Open the project in Android Studio.
2. Select the **debug** build variant.
3. Deploy to a device with a microphone (Android TV or phone).
4. Observe behavior through:

adb logcat | grep "Simmer-"

The debug pipeline logs will show raw vs smoothed dB, silence detection, and controller decisions.
- App will request RECORD_AUDIO permission on first launch.

---

## Contributing

Thanks for your interest!  
Simmer is still stabilizing its architecture, so contributions aren’t encouraged yet.

Feel free to open issues for architecture questions, bug reports, or AVR protocol findings. PRs will be welcomed once the API and internal modules are finalized.

---

## License

This project is licensed under the MIT License — see `LICENSE` for details.