package com.github.yuyu1815.vscodeallinoneicon

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Utility functions shared across the plugin
 */
object Utils {
    /**
     * Get the relative path of a file from the project base path.
     * Returns null if the file is not under the project root or base path is null.
     */
    fun getRelativePath(file: VirtualFile, project: Project): String? {
        val basePath = project.basePath ?: return null
        return getRelativePath(file, basePath)
    }

    /**
     * Get the relative path of a file from a base path.
     */
    fun getRelativePath(file: VirtualFile, basePath: String?): String? {
        if (basePath == null) return null
        val filePath = file.path
        return if (filePath.startsWith(basePath)) {
            filePath.removePrefix(basePath).removePrefix("/").removePrefix("\\")
        } else {
            null
        }
    }
}
