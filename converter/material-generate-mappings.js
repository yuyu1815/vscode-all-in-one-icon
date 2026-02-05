const fs = require('fs');
const path = require('path');

// Paths
const MATERIAL_ICONS_DIR = path.join(__dirname, 'vscode-material-icon-theme', 'src', 'core', 'icons');
const OUTPUT_DIR = path.join(__dirname, '../src/main/resources/settings/material');

// Helper to extract strings from an array string like "['a', 'b']"
function extractArray(str) {
    if (!str) return [];
    const items = [];
    const regex = /['"`]([^'"`]+)['"`]/g;
    let match;
    while ((match = regex.exec(str)) !== null) {
        items.push(match[1]);
    }
    return items;
}

// Parse fileIcons.ts
function parseFileIcons(content) {
    const iconExtensions = {}; // icon -> Set relative extensions
    const iconFilenames = {};  // icon -> Set relative filenames
    
    // The content has "export const fileIcons: FileIcons = { ... icons: parseByPattern([ ... ]) ... }"
    // We want the content inside `parseByPattern([ ... ])` or just `icons: [ ... ]` if it wasn't wrapped (it is wrapped).
    
    // Let's find the array content. 
    // It starts after `icons: parseByPattern([` or `icons: [`
    const startRegex = /icons:\s*(?:parseByPattern\()?\s*\[/;
    const startMatch = content.match(startRegex);
    if (!startMatch) return { iconExtensions, iconFilenames };
    
    const arrayStartIndex = startMatch.index + startMatch[0].length;
    const arrayContent = content.substring(arrayStartIndex);
    
    // We can use a regex to match each object in the array.
    // { name: '...', fileExtensions: [...], ... }
    const objectRegex = /\{\s*name:\s*['"]([^'"]+)['"]([\s\S]*?)\}(?:,|$)/g;
    
    let match;
    while ((match = objectRegex.exec(arrayContent)) !== null) {
        const iconName = match[1];
        const body = match[2];
        
        // Check for fileExtensions
        const extMatch = body.match(/fileExtensions:\s*\[([\s\S]*?)\]/);
        if (extMatch) {
            const extensions = extractArray(extMatch[1]);
            if (!iconExtensions[iconName]) iconExtensions[iconName] = [];
            iconExtensions[iconName].push(...extensions);
        }
        
        // Check for fileNames
        const nameMatch = body.match(/fileNames:\s*\[([\s\S]*?)\]/);
        if (nameMatch) {
            const filenames = extractArray(nameMatch[1]);
            if (!iconFilenames[iconName]) iconFilenames[iconName] = [];
            iconFilenames[iconName].push(...filenames);
        }
    }
    
    return { iconExtensions, iconFilenames };
}

// Parse folderIcons.ts
function parseFolderIcons(content) {
    const iconFolders = {};
    
    // export const folderIcons: FolderTheme[] = [ ... icons: [ ... ] ... ]
    // This file structure is a bit different. It defines themes.
    // We want the `icons` array inside the theme, probably the first one or we aggregate all?
    // looking at the file, it has `name: 'specific'` and then `icons: [...]`.
    // It seems to be the main list.
    
    const startRegex = /icons:\s*\[/;
    const startMatch = content.match(startRegex);
    if (!startMatch) return iconFolders;
    
    const arrayStartIndex = startMatch.index + startMatch[0].length;
    const arrayContent = content.substring(arrayStartIndex);
    
    const objectRegex = /\{\s*name:\s*['"]([^'"]+)['"]([\s\S]*?)\}(?:,|$)/g;
    
    let match;
    while ((match = objectRegex.exec(arrayContent)) !== null) {
        const iconName = match[1];
        const body = match[2];
        
        const folderMatch = body.match(/folderNames:\s*\[([\s\S]*?)\]/);
        if (folderMatch) {
            const folders = extractArray(folderMatch[1]);
            if (!iconFolders[iconName]) iconFolders[iconName] = [];
            iconFolders[iconName].push(...folders);
        }
    }
    
    return iconFolders;
}

// Main
try {
    console.log('Reading source files...');
    const fileIconsContent = fs.readFileSync(path.join(MATERIAL_ICONS_DIR, 'fileIcons.ts'), 'utf8');
    const folderIconsContent = fs.readFileSync(path.join(MATERIAL_ICONS_DIR, 'folderIcons.ts'), 'utf8');
    
    console.log('Parsing file icons...');
    const { iconExtensions, iconFilenames } = parseFileIcons(fileIconsContent);
    
    console.log('Parsing folder icons...');
    const iconFolders = parseFolderIcons(folderIconsContent);
    
    console.log('Writing output files...');
    if (!fs.existsSync(OUTPUT_DIR)) {
        fs.mkdirSync(OUTPUT_DIR, { recursive: true });
    }
    
    fs.writeFileSync(path.join(OUTPUT_DIR, 'icon_extensions.json'), JSON.stringify(iconExtensions, null, 2));
    fs.writeFileSync(path.join(OUTPUT_DIR, 'icon_filenames.json'), JSON.stringify(iconFilenames, null, 2));
    fs.writeFileSync(path.join(OUTPUT_DIR, 'icon_folders.json'), JSON.stringify(iconFolders, null, 2));
    
    console.log('Done!');
    console.log(`Generated ${Object.keys(iconExtensions).length} extension icons`);
    console.log(`Generated ${Object.keys(iconFilenames).length} filename icons`);
    console.log(`Generated ${Object.keys(iconFolders).length} folder icons`);
    
} catch (e) {
    console.error('Error:', e);
    process.exit(1);
}
