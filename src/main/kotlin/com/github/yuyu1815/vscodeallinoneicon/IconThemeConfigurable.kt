package com.github.yuyu1815.vscodeallinoneicon

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

/**
 * Configuration UI for icon theme settings
 * Supports multiple languages
 */
class IconThemeConfigurable : Configurable {
    private var mainPanel: JPanel? = null
    private var enabledCheckBox: JBCheckBox? = null
    private var languageComboBox: ComboBox<String>? = null
    private var activeListModel: DefaultListModel<String>? = null
    private var availableListModel: DefaultListModel<String>? = null
    private var activeList: JBList<String>? = null
    private var availableList: JBList<String>? = null

    // UI components that need to be updated when language changes
    private var activeTitlePanel: JPanel? = null
    private var availableTitlePanel: JPanel? = null
    private var upButton: JButton? = null
    private var downButton: JButton? = null
    private var addButton: JButton? = null
    private var removeButton: JButton? = null
    private var infoPanel: JPanel? = null

    private var currentLang: String = "en"

    override fun getDisplayName(): String = Messages.get("settings.title", currentLang)

    override fun createComponent(): JComponent? {
        if (mainPanel != null) return mainPanel

        // Get current language from config
        currentLang = IconThemeConfig.getInstance().getLanguage()

        mainPanel = JPanel(BorderLayout(10, 10))
        mainPanel?.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.Y_AXIS)

        // Language selector
        val langPanel = JPanel(BorderLayout(5, 0))
        val langLabel = JLabel(Messages.get("settings.language", currentLang))
        langPanel.add(langLabel, BorderLayout.WEST)

        languageComboBox = ComboBox()
        Language.entries.forEach { lang ->
            languageComboBox?.addItem(lang.displayName)
        }
        languageComboBox?.selectedItem = Language.fromCode(currentLang)?.displayName ?: Language.ENGLISH.displayName
        languageComboBox?.addActionListener {
            val selectedName = languageComboBox?.selectedItem as? String
            val selectedLang = Language.entries.find { it.displayName == selectedName }
            selectedLang?.let {
                currentLang = it.code
                updateUILanguage()
            }
        }
        langPanel.add(languageComboBox, BorderLayout.CENTER)
        langPanel.maximumSize = Dimension(Int.MAX_VALUE, langPanel.preferredSize.height)
        topPanel.add(langPanel)
        topPanel.add(Box.createVerticalStrut(10))

        // Enable/disable checkbox
        enabledCheckBox = JBCheckBox(Messages.get("settings.enable", currentLang))
        enabledCheckBox?.isSelected = true
        enabledCheckBox?.alignmentX = JComponent.LEFT_ALIGNMENT
        topPanel.add(enabledCheckBox)

        mainPanel?.add(topPanel, BorderLayout.NORTH)

        // Main content with two lists
        val contentPanel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()

        // Active themes panel (left)
        val activePanel = createActiveThemesPanel()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 1.0
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = Insets(5, 0, 5, 5)
        contentPanel.add(activePanel, gbc)

        // Transfer buttons (center)
        val transferPanel = createTransferButtonsPanel()
        gbc.gridx = 1
        gbc.weightx = 0.0
        gbc.fill = GridBagConstraints.VERTICAL
        gbc.insets = Insets(5, 5, 5, 5)
        contentPanel.add(transferPanel, gbc)

        // Available themes panel (right)
        val availablePanel = createAvailableThemesPanel()
        gbc.gridx = 2
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = Insets(5, 5, 5, 0)
        contentPanel.add(availablePanel, gbc)

        mainPanel?.add(contentPanel, BorderLayout.CENTER)

        // Info panel at bottom
        infoPanel = createInfoPanel()
        mainPanel?.add(infoPanel, BorderLayout.SOUTH)

