# Developer Guide: Context-Aware Folder Icons

This plugin supports context-aware folder icons based on glob patterns.

## Adding Context Rules

Edit `src/main/resources/settings/context_rules.json`:

```json
{
  "rules": [
    {
      "pattern": "test/**/components",
      "icon": "test",
      "theme": "VSCODE_ICONS",
      "description": "Components under test directory"
    }
  ]
}
```

## Pattern Syntax

| Pattern | Description               | Example Match                                     |
| ------- | ------------------------- | ------------------------------------------------- |
| `*`     | Single segment wildcard   | `src/*/config` matches `src/main/config`          |
| `**`    | Multi-segment wildcard    | `test/**/utils` matches `test/unit/helpers/utils` |
| `*_tmp` | Wildcard within a segment | `cache/*_tmp` matches `cache/build_tmp`           |
| `pre*`  | Prefix wildcard           | `build/pre*` matches `build/pre_build`            |

## Matching Rules

1. **Priority**: Rules are evaluated in order. The first matching rule determines the icon.
2. **Case Insensitivity**: Patterns and paths are matched case-insensitively.
3. **Path Normalization**: All paths use forward slashes (`/`). Windows backslashes are automatically converted.
4. **Context**: Matching is performed against the file/folder's path relative to the project root.

## Available Themes

- `VSCODE_ICONS` - VSCode Icons (default)
- `MATERIAL_ICONS` - Material Design Icons
- `FILE_ICONS` - File Icons

## Adding New Rules

1. Add rule to `context_rules.json`
2. Ensure icon exists in corresponding theme folder
3. Rebuild plugin
