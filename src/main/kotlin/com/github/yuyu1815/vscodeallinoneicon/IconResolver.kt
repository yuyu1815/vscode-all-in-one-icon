package com.github.yuyu1815.vscodeallinoneicon

import com.github.yuyu1815.vscodeallinoneicon.IconTheme.FILE_ICONS
import com.github.yuyu1815.vscodeallinoneicon.IconTheme.MATERIAL_ICONS
import com.github.yuyu1815.vscodeallinoneicon.IconTheme.VSCODE_ICONS

/**
 * Result of icon resolution containing both the icon name and which theme it came from
 */
data class IconResolutionResult(
    val iconName: String,
    val theme: IconTheme
)

object IconResolver {
    private var themes: List<IconTheme> = listOf(VSCODE_ICONS)

    /**
     * Set the list of themes in priority order
     */
    fun setThemes(themes: List<IconTheme>) {
        this.themes = themes.ifEmpty { listOf(VSCODE_ICONS) }
    }

    /**
     * Get the current theme list
     */
    fun getThemes(): List<IconTheme> = themes

    /**
     * Resolve icon name with fallback across multiple themes
     * Returns null if no theme has an icon for this file
     */
    fun resolveIconName(name: String, isDirectory: Boolean): IconResolutionResult? {
        val lowercaseName = name.lowercase()
        for (theme in themes) {
            val iconName = if (isDirectory) {
                resolveFolderIcon(lowercaseName, theme)
            } else {
                resolveFileIcon(lowercaseName, theme)
            }
            if (iconName != null) {
                return IconResolutionResult(iconName, theme)
            }
        }
        return null
    }

    private fun resolveFolderIcon(name: String, theme: IconTheme): String? = when (theme) {
        VSCODE_ICONS -> Mappings.vscordFolderNameMap[name]
        MATERIAL_ICONS -> Mappings.materialFolderNameMap[name]
        FILE_ICONS -> Mappings.fileIconsFolderNameMap[name]
    }

    private fun resolveFileIcon(name: String, theme: IconTheme): String? = when (theme) {
        VSCODE_ICONS -> resolveVSCodeFileIcon(name)
        MATERIAL_ICONS -> resolveMaterialFileIcon(name)
        FILE_ICONS -> resolveFileIconsFileIcon(name)
    }

    private fun resolveVSCodeFileIcon(name: String): String? {
        // 1. Exact filename match
        Mappings.vscordFileNameMap[name]?.let { return it }

        // 2. Extension match (complex to simple)
        // e.g. "test.spec.ts" -> check "spec.ts" -> check "ts"
        var current = name
        while (current.contains(".")) {
            current = current.substringAfter(".")
            Mappings.vscordFileExtensionMap[current]?.let { return it }
        }
        return null
    }

    private fun resolveMaterialFileIcon(name: String): String? {
        // 1. Exact filename match
        Mappings.materialFileNameMap[name]?.let { return it }

        // 2. Extension match (complex to simple)
        var current = name
        while (current.contains(".")) {
            current = current.substringAfter(".")
            Mappings.materialFileExtensionMap[current]?.let { return it }
        }
        return null
    }

    private fun resolveFileIconsFileIcon(name: String): String? {
        // 1. Exact filename match
        Mappings.fileIconsFileNameMap[name]?.let { return it }

        // 2. Extension match (complex to simple)
        var current = name
        while (current.contains(".")) {
            current = current.substringAfter(".")
            Mappings.fileIconsFileExtensionMap[current]?.let { return it }
        }
        return null
    }
}
