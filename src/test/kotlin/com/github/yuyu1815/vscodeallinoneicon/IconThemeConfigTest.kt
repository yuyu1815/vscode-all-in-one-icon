package com.github.yuyu1815.vscodeallinoneicon

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class IconThemeConfigTest : BasePlatformTestCase() {

    private lateinit var config: IconThemeConfig

    override fun setUp() {
        super.setUp()
        config = IconThemeConfig()
    }

    fun testShouldHaveCorrectDefaultConfiguration() {
        // Arrange
        // (initialized in setUp)

        // Act & Assert
        assertEquals("Default should have one active theme", 1, config.getActiveThemes().size)
        assertEquals("Default theme should be VSCode Icons", IconTheme.VSCODE_ICONS, config.getActiveThemes().first())
        assertTrue("Plugin should be enabled by default", config.isEnabled())
    }

    fun testShouldUpdateActiveThemes() {
        // Arrange
        val themes = listOf(IconTheme.MATERIAL_ICONS, IconTheme.FILE_ICONS)

        // Act
        config.setActiveThemes(themes)

        // Assert
        assertEquals("Should have 2 active themes", 2, config.getActiveThemes().size)
        assertEquals("First theme should be Material", IconTheme.MATERIAL_ICONS, config.getActiveThemes()[0])
        assertEquals("Second theme should be File Icons", IconTheme.FILE_ICONS, config.getActiveThemes()[1])
    }

    fun testShouldReturnAvailableThemes() {
        // Arrange
        config.setActiveThemes(listOf(IconTheme.VSCODE_ICONS))

        // Act
        val available = config.getAvailableThemes()

        // Assert
        assertEquals("Should have 2 available themes", 2, available.size)
        assertTrue("Available should contain Material", available.contains(IconTheme.MATERIAL_ICONS))
        assertTrue("Available should contain File Icons", available.contains(IconTheme.FILE_ICONS))
    }

    fun testShouldAddTheme() {
        // Arrange
        config.setActiveThemes(listOf(IconTheme.VSCODE_ICONS))

        // Act
        config.addTheme(IconTheme.MATERIAL_ICONS)

        // Assert
        assertEquals("Should have 2 active themes", 2, config.getActiveThemes().size)
        assertTrue("Should contain added theme", config.getActiveThemes().contains(IconTheme.MATERIAL_ICONS))
    }

    fun testShouldRemoveTheme() {
        // Arrange
        config.setActiveThemes(listOf(IconTheme.VSCODE_ICONS, IconTheme.MATERIAL_ICONS))

        // Act
        config.removeTheme(IconTheme.MATERIAL_ICONS)

        // Assert
        assertEquals("Should have 1 active theme", 1, config.getActiveThemes().size)
        assertFalse("Should not contain removed theme", config.getActiveThemes().contains(IconTheme.MATERIAL_ICONS))
    }

    fun testShouldMoveThemeUp() {
        // Arrange
        config.setActiveThemes(listOf(IconTheme.VSCODE_ICONS, IconTheme.MATERIAL_ICONS, IconTheme.FILE_ICONS))

        // Act
        val result = config.moveThemeUp(IconTheme.MATERIAL_ICONS)

        // Assert
        assertTrue("Move should succeed", result)
        assertEquals("Material should be first", IconTheme.MATERIAL_ICONS, config.getActiveThemes()[0])
        assertEquals("VSCode should be second", IconTheme.VSCODE_ICONS, config.getActiveThemes()[1])
    }

    fun testShouldNotMoveFirstThemeUp() {
        // Arrange
        config.setActiveThemes(listOf(IconTheme.VSCODE_ICONS, IconTheme.MATERIAL_ICONS))

        // Act
        val result = config.moveThemeUp(IconTheme.VSCODE_ICONS)

        // Assert
        assertFalse("Move should fail for first item", result)
        assertEquals("Order should be unchanged", IconTheme.VSCODE_ICONS, config.getActiveThemes()[0])
    }

    fun testShouldMoveThemeDown() {
        // Arrange
        config.setActiveThemes(listOf(IconTheme.VSCODE_ICONS, IconTheme.MATERIAL_ICONS, IconTheme.FILE_ICONS))

        // Act
        val result = config.moveThemeDown(IconTheme.MATERIAL_ICONS)

        // Assert
        assertTrue("Move should succeed", result)
        assertEquals("Material should be third", IconTheme.MATERIAL_ICONS, config.getActiveThemes()[2])
        assertEquals("File Icons should be second", IconTheme.FILE_ICONS, config.getActiveThemes()[1])
    }

    fun testShouldUpdateEnabledStatus() {
        // Arrange
        val enabled = false

        // Act
        config.setEnabled(enabled)

        // Assert
        assertFalse("Enabled status should be updated", config.isEnabled())
    }

    fun testShouldPersistState() {
        // Arrange
        val newState = IconThemeConfig.State(
            activeThemes = mutableListOf(IconTheme.FILE_ICONS.displayName, IconTheme.MATERIAL_ICONS.displayName),
            isEnabled = false
        )

        // Act
        config.loadState(newState)

        // Assert
        assertEquals("Loaded state should have 2 themes", 2, config.getActiveThemes().size)
        assertEquals("First theme should be File Icons", IconTheme.FILE_ICONS, config.getActiveThemes()[0])
        assertFalse("Loaded state should reflect enabled config", config.isEnabled())
    }
}
