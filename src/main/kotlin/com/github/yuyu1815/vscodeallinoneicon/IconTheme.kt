package com.github.yuyu1815.vscodeallinoneicon

/**
 * サポートするアイコンテーマの定義
 */
enum class IconTheme(
    val displayName: String,
    val description: String,
    val folderName: String,
    val iconCount: Int
) {
    VSCODE_ICONS(
        displayName = "VSCode Icons",
        description = "VSCode Icons (1,473 icons) - 最も多くのファイル拡張子をサポート",
        folderName = "vscode-icons",
        iconCount = 1473
    ),
    MATERIAL_ICONS(
        displayName = "Material Icons",
        description = "Material Icons (1,136 icons) - シンプルでモダンなデザイン",
        folderName = "vscode-material-icon-theme",
        iconCount = 1136
    ),
    FILE_ICONS(
        displayName = "File Icons",
        description = "File Icons (2,005 icons) - フォントベースのアイコンセット",
        folderName = "file-icons",
        iconCount = 2005
    );

    companion object {
        fun fromDisplayName(displayName: String): IconTheme? = entries.find { it.displayName == displayName }
    }
}
