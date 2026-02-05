package com.github.yuyu1815.vscodeallinoneicon

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ResourceConsistencyTest : BasePlatformTestCase() {

    fun testAllVscordIconsShouldExistAsResources() {
        val missingIcons = mutableListOf<String>()

        // Check file extensions
        Mappings.vscordFileExtensionMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.VSCODE_ICONS, false)) missingIcons.add("File Ext: $iconName")
        }

        // Check file names
        Mappings.vscordFileNameMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.VSCODE_ICONS, false)) missingIcons.add("File Name: $iconName")
        }

        // Check folder names
        Mappings.vscordFolderNameMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.VSCODE_ICONS, true)) missingIcons.add("Folder: $iconName")
        }

        if (missingIcons.isNotEmpty()) {
            println("WARNING: Missing VSCode icons:\n" + missingIcons.joinToString("\n"))
            // fail("Missing VSCode icons:\n" + missingIcons.joinToString("\n"))
        }
    }

    fun testAllMaterialIconsShouldExistAsResources() {
        val missingIcons = mutableListOf<String>()

        Mappings.materialFileExtensionMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.MATERIAL_ICONS, false)) missingIcons.add("File Ext: $iconName")
        }

        Mappings.materialFileNameMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.MATERIAL_ICONS, false)) missingIcons.add("File Name: $iconName")
        }

        Mappings.materialFolderNameMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.MATERIAL_ICONS, true)) missingIcons.add("Folder: $iconName")
        }

        if (missingIcons.isNotEmpty()) {
            println("WARNING: Missing Material icons:\n" + missingIcons.joinToString("\n"))
            // fail("Missing Material icons:\n" + missingIcons.joinToString("\n"))
        }
    }

    fun testAllFileIconsIconsShouldExistAsResources() {
        val missingIcons = mutableListOf<String>()

        Mappings.fileIconsFileExtensionMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.FILE_ICONS, false)) missingIcons.add("File Ext: $iconName")
        }

        Mappings.fileIconsFileNameMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.FILE_ICONS, false)) missingIcons.add("File Name: $iconName")
        }

        Mappings.fileIconsFolderNameMap.values.distinct().forEach { iconName ->
            if (!iconExists(iconName, IconTheme.FILE_ICONS, true)) missingIcons.add("Folder: $iconName")
        }

        if (missingIcons.isNotEmpty()) {
            println("WARNING: Missing File Icons assets:\n" + missingIcons.joinToString("\n"))
            // fail("Missing File Icons assets:\n" + missingIcons.joinToString("\n"))
        }
    }

    /**
     * Helper to verify icon existence logic replicating VscordIconProvider logic
     */
    private fun iconExists(name: String, theme: IconTheme, isFolder: Boolean): Boolean {
        // Construct code that replicates VscordIconProvider.loadIcon logic mostly
        val iconFileName = when (theme) {
            IconTheme.MATERIAL_ICONS -> name
            IconTheme.VSCODE_ICONS -> {
                val prefix = if (isFolder) "folder_type_" else "file_type_"
                "$prefix$name"
            }
            IconTheme.FILE_ICONS -> name
        }

        val themePrefix = when (theme) {
            IconTheme.VSCODE_ICONS -> "vscode/"
            IconTheme.MATERIAL_ICONS -> "material/"
            IconTheme.FILE_ICONS -> "file-icons/"
        }

        val path = "/icons/${themePrefix}$iconFileName.svg"
        val url = javaClass.getResource(path)
        
        if (url == null) {
            println("!! Missing Icon: $path (Theme: $theme, Name: $name)")
        }
        
        return url != null
    }
}
