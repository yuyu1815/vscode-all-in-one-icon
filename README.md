# vscode-all-in-one-icon

🌐 [日本語](README.ja.md) | [简体中文](README.zh-CN.md) | [Deutsch](README.de.md)

<!-- Plugin description -->

An IntelliJ IDEA plugin that brings beautiful VS Code-style file and folder icons to your JetBrains IDE. Supports multiple icon themes including VSCode Icons, Material Icons, and File Icons.

**Features:**

- Multiple icon themes (VSCode Icons, Material Icons, File Icons)
- Theme priority system with fallback resolution
- 2,000+ file and folder icons
- Multi-language UI (English, 日本語, 简体中文, 한국어)
- Easy theme switching
<!-- Plugin description end -->

![Build](https://github.com/yuyu1815/vscode-all-in-one-icon/workflows/Build/badge.svg)

## Features

- **Multiple Icon Themes**
  - **VSCode Icons**: Based on the popular [vscode-icons/vscode-icons](https://github.com/vscode-icons/vscode-icons) project
  - **Material Icons**: Beautiful Material Design-inspired icons
  - **File Icons**: Font-based icon system with 2,000+ icons from [file-icons/vscode](https://github.com/file-icons/vscode)

- **Comprehensive Coverage**
  - Support for 100+ file extensions
  - Special icons for popular frameworks and tools
  - Custom folder icons for common directories

- **Easy Configuration**
  - Simple project-level settings
  - Enable/disable custom icons per project
  - Switch themes instantly without restart

## Marketplace

**📦 Available on JetBrains Marketplace**

[![Version](https://img.shields.io/jetbrains/plugin/v/30077)](https://plugins.jetbrains.com/plugin/30077-vscode-all-in-one-icon)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/30077-vscode-all-in-one-icon)](https://plugins.jetbrains.com/plugin/30077-vscode-all-in-one-icon)
[![Rating](https://img.shields.io/jetbrains/plugin/r/rating/30077)](https://plugins.jetbrains.com/plugin/30077-vscode-all-in-one-icon/reviews)

👉 **[Get it from JetBrains Marketplace](https://plugins.jetbrains.com/plugin/30077-vscode-all-in-one-icon)**

## Installation

### Using JetBrains Marketplace

1. Open your JetBrains IDE
2. Go to <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd>
3. Click on <kbd>Marketplace</kbd>
4. Search for "vscode-all-in-one-icon"
5. Click <kbd>Install</kbd>

### Manual Installation

1. Download the latest release from [GitHub Releases](https://github.com/yuyu1815/vscode-all-in-one-icon/releases/latest)
2. Go to <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd>
3. Click the gear icon ⚙️
4. Select <kbd>Install plugin from disk...</kbd>
5. Choose the downloaded file

## Configuration

After installing the plugin:

1. Go to <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>Icon Theme Settings</kbd>
2. **Language**: Select your preferred UI language
3. **Theme Priority**:
   - "Active Themes" shows enabled themes in priority order
   - Use ▲/▼ buttons to reorder themes
   - Use Add/Remove buttons to manage themes
   - Icons are resolved from top theme first, falling back to next if not found
4. Check/uncheck "Enable custom file icons" to toggle the plugin
5. Click <kbd>Apply</kbd> to save your changes

## Supported Icons

### File Extensions

- Programming languages: `.js`, `.ts`, `.py`, `.java`, `.kt`, `.go`, `.rs`, etc.
- Markup & Style: `.html`, `.css`, `.scss`, `.json`, `.xml`, etc.
- Frameworks: `.vue`, `.jsx`, `.tsx`, `.svelte`, etc.
- Config files: `.yaml`, `.toml`, `.ini`, `.env`, etc.

### Folders

- Common folders: `src`, `dist`, `build`, `test`, etc.
- Framework-specific: `node_modules`, `.vscode`, etc.

## Development

### Building the Plugin

```bash
./gradlew buildPlugin
```

### Running Tests

```bash
./gradlew test
```

### Updating Icons (Optional)

<details>
<summary><strong>📦 Advanced: Generate icon resources from source</strong></summary>

> **⚠️ Note:** You usually don't need to follow these steps.  
> Icon resources are already included in the project.  
> This is only necessary if you want to fetch the latest icons or modify the icon generation scripts.

If you want to generate icon resources from the latest upstream sources:

1. **Install dependencies**

   ```bash
   cd converter
   npm install
   ```

2. **Setup icon repositories**

   ```bash
   npm run setup
   ```

   This will clone the following repositories:
   - `vscode` (file-icons/vscode)
   - `vscode-material-icon-theme` (material-extensions/vscode-material-icon-theme)
   - `vscode-icons` (vscode-icons/vscode-icons)

3. **Generate icon mappings and resources**

   ```bash
   npm run generate:all
   ```

4. **Build the plugin**
   ```bash
   cd ..
   ./gradlew buildPlugin
   ```

#### Available Scripts

```bash
cd converter

# Complete setup (clone repos and generate all resources)
npm run setup
npm run generate:all

# Generate individual icon themes
npm run generate:file-icons    # Generate File Icons resources
npm run generate:material       # Generate Material Icons resources
npm run generate:vscord         # Generate VSCode Icons resources
npm run generate:snapshot       # Generate test snapshot
```

</details>

## Credits

### Icons

This plugin uses icons from the following projects:

- **[vscode-icons/vscode-icons](https://github.com/vscode-icons/vscode-icons)**
  - License: [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/)
  - Copyright © 2016 Roberto Huertas

- **[PKief/vscode-material-icon-theme](https://github.com/PKief/vscode-material-icon-theme)**
  - License: [MIT](https://opensource.org/licenses/MIT)
  - Copyright © PKief
  - Material Design Icons for Visual Studio Code (30M+ installations)

- **[file-icons/vscode](https://github.com/file-icons/vscode)**
  - License: [MIT](https://opensource.org/licenses/MIT)
  - Copyright © Daniel Brooker (2017-2019), John Gardner (2019-2023)
  - File-specific icons for improved visual grepping

### License

This plugin is licensed under the [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/).

```

You are free to:
- Share — copy and redistribute the material in any medium or format
- Adapt — remix, transform, and build upon the material

Under the following terms:
- **Attribution** — You must give appropriate credit to:
  - vscode-icons project (https://github.com/vscode-icons/vscode-icons)
  - vscode-material-icon-theme project (https://github.com/PKief/vscode-material-icon-theme)
  - file-icons project (https://github.com/file-icons/vscode)
  - This plugin
- **ShareAlike** — If you remix, transform, or build upon the material, you must distribute your contributions under the same license

```

**Icon Sources**

- VSCode Icons: [vscode-icons/vscode-icons repository](https://github.com/vscode-icons/vscode-icons)
- Material Icons: [PKief/vscode-material-icon-theme repository](https://github.com/PKief/vscode-material-icon-theme) (included in `/converter/vscode-material-icon-theme`)
- File Icons: [file-icons/vscode repository](https://github.com/file-icons/vscode) (included in `/converter/vscode`)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

If you encounter any issues or have suggestions, please [open an issue](https://github.com/yuyu1815/vscode-all-in-one-icon/issues).

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and changes.

---

Made with ❤️ by [yuyu1815](https://github.com/yuyu1815)
