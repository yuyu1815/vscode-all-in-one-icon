package com.github.yuyu1815.vscodeallinoneicon

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object Mappings {
    // VSCode Icons Mappings
    val vscordFileExtensionMap: Map<String, String> by lazy { loadMapping("/settings/vscord/icon_extensions.json") }
    val vscordFileNameMap: Map<String, String> by lazy { loadMapping("/settings/vscord/icon_filenames.json") }
    val vscordFolderNameMap: Map<String, String> by lazy { loadMapping("/settings/vscord/icon_folders.json") }

    // Material Icons Mappings
    val materialFileExtensionMap: Map<String, String> by lazy { loadMapping("/settings/material/icon_extensions.json") }
    val materialFileNameMap: Map<String, String> by lazy { loadMapping("/settings/material/icon_filenames.json") }
    val materialFolderNameMap: Map<String, String> by lazy { loadMapping("/settings/material/icon_folders.json") }

    // File Icons Mappings
    val fileIconsFileExtensionMap: Map<String, String> by lazy { loadMapping("/settings/file-icons/icon_extensions.json") }
    val fileIconsFileNameMap: Map<String, String> by lazy { loadMapping("/settings/file-icons/icon_filenames.json") }
    val fileIconsFolderNameMap: Map<String, String> by lazy { loadMapping("/settings/file-icons/icon_folders.json") }

    private fun loadMapping(resourcePath: String): Map<String, String> {
        val stream = Mappings::class.java.getResourceAsStream(resourcePath)
            ?: return emptyMap()

        val type = object : TypeToken<Map<String, List<String>>>() {}.type
        val rawMap: Map<String, List<String>> = InputStreamReader(stream).use { reader ->
            Gson().fromJson(reader, type)
        }

        // Flatten: Icon -> [ext1, ext2]  ===>  ext1 -> Icon, ext2 -> Icon
        return rawMap.flatMap { (icon, extensions) ->
            extensions.map { it to icon }
        }.toMap()
    }
}
