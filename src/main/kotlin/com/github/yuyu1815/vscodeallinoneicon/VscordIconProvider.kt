package com.github.yuyu1815.vscodeallinoneicon

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class VscordIconProvider : FileIconProvider {
    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        // Return null if plugin is disabled (uses default IDE icons)
        val config = IconThemeConfig.getInstance()
        if (!config.isEnabled()) {
            return null
        }

        // Set themes in priority order for resolution
        val activeThemes = config.getActiveThemes()
        IconResolver.setThemes(activeThemes)

        // Get relative path from project root for context-aware resolution
        val relativePath = project?.let { Utils.getRelativePath(file, it) }

        // Resolve icon name with context-aware fallback
        val result = IconResolver.resolveIconName(file.name, file.isDirectory, relativePath)

        return result?.let {
            loadIcon(it.iconName, file.isDirectory, it.theme)
        }
    }

    companion object {
        fun loadIcon(name: String, isFolder: Boolean, theme: IconTheme): Icon? {
            // Material theme mappings include the prefix (e.g., "folder-src")
            // VSCode theme mappings don't include prefix (e.g., "src")
            // File Icons theme uses font-based icons (not SVG files)
            val iconFileName = when (theme) {
                IconTheme.MATERIAL_ICONS -> name  // Use name from mapping as-is
                IconTheme.VSCODE_ICONS -> {
                    // Add prefix for VSCode theme only
                    val prefix = if (isFolder) "folder_type_" else "file_type_"
                    "$prefix$name"
                }
                IconTheme.FILE_ICONS -> name  // Use name from mapping as-is (SVG files)
            }

            val themePrefix = when (theme) {
                IconTheme.VSCODE_ICONS -> "vscode/"
                IconTheme.MATERIAL_ICONS -> "material/"
                IconTheme.FILE_ICONS -> "file-icons/"
            }
            val path = "/icons/${themePrefix}$iconFileName.svg"
            val icon = IconLoader.findIcon(path, VscordIconProvider::class.java)

            // Log if icon file is not found (helps with debugging)
            if (icon == null) {
                println("[VscordIconProvider] Icon not found: $path (isFolder=$isFolder, theme=$theme)")
            }

            // Scale icon based on theme
            // File Icons are typically smaller (14px), others are standard 16px
            val targetSize = if (theme == IconTheme.FILE_ICONS) 14 else 16
            
            return icon?.let {
                if (it.iconWidth != targetSize || it.iconHeight != targetSize) {
                    val scale = targetSize.toFloat() / maxOf(it.iconWidth, it.iconHeight)
                    com.intellij.util.IconUtil.scale(it, null, scale)
                } else {
                    it
                }
            }
        }
    }
}
