# AI Collaborative Development Guidelines

This document provides guidelines for AI agents and developers to work efficiently on this project.

---

## 1. Project Overview

**vscode-all-in-one-icon** is a file/folder icon plugin for JetBrains IDEs.

### Key Features
- Multiple icon themes (VSCode Icons, Material Icons, File Icons)
- Theme priority system with fallback resolution
- 2,000+ file and folder icons
- Multi-language UI (English, Japanese, Chinese, Korean)

### Tech Stack
- **Language**: Kotlin
- **Build**: Gradle (Kotlin DSL)
- **JDK**: 21
- **Platform**: IntelliJ Platform Plugin

---

## 2. Directory Structure

```
/
├── src/
│   ├── main/
│   │   ├── kotlin/          # Plugin source code
│   │   └── resources/
│   │       ├── icons/       # SVG icon files
│   │       │   ├── vscode/      # VSCode Icons theme
│   │       │   ├── material/    # Material Icons theme
│   │       │   └── file-icons/  # File Icons theme
│   │       ├── settings/    # JSON mapping files
│   │       └── META-INF/    # Plugin configuration
│   └── test/kotlin/         # Test code
├── converter/               # Icon generation scripts (Node.js)
├── icon_test/               # Generated test files
├── .junie/                  # AI/development guidelines
├── build.gradle.kts         # Build configuration
├── gradle.properties        # Plugin/Gradle settings
├── README.md                # Project description (English)
├── README.ja.md             # Project description (Japanese)
├── CHANGELOG.md             # Change history
└── LICENSE                  # License (CC BY-SA 4.0)
```

### Key File Roles

| File | Role |
|------|------|
| `build.gradle.kts` | Gradle build config, dependencies, plugin settings |
| `gradle.properties` | Plugin version, platform version, JVM settings |
| `settings.gradle.kts` | Project name, plugin repository settings |
| `CHANGELOG.md` | Change history in [Keep a Changelog](https://keepachangelog.com/) format |
| `plugin.xml` | IntelliJ plugin manifest |

---

## 3. Build & Configuration

### JDK Setup
```powershell
$env:JAVA_HOME = "<path-to-your-jdk-21>"
```

### Build Command
```powershell
.\gradlew.bat build
```

### Run Plugin (Launch IDE)
```powershell
.\gradlew.bat runIde
```

---

## 4. Testing

### Running Tests
```powershell
# All tests
$env:JAVA_HOME = "<path-to-your-jdk-21>"; .\gradlew.bat test

# Specific test class
$env:JAVA_HOME = "<path-to-your-jdk-21>"; .\gradlew.bat test --tests "com.github.yuyu1815.vscodeallinoneicon.ResourceConsistencyTest"
```

### Existing Test Classes
| Class | Purpose |
|-------|---------|
| `ResourceConsistencyTest` | Verifies icon mappings and SVG resource consistency |
| `IconResolverTest` | Tests icon resolution logic |
| `MappingsTest` | Tests mapping data integrity |

### Test Strategy (Test Pyramid)
- **Unit Tests**: Individual feature verification (foundation, most numerous)
- **Integration Tests**: Component interaction
- **E2E Tests**: Critical user flows only (fewest)

### AAA Pattern
Write tests with the following structure:
```kotlin
fun testExample() {
    // Arrange
    val resolver = IconResolver
    resolver.setThemes(listOf(IconTheme.VSCODE_ICONS))
    
    // Act
    val result = resolver.resolveIconName("test.kt", false)
    
    // Assert
    assertNotNull(result)
    assertEquals("kotlin", result?.iconName)
}
```

---

## 5. Icon Theme Generation

The `converter/` directory contains Node.js scripts to generate mappings from upstream icon themes.

### Setup
```powershell
cd converter
npm install
npm run setup        # Clone upstream repositories
npm run generate:all # Generate all mappings
```

### Individual Generation
```powershell
npm run generate:file-icons  # File Icons
npm run generate:material    # Material Icons
npm run generate:vscord      # VSCode Icons
```

### Generated Files
- `src/main/resources/settings/*/` - JSON mapping files
- `src/main/resources/icons/*/` - SVG icon files
- `icon_test/*/` - Test files

---

## 6. Code Style

### Core Principles
- **Keep nesting shallow**: Use early returns and guard clauses
- **Use descriptive names**: Function and variable names should convey intent
- **Write comments in English** (to match existing codebase)
- **Push side effects to boundaries**: I/O on the outside, logic on the inside

### Kotlin-Specific
- Use `?.let { }` and `?:` for null-safe code
- Use `when` expressions for exhaustive pattern matching
- Leverage data classes

### Example
```kotlin
// Good: Early return keeps nesting shallow
private fun resolveFileIcon(name: String, theme: IconTheme): String? {
    val lowercaseName = name.lowercase()
    
    // 1. Exact filename match
    getFileNameMap(theme)[lowercaseName]?.let { return it }
    
    // 2. Extension match
    var current = lowercaseName
    while (current.contains(".")) {
        current = current.substringAfter(".")
        getExtensionMap(theme)[current]?.let { return it }
    }
    
    return null
}
```

---

## 7. AI Collaboration Rules

### Core Principles
- **Don't swallow errors**: Investigate failures and fix them properly
- **Find workarounds for lint errors**: Don't disable or delete rules
- **Minimal diff changes**: Avoid unnecessary modifications

### Change Process
1. **Consider alternative approaches** from a broad perspective
2. **Write tests first** to define expected behavior
3. **Implement/improve** until tests pass
4. **Refactor if needed** (but don't mix purposes)

### AI Request Template
```markdown
## Objective
[What you want to achieve]

## Constraints
- Files allowed to modify: [specify scope]
- Off-limits: [prohibited items]
- Technologies: Kotlin, IntelliJ Platform SDK

## Expected Output
- [ ] Present diff in patch format
- [ ] Include test code
- [ ] Explain changes with comments

## Additional Instructions
- Minimal diff changes
- Refactoring in separate PR
```

### Verification Checklist
- [ ] `.\gradlew.bat build` succeeds
- [ ] `.\gradlew.bat test` all pass
- [ ] Follows existing code style

---

## 8. PR/Commit Conventions

### Commit Messages
- Summary within 50 characters
- Add detailed description as needed
- Commit at granularity that allows tracking change reasons

---

## 9. Troubleshooting

### Build Errors
```powershell
# Clear Gradle cache
.\gradlew.bat clean

# Refresh dependencies
.\gradlew.bat --refresh-dependencies build
```

### Test Failures
- If `ResourceConsistencyTest` fails, icon files may be missing
- Try regenerating icons in `converter/`

### Icons Not Displaying
- Check `<fileIconProvider>` configuration in `plugin.xml`
- Look for "Missing Icon" messages in logs

---

## 10. Reference Resources

- [IntelliJ Platform SDK Docs](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [IntelliJ Platform Gradle Plugin](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html)
- [Keep a Changelog](https://keepachangelog.com/)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)

---

## 11. License

This plugin is licensed under **CC BY-SA 4.0**.

Icon Sources:
- **vscode-icons**: CC BY-SA 4.0
- **vscode-material-icon-theme**: MIT
- **file-icons**: MIT
