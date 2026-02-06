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
     * Resolve icon name with context-aware rules first, then fallback to theme-based resolution.
     * @param name The file or folder name
     * @param isDirectory Whether this is a directory
     * @param relativePath Optional relative path from project root (e.g., "src/main/components")
     */
    fun resolveIconName(name: String, isDirectory: Boolean, relativePath: String? = null): IconResolutionResult? {
        // 1. Try context-aware rules first (if path is provided)
        if (!relativePath.isNullOrBlank()) {
            resolveFromContextRules(relativePath)?.let { return it }
        }

        // 2. Fallback to standard theme-based resolution
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

    /**
     * Try to match path against context rules
     */
    private fun resolveFromContextRules(path: String): IconResolutionResult? {
        val normalizedPath = path.replace("\\", "/").lowercase()
        
        for (rule in Mappings.contextRules) {
            if (matchGlobPattern(rule.pattern.lowercase(), normalizedPath)) {
                return IconResolutionResult(rule.icon, rule.getIconTheme())
            }
        }
        return null
    }

    /**
     * Match a path against a glob pattern.
     * Supports:
     * - `*` matches any single path segment (excluding /)
     * - `**` matches any number of path segments (including zero)
     * - Literal text matches exactly
     */
    internal fun matchGlobPattern(pattern: String, path: String): Boolean {
        val patternParts = pattern.split("/")
        val pathParts = path.split("/").filter { it.isNotEmpty() }
        
        return matchParts(patternParts, 0, pathParts, 0)
    }

    private fun matchParts(
        patternParts: List<String>, 
        patternIndex: Int, 
        pathParts: List<String>, 
        pathIndex: Int
    ): Boolean {
        var pi = patternIndex
        var ti = pathIndex

        while (pi < patternParts.size && ti < pathParts.size) {
            val pattern = patternParts[pi]
            val target = pathParts[ti]

            when {
                pattern == "**" -> {
                    // ** can match zero or more segments
                    // Try matching remaining pattern with current and all subsequent positions
                    
                    // If ** is the last part, it matches everything
                    if (pi + 1 == patternParts.size) {
                        return true
                    }
                    
                    // Try matching from current position onwards using recursion with indices
                    // We need to match the REST of the pattern against any suffix of pathParts
                    for (i in ti..pathParts.size) {
                        if (matchParts(patternParts, pi + 1, pathParts, i)) {
                            return true
                        }
                    }
                    return false
                }
                matchSegment(pattern, target) -> {
                    pi++
                    ti++
                }
                else -> return false
            }
        }

        // Handle trailing ** in pattern
        while (pi < patternParts.size && patternParts[pi] == "**") {
            pi++
        }

        return pi == patternParts.size && ti == pathParts.size
    }

    /**
     * Match a single path segment against a pattern segment.
     * Supports * as wildcard within segment (e.g., *_tmp matches build_tmp)
     */
    private fun matchSegment(pattern: String, target: String): Boolean {
        if (!pattern.contains("*")) {
            return pattern == target
        }

        if (pattern == "*") return true

        val starCount = pattern.count { it == '*' }
        
        // Simple optimization for common patterns
        if (starCount == 1) {
            if (pattern.startsWith("*")) {
                val suffix = pattern.substring(1)
                return target.endsWith(suffix)
            }
            if (pattern.endsWith("*")) {
                val prefix = pattern.substring(0, pattern.length - 1)
                return target.startsWith(prefix)
            }
            // pattern is like "pre*suf"
            val parts = pattern.split("*")
            if (parts.size == 2) {
                val prefix = parts[0]
                val suffix = parts[1]
                return target.startsWith(prefix) && 
                       target.endsWith(suffix) && 
                       target.length >= prefix.length + suffix.length
            }
        }

        // Fallback to regex for complex patterns (e.g. *foo*bar)
        // Convert glob pattern to regex safely by escaping non-wildcard chars
        val regex = Regex.escape(pattern).replace("\\*", ".*")
        
        return Regex("^$regex$").matches(target)
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
