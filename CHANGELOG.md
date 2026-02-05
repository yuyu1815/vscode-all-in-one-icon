<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# vscode-all-in-one-icon Changelog

## [Unreleased]

### Added

- **Theme Priority System**
  - Add, remove, and reorder multiple themes
  - Fallback resolution: if no icon found in first theme, next theme is tried
  - Settings UI with dual-list interface for theme management
- **Multi-Language Support**
  - English, Japanese (日本語), Chinese (简体中文), Korean (한국어)
  - Language selection in settings UI
  - Localized messages stored in JSON files for easy contribution
- Initial release of vscode-all-in-one-icon plugin
- **Multiple Icon Themes**
  - VSCode Icons theme based on [vscode-icons/vscode-icons](https://github.com/vscode-icons/vscode-icons)
  - Material Icons theme based on [PKief/vscode-material-icon-theme](https://github.com/PKief/vscode-material-icon-theme)
  - File Icons theme based on [file-icons/vscode](https://github.com/file-icons/vscode)
- **Comprehensive Icon Support**
  - File icon mapping for 100+ file extensions
  - Special icons for popular frameworks (React, Vue, Angular, Svelte, etc.)
  - Config file icons (JSON, YAML, TOML, INI, ENV, etc.)
  - Folder icon mapping for 150+ common directories
  - Theme-specific folder icons with correct prefixes
- **Project-Level Configuration**
  - Settings UI at Settings > Tools > Icon Theme Settings
  - Enable/disable custom file icons per project
  - Instant theme switching without IDE restart
  - Persistent settings stored in JSON format (`vscode-all-in-one-icon-settings.json`)
- **Icon Features**
  - Automatic icon scaling for high-resolution displays
  - Smart file name matching (e.g., `package.json`, `Dockerfile`)
  - Complex extension support (e.g., `test.spec.ts` → TypeScript test icon)
  - Logging for missing icons to aid debugging

### Fixed

- Material Icons folder icon prefix issue
  - VSCode Icons uses `folder_type_` prefix
  - Material Icons uses `folder-` prefix
  - Each theme now correctly applies its own prefix convention

### Technical Implementation

- **Core Components**
  - `VscordIconProvider`: Main icon provider implementing `FileIconProvider`
    - Resolves icons based on current theme and file/folder name
    - Loads SVG icons from resources with proper scaling
    - Returns null when plugin is disabled (uses IDE defaults)
  - `IconResolver`: Theme-specific icon name mapping
    - Manages current theme state
    - Resolves file icons by extension and filename
    - Resolves folder icons by folder name
    - Supports both VSCode Icons and Material Icons mappings
  - `IconThemeConfig`: Persistent configuration storage
    - Stores settings in JSON format using `PersistentStateComponent`
    - Project-level service (saved per project)
    - Tracks selected theme and enabled state
  - `IconThemeConfigurable`: Settings UI implementation
    - Swing-based configuration panel
    - Theme selection combo box with descriptions
    - Enable/disable checkbox
    - Icon count information display

- **Icon Mapping System**
  - `Mappings.kt`: Centralized icon mappings loaded from JSON
    - VSCode Icons: extensions, filenames, folders
    - Material Icons: extensions, filenames, folders
    - Lazy-loaded JSON resources from `/settings/` directory
    - Flattened mapping format (icon → [extensions])

- **Resource Structure**
  - `/icons/vscode/`: VSCode Icons SVG files
    - File icons: `file_type_*.svg`
    - Folder icons: `folder_type_*.svg`
  - `/icons/material/`: Material Icons SVG files
    - File icons: `file_type_*.svg`
    - Folder icons: `folder-*.svg`
  - `/settings/vscord/`: VSCode Icons mapping JSON files
  - `/settings/material/`: Material Icons mapping JSON files

- **Test Infrastructure**
  - `converter/regenerate_all_test_files.js`: Main test generation script
  - `converter/generate_vscode_test_files.js`: VSCode Icons test generator
  - `converter/generate_material_test_files.js`: Material Icons test generator
  - `converter/generate_icon_test_snapshot.js`: Test structure snapshot generator
  - `/icon_test/vscode/`: Generated VSCode Icons test files (150 files/folders)
  - `/icon_test/material/`: Generated Material Icons test files (150 files/folders)
  - `/icon_test/snapshot.json`: Current test file structure snapshot

- **Icon Sources**
  - VSCode Icons: [vscode-icons/vscode-icons repository](https://github.com/vscode-icons/vscode-icons)
  - Material Icons: [PKief/vscode-material-icon-theme repository](https://github.com/PKief/vscode-material-icon-theme)
    - Included in `/converter/vscode-material-icon-theme` for reference
  - File Icons: [file-icons/vscode repository](https://github.com/file-icons/vscode)
    - Included in `/converter/vscode` for reference

### Code Quality

- All code comments in English
- Meaningful comments explaining implementation details
- Consistent naming conventions
- Proper error handling with logging

### Documentation

- Comprehensive README.md with installation, configuration, and development instructions
- Detailed LICENSE file with proper attributions to:
  - vscode-icons project (CC BY-SA 4.0)
  - vscode-material-icon-theme project (MIT)
  - file-icons project (MIT)
  - Gson library (Apache 2.0)
- CHANGELOG.md following [Keep a Changelog](https://keepachangelog.com/) format
