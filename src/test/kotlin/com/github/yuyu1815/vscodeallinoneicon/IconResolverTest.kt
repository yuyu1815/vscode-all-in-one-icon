package com.github.yuyu1815.vscodeallinoneicon

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class IconResolverTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        // Reset to default theme before each test to ensure isolation
        IconResolver.setThemes(listOf(IconTheme.VSCODE_ICONS))
    }

    // --- VSCode Theme Tests ---

    fun testResolveExactFilenameMatchInVSCodeTheme() {
        // Arrange
        val fileName = "package.json"
        
        // Act
        val result = IconResolver.resolveIconName(fileName, false)

        // Assert
        assertNotNull("Should resolve package.json", result)
        assertEquals(IconTheme.VSCODE_ICONS, result?.theme)
    }

    fun testResolveExactExtensionMatchInVSCodeTheme() {
        // Arrange
        val fileName = "main.ts"
        
        // Act
        val result = IconResolver.resolveIconName(fileName, false)

        // Assert
        assertTrue("Should contain typescript", result?.iconName?.contains("typescript") == true)
        assertEquals(IconTheme.VSCODE_ICONS, result?.theme)
    }

    fun testResolveLongestExtensionMatchInVSCodeTheme() {
        // Arrange
        val fileName = "types.d.ts"
        
        // Act
        val result = IconResolver.resolveIconName(fileName, false)

        // Assert
        assertTrue("Should contain typescriptdef", result?.iconName?.contains("typescriptdef") == true)
    }

    fun testHandleCaseInsensitivityInVSCodeTheme() {
        // Arrange
        val fileName = "PACKAGE.JSON"
        
        // Act
        val result = IconResolver.resolveIconName(fileName, false)

        // Assert
        assertNotNull("Should handle uppercase input", result)
    }

    fun testResolveFolderIconInVSCodeTheme() {
        // Arrange
        val folderName = "src"
        val isDirectory = true
        
        // Act
        val result = IconResolver.resolveIconName(folderName, isDirectory)

        // Assert
        assertEquals("src", result?.iconName)
        assertEquals(IconTheme.VSCODE_ICONS, result?.theme)
    }

    fun testResolveBuildFolderIconInVSCodeTheme() {
        // Arrange
        val folderName = "build"
        val isDirectory = true
        
        // Act
        val result = IconResolver.resolveIconName(folderName, isDirectory)

        // Assert
        assertEquals("dist", result?.iconName)
        assertEquals(IconTheme.VSCODE_ICONS, result?.theme)
    }

    // --- Material Theme Tests ---

    fun testResolveFileIconInMaterialTheme() {
        // Arrange
        IconResolver.setThemes(listOf(IconTheme.MATERIAL_ICONS))
        val fileName = "index.html"
        
        // Act
        val result = IconResolver.resolveIconName(fileName, false)

        // Assert
        if (result != null) {
            assertEquals(IconTheme.MATERIAL_ICONS, result.theme)
        }
    }

    fun testResolveFolderIconInMaterialTheme() {
        // Arrange
        IconResolver.setThemes(listOf(IconTheme.MATERIAL_ICONS))
        val folderName = "test"
        val isDirectory = true
        
        // Act
        val result = IconResolver.resolveIconName(folderName, isDirectory)

        // Assert
        assertEquals("folder-test", result?.iconName)
        assertEquals(IconTheme.MATERIAL_ICONS, result?.theme)
    }

    // --- File Icons Theme Tests ---

    fun testResolveFileIconInFileIconsTheme() {
        // Arrange
        IconResolver.setThemes(listOf(IconTheme.FILE_ICONS))
        val fileName = "script.js"
        
        // Act
        val result = IconResolver.resolveIconName(fileName, false)

        // Assert
        if (result != null) {
            assertEquals(IconTheme.FILE_ICONS, result.theme)
        }
    }

    // --- Fallback Tests ---

    fun testShouldFallbackToSecondTheme() {
        // Arrange - Use a unique folder that only exists in Material theme
        IconResolver.setThemes(listOf(IconTheme.VSCODE_ICONS, IconTheme.MATERIAL_ICONS))
        val folderName = "test" // This exists in Material theme
        
        // Act
        val result = IconResolver.resolveIconName(folderName, true)

        // Assert
        assertNotNull("Should find icon via fallback", result)
    }

    fun testShouldUseFirstThemeIfIconExists() {
        // Arrange - "src" folder exists in VSCode theme
        IconResolver.setThemes(listOf(IconTheme.VSCODE_ICONS, IconTheme.MATERIAL_ICONS))
        val folderName = "src"
        
        // Act
        val result = IconResolver.resolveIconName(folderName, true)

        // Assert
        assertNotNull(result)
        assertEquals("Should use first theme", IconTheme.VSCODE_ICONS, result?.theme)
    }

    fun testShouldReturnNullIfNoThemeHasIcon() {
        // Arrange
        IconResolver.setThemes(listOf(IconTheme.VSCODE_ICONS))
        val fileName = "very_unlikely_file_extension_12345.xyz789abc"
        
        // Act
        val result = IconResolver.resolveIconName(fileName, false)

        // Assert
        assertNull("Should return null for unknown file", result)
    }

    // --- Glob Pattern Matching Tests ---

    fun testGlobPatternExactMatch() {
        // Arrange
        val pattern = "src/components"
        val path = "src/components"

        // Act
        val result = IconResolver.matchGlobPattern(pattern, path)

        // Assert
        assertTrue("Should match exact path", result)
    }

    fun testGlobPatternSingleWildcard() {
        // Arrange
        val pattern = "src/*/config"
        val path = "src/main/config"

        // Act
        val result = IconResolver.matchGlobPattern(pattern, path)

        // Assert
        assertTrue("Should match single wildcard", result)
    }

    fun testGlobPatternDoubleWildcard() {
        // Arrange
        val pattern = "test/**/utils"
        val path = "test/unit/helpers/utils"

        // Act
        val result = IconResolver.matchGlobPattern(pattern, path)

        // Assert
        assertTrue("Should match multi-segment wildcard", result)
    }

    fun testGlobPatternDoubleWildcardAtEnd() {
        // Arrange
        val pattern = "src/**"
        val path = "src/main/java/com/example"

        // Act
        val result = IconResolver.matchGlobPattern(pattern, path)

        // Assert
        assertTrue("Should match ** at end", result)
    }

    fun testGlobPatternWithSuffixWildcard() {
        // Arrange
        val pattern = "cache/*_tmp"
        val path = "cache/build_tmp"

        // Act
        val result = IconResolver.matchGlobPattern(pattern, path)

        // Assert
        assertTrue("Should match suffix wildcard", result)
    }

    fun testGlobPatternNoMatch() {
        // Arrange
        val pattern = "src/components"
        val path = "test/components"

        // Act
        val result = IconResolver.matchGlobPattern(pattern, path)

        // Assert
        assertFalse("Should not match different path", result)
    }
}
