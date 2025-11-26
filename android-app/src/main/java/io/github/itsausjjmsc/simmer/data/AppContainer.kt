package io.github.itsausjjmsc.simmer.data

interface AppContainer {
    val audioRepository: AudioRepository
    val settingsRepository: SettingsRepository
}

class DefaultAppContainer : AppContainer {
    override val audioRepository: AudioRepository = DefaultAudioRepository()
    override val settingsRepository: SettingsRepository = DefaultSettingsRepository()
}