        return mainPanel
    }

    private fun createActiveThemesPanel(): JPanel {
        val panel = JPanel(BorderLayout(5, 5))
        activeTitlePanel = panel
        panel.border = BorderFactory.createTitledBorder(Messages.get("settings.activeThemes", currentLang))

        activeListModel = DefaultListModel()
        activeList = JBList(activeListModel!!)
        activeList?.selectionMode = ListSelectionModel.SINGLE_SELECTION
        activeList?.visibleRowCount = 5

        val scrollPane = JBScrollPane(activeList)
        scrollPane.preferredSize = Dimension(200, 150)
        panel.add(scrollPane, BorderLayout.CENTER)

        // Reorder buttons
        val reorderPanel = JPanel()
        reorderPanel.layout = BoxLayout(reorderPanel, BoxLayout.Y_AXIS)

        upButton = JButton(Messages.get("settings.up", currentLang))
        upButton?.addActionListener { moveSelectedThemeUp() }
        reorderPanel.add(upButton)

        reorderPanel.add(Box.createVerticalStrut(5))

        downButton = JButton(Messages.get("settings.down", currentLang))
        downButton?.addActionListener { moveSelectedThemeDown() }
        reorderPanel.add(downButton)

        panel.add(reorderPanel, BorderLayout.EAST)

        return panel
    }

    private fun createAvailableThemesPanel(): JPanel {
        val panel = JPanel(BorderLayout(5, 5))
        availableTitlePanel = panel
        panel.border = BorderFactory.createTitledBorder(Messages.get("settings.availableThemes", currentLang))

        availableListModel = DefaultListModel()
        availableList = JBList(availableListModel!!)
        availableList?.selectionMode = ListSelectionModel.SINGLE_SELECTION
        availableList?.visibleRowCount = 5

        val scrollPane = JBScrollPane(availableList)
        scrollPane.preferredSize = Dimension(200, 150)
        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }

    private fun createTransferButtonsPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        panel.add(Box.createVerticalGlue())

        addButton = JButton(Messages.get("settings.add", currentLang))
        addButton?.addActionListener { addSelectedTheme() }
        panel.add(addButton)

        panel.add(Box.createVerticalStrut(10))

        removeButton = JButton(Messages.get("settings.remove", currentLang))
        removeButton?.addActionListener { removeSelectedTheme() }
        panel.add(removeButton)

        panel.add(Box.createVerticalGlue())

        return panel
    }

    private fun createInfoPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createTitledBorder(Messages.get("settings.howItWorks", currentLang))

        val infoLabel = JLabel("<html><p style='width: 400px;'>" +
                "<b>${Messages.get("settings.priority", currentLang)}</b><br>" +
                Messages.get("settings.tip", currentLang) +
                "</p></html>")
        panel.add(infoLabel)
        panel.add(Box.createVerticalStrut(5))

        IconTheme.entries.forEach { theme ->
            val themeLabel = JLabel("• ${theme.displayName}: ${theme.iconCount} ${Messages.get("settings.icons", currentLang)}")
            panel.add(themeLabel)
        }

        return panel
    }

    private fun updateUILanguage() {
        // Update all UI text elements
        enabledCheckBox?.text = Messages.get("settings.enable", currentLang)
        upButton?.text = Messages.get("settings.up", currentLang)
        downButton?.text = Messages.get("settings.down", currentLang)
        addButton?.text = Messages.get("settings.add", currentLang)
        removeButton?.text = Messages.get("settings.remove", currentLang)

        activeTitlePanel?.border = BorderFactory.createTitledBorder(Messages.get("settings.activeThemes", currentLang))
        availableTitlePanel?.border = BorderFactory.createTitledBorder(Messages.get("settings.availableThemes", currentLang))
        infoPanel?.border = BorderFactory.createTitledBorder(Messages.get("settings.howItWorks", currentLang))

        // Recreate info panel content
        infoPanel?.removeAll()
        val infoLabel = JLabel("<html><p style='width: 400px;'>" +
                "<b>${Messages.get("settings.priority", currentLang)}</b><br>" +
                Messages.get("settings.tip", currentLang) +
                "</p></html>")
        infoPanel?.add(infoLabel)
        infoPanel?.add(Box.createVerticalStrut(5))

        IconTheme.entries.forEach { theme ->
            val themeLabel = JLabel("• ${theme.displayName}: ${theme.iconCount} ${Messages.get("settings.icons", currentLang)}")
            infoPanel?.add(themeLabel)
        }

        mainPanel?.revalidate()
        mainPanel?.repaint()
    }

    private fun moveSelectedThemeUp() {
        val selectedIndex = activeList?.selectedIndex ?: return
        if (selectedIndex > 0) {
            val model = activeListModel ?: return
            val item = model.getElementAt(selectedIndex)
            model.removeElementAt(selectedIndex)
            model.insertElementAt(item, selectedIndex - 1)
            activeList?.selectedIndex = selectedIndex - 1
        }
    }

    private fun moveSelectedThemeDown() {
        val selectedIndex = activeList?.selectedIndex ?: return
        val model = activeListModel ?: return
        if (selectedIndex >= 0 && selectedIndex < model.size - 1) {
            val item = model.getElementAt(selectedIndex)
            model.removeElementAt(selectedIndex)
            model.insertElementAt(item, selectedIndex + 1)
            activeList?.selectedIndex = selectedIndex + 1
        }
    }

    private fun addSelectedTheme() {
        val selectedValue = availableList?.selectedValue ?: return
        activeListModel?.addElement(selectedValue)
        availableListModel?.removeElement(selectedValue)
    }

    private fun removeSelectedTheme() {
        val selectedValue = activeList?.selectedValue ?: return
        availableListModel?.addElement(selectedValue)
        activeListModel?.removeElement(selectedValue)
    }

    override fun isModified(): Boolean {
        val config = IconThemeConfig.getInstance()

        if (enabledCheckBox?.isSelected != config.isEnabled()) return true
        if (currentLang != config.getLanguage()) return true

        val currentActive = config.getActiveThemes().map { it.displayName }
        val uiActive = (0 until (activeListModel?.size ?: 0)).map { activeListModel?.getElementAt(it) }

        return currentActive != uiActive
    }

    override fun apply() {
        val config = IconThemeConfig.getInstance()

        config.setEnabled(enabledCheckBox?.isSelected ?: true)
        config.setLanguage(currentLang)

        val activeThemes = (0 until (activeListModel?.size ?: 0))
            .mapNotNull { activeListModel?.getElementAt(it) }
            .mapNotNull { IconTheme.fromDisplayName(it) }

        config.setActiveThemes(activeThemes)
    }

    override fun reset() {
        val config = IconThemeConfig.getInstance()

        enabledCheckBox?.isSelected = config.isEnabled()
        currentLang = config.getLanguage()
        languageComboBox?.selectedItem = Language.fromCode(currentLang)?.displayName ?: Language.ENGLISH.displayName

        // Populate active themes list
        activeListModel?.clear()
        config.getActiveThemes().forEach { theme ->
            activeListModel?.addElement(theme.displayName)
        }

        // Populate available themes list
        availableListModel?.clear()
        config.getAvailableThemes().forEach { theme ->
            availableListModel?.addElement(theme.displayName)
        }

        updateUILanguage()
    }

    override fun disposeUIResources() {
        mainPanel = null
        enabledCheckBox = null
        languageComboBox = null
        activeListModel = null
        availableListModel = null
        activeList = null
        availableList = null
        activeTitlePanel = null
        availableTitlePanel = null
        upButton = null
        downButton = null
        addButton = null
        removeButton = null
        infoPanel = null
    }
}
