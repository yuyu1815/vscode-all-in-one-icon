package com.github.yuyu1815.vscodeallinoneicon

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.ui.ColoredTreeCellRenderer

class VscordProjectViewNodeDecorator : ProjectViewNodeDecorator {
    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        // Only process if the node represents a VirtualFile
        val file = node.virtualFile ?: return
        
        // We only care about directories for now (as files are generally handled well by FileIconProvider)
        // But ProjectViewNodeDecorator could technically handle both. 
        // Let's stick to directories as requested for "build", "dist", etc.
        if (!file.isDirectory) return

        val config = IconThemeConfig.getInstance()
        if (!config.isEnabled()) return

        // Set active themes for resolution
        val activeThemes = config.getActiveThemes()
        IconResolver.setThemes(activeThemes)

        // Get relative path from project root for context-aware resolution
        val project = node.project
        val relativePath = project?.let { getRelativePath(file, it.basePath) }

        // Resolve icon with context-aware rules
        val result = IconResolver.resolveIconName(file.name, true, relativePath)
        
        if (result != null) {
            // Force load the icon
            val icon = VscordIconProvider.loadIcon(result.iconName, true, result.theme)
            if (icon != null) {
                // Apply the icon to the PresentationData
                // This overrides the default icon (including the orange excluded folder icon)
                data.setIcon(icon)
            }
        }
    }

    private fun getRelativePath(file: VirtualFile, basePath: String?): String? {
        if (basePath == null) return null
        val filePath = file.path
        return if (filePath.startsWith(basePath)) {
            filePath.removePrefix(basePath).removePrefix("/").removePrefix("\\")
        } else {
            null
        }
    }

    @Deprecated("This method is deprecated and never called by the platform")
    @Suppress("DEPRECATION")
    override fun decorate(node: PackageDependenciesNode?, cellRenderer: ColoredTreeCellRenderer?) {
        // No-op: This method is deprecated and never called by the platform
    }
}
