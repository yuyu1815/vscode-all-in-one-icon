package com.github.yuyu1815.vscodeallinoneicon

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Persistent icon theme configuration
 * Saves and loads settings for icon theme priority
 */
@State(
    name = "IconThemeSettings",
    storages = [Storage("vscode-all-in-one-icon-settings.json")]
)
@Service(Service.Level.APP)
class IconThemeConfig : PersistentStateComponent<IconThemeConfig.State> {

    data class State(
        var activeThemes: MutableList<String> = mutableListOf(IconTheme.VSCODE_ICONS.displayName),
        var isEnabled: Boolean = true,
        var language: String = "en"
    ) {
        // Empty constructor for JSON serialization
        constructor() : this(
            activeThemes = mutableListOf(IconTheme.VSCODE_ICONS.displayName),
            isEnabled = true,
            language = "en"
        )
    }

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
        // Migration: if activeThemes is empty but old selectedTheme might exist
        if (this.state.activeThemes.isEmpty()) {
            this.state.activeThemes = mutableListOf(IconTheme.VSCODE_ICONS.displayName)
        }
    }

    /**
     * Get the list of active themes in priority order
     */
    fun getActiveThemes(): List<IconTheme> = state.activeThemes.mapNotNull { IconTheme.fromDisplayName(it) }

    /**
     * Set the active themes list
     */
    fun setActiveThemes(themes: List<IconTheme>) {
        state.activeThemes = themes.map { it.displayName }.toMutableList()
    }

    /**
     * Get themes that are not currently active (available for adding)
     */
    fun getAvailableThemes(): List<IconTheme> {
        val activeSet = state.activeThemes.toSet()
        return IconTheme.entries.filter { it.displayName !in activeSet }
    }

    /**
     * Add a theme to the active list
     */
    fun addTheme(theme: IconTheme) {
        if (theme.displayName !in state.activeThemes) {
            state.activeThemes.add(theme.displayName)
        }
    }

    /**
     * Remove a theme from the active list
     */
    fun removeTheme(theme: IconTheme) {
        state.activeThemes.remove(theme.displayName)
    }

    /**
     * Move a theme up in priority (lower index = higher priority)
     */
    fun moveThemeUp(theme: IconTheme): Boolean {
        val index = state.activeThemes.indexOf(theme.displayName)
        if (index > 0) {
            val temp = state.activeThemes[index - 1]
            state.activeThemes[index - 1] = state.activeThemes[index]
            state.activeThemes[index] = temp
            return true
        }
        return false
    }

    /**
     * Move a theme down in priority
     */
    fun moveThemeDown(theme: IconTheme): Boolean {
        val index = state.activeThemes.indexOf(theme.displayName)
        if (index >= 0 && index < state.activeThemes.size - 1) {
            val temp = state.activeThemes[index + 1]
            state.activeThemes[index + 1] = state.activeThemes[index]
            state.activeThemes[index] = temp
            return true
        }
        return false
    }

    /**
     * Check if the plugin is enabled
     */
    fun isEnabled(): Boolean = state.isEnabled

    /**
     * Enable or disable the plugin
     */
    fun setEnabled(enabled: Boolean) { state.isEnabled = enabled }

    /**
     * Get the current UI language code
     */
    fun getLanguage(): String = state.language

    /**
     * Set the UI language code
     */
    fun setLanguage(languageCode: String) { state.language = languageCode }

    // Compatibility methods for legacy code
    @Deprecated("Use getActiveThemes() instead", ReplaceWith("getActiveThemes().firstOrNull() ?: IconTheme.VSCODE_ICONS"))
    fun getSelectedTheme(): IconTheme = getActiveThemes().firstOrNull() ?: IconTheme.VSCODE_ICONS

    @Deprecated("Use setActiveThemes() instead", ReplaceWith("setActiveThemes(listOf(theme))"))
    fun setSelectedTheme(theme: IconTheme) { setActiveThemes(listOf(theme)) }

    companion object {
        fun getInstance(): IconThemeConfig = com.intellij.openapi.components.service()
    }
}
