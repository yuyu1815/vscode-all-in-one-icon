// @ts-check
const fs = require('fs');
const path = require('path');

// Paths
const VSCODE_ICONS_DIR = path.join(__dirname, 'vscode-icons', 'src', 'iconsManifest');
const OUTPUT_DIR = path.join(__dirname, '../src/main/resources/settings/vscord');

// Helper to extract strings from an array string like "['a', 'b']"
function extractArray(str) {
    if (!str) return [];
    const items = [];
    const regex = /['"`]([^'"`]+)['"`]/g;
    let match;
    while ((match = regex.exec(str)) !== null) {
        // Remove leading/trailing dot if it's meant to be an extension? 
        // No, the source has 'accdb', target has 'accdb'.
        // But source 'allcontributors' extensions: ['.all-contributorsrc'] -> target ['.all-contributorsrc']
        // So keep exactly as is.
        items.push(match[1]);
    }
    return items;
}

// Parse languages.ts
function parseLanguages(content) {
    const languages = {}; // key -> { extensions: [], filenames: [] }
    
    // Regex to capture "key: { ... }" blocks
    // This is tricky with nested braces, but languages.ts structure is regular.
    // keys are "actionscript: { ... },"
    
    const lines = content.split('\n');
    let currentKey = null;
    let buffer = '';
    
    // Simple state machine parsing
    for (let line of lines) {
        const keyMatch = line.match(/^\s*(\w+):\s*\{/);
        if (keyMatch) {
            currentKey = keyMatch[1];
        }
        
        if (currentKey) {
            buffer += line;
            if (line.includes('},')) {
                // End of block
                const extensionsMatch = buffer.match(/knownExtensions:\s*\[([\s\S]*?)]/);
                const filenamesMatch = buffer.match(/knownFilenames:\s*\[([\s\S]*?)]/);
                
                languages[currentKey] = {
                    extensions: extensionsMatch ? extractArray(extensionsMatch[1]) : [],
                    filenames: filenamesMatch ? extractArray(filenamesMatch[1]) : []
                };
                
                currentKey = null;
                buffer = '';
            }
        }
    }
    return languages;
}

// Parse supportedExtensions.ts
function parseExtensions(content, languageMap) {
    const iconExtensions = {}; // icon -> Set relative extensions
    const iconFilenames = {};  // icon -> Set relative filenames
    
    // We look for objects in "supported: [ ... ]"
    // Regex to match "supported: [" then parse objects?
    // The objects are comma separated.
    
    // Naive block splitter: match "{ icon: ... }"
    // Assuming balanced braces isn't too hard or simply content between { and }

    // Let's use a regex to find each object block starting with "{ icon:" or "{\n\s*icon:"
    const objectRegex = /\{\s*icon:\s*['"]([^'"]+)['"]([\s\S]*?)}(?:,|$)/g;
    
    // We need to limit this to the 'supported' array, avoiding 'default'.
    const supportedStart = content.indexOf('supported: [');
    if (supportedStart === -1) return { iconExtensions, iconFilenames };
    
    const supportedContent = content.substring(supportedStart);
    
    let match;
    // We assume the objectRegex will continue finding matches
    while ((match = objectRegex.exec(supportedContent)) !== null) {
        const icon = match[1];
        const body = match[2];
        
        if (!iconExtensions[icon]) iconExtensions[icon] = new Set();
        if (!iconFilenames[icon]) iconFilenames[icon] = new Set();
        
        // 1. Extensions
        const extMatch = body.match(/extensions:\s*\[([\s\S]*?)]/);
        if (extMatch) {
            extractArray(extMatch[1]).forEach(e => iconExtensions[icon].add(e));
        }

        // 2. Filenames / FilenamesGlob
        const fileMatch = body.match(/filenames:\s*\[([\s\S]*?)]/);
        if (fileMatch) {
            extractArray(fileMatch[1]).forEach(f => iconFilenames[icon].add(f));
        }

        const globMatch = body.match(/filenamesGlob:\s*\[([\s\S]*?)]/);
        if (globMatch) {
            extractArray(globMatch[1]).forEach(f => iconFilenames[icon].add(f));
        }

        // 3. Languages
        const langMatch = body.match(/languages:\s*\[([\s\S]*?)]/);
        if (langMatch) {
            // Find "languages.something"
            const langRefs = langMatch[1].matchAll(/languages\.(\w+)/g);
            for (const ref of langRefs) {
                const langKey = ref[1];
                const langData = languageMap[langKey];
                if (langData) {
                    langData.extensions.forEach(e => iconExtensions[icon].add(e));
                    langData.filenames.forEach(f => iconFilenames[icon].add(f));
                }
            }
        }
    }
    
    // Convert Sets to Arrays and sort
    const finalExtensions = {};
    const finalFilenames = {};
    
    Object.keys(iconExtensions).forEach(key => {
        if (iconExtensions[key].size > 0) {
            finalExtensions[key] = Array.from(iconExtensions[key]); 
        }
    });

    Object.keys(iconFilenames).forEach(key => {
        if (iconFilenames[key].size > 0) {
            finalFilenames[key] = Array.from(iconFilenames[key]);
        }
    });
    
    return { extensions: finalExtensions, filenames: finalFilenames };
}

// Parse supportedFolders.ts
function parseFolders(content) {
    const iconFolders = {};
    
    const supportedStart = content.indexOf('supported: [');
    if (supportedStart === -1) return iconFolders;
    const supportedContent = content.substring(supportedStart);
    
    const objectRegex = /\{\s*icon:\s*['"]([^'"]+)['"]([\s\S]*?)}(?:,|$)/g;
    
    let match;
    while ((match = objectRegex.exec(supportedContent)) !== null) {
        const icon = match[1];
        const body = match[2];
        
        if (!iconFolders[icon]) iconFolders[icon] = new Set();
        
        const extMatch = body.match(/extensions:\s*\[([\s\S]*?)]/);
        if (extMatch) {
            extractArray(extMatch[1]).forEach(e => iconFolders[icon].add(e));
        }
    }
    
    const finalFolders = {};
    Object.keys(iconFolders).forEach(key => {
        if (iconFolders[key].size > 0) {
            finalFolders[key] = Array.from(iconFolders[key]);
        }
    });
    
    return finalFolders;
}

// Main
try {
    console.log('Reading source files...');
    const languagesContent = fs.readFileSync(path.join(VSCODE_ICONS_DIR, 'languages.ts'), 'utf8');
    const extensionsContent = fs.readFileSync(path.join(VSCODE_ICONS_DIR, 'supportedExtensions.ts'), 'utf8');
    const foldersContent = fs.readFileSync(path.join(VSCODE_ICONS_DIR, 'supportedFolders.ts'), 'utf8');
    
    console.log('Parsing languages...');
    const languageMap = parseLanguages(languagesContent);
    // console.log(languageMap);
    
    console.log('Parsing extensions and filenames...');
    const { extensions, filenames } = parseExtensions(extensionsContent, languageMap);
    
    console.log('Parsing folders...');
    const folders = parseFolders(foldersContent);
    
    console.log('Writing output files...');
    if (!fs.existsSync(OUTPUT_DIR)) {
        fs.mkdirSync(OUTPUT_DIR, { recursive: true });
    }
    
    fs.writeFileSync(path.join(OUTPUT_DIR, 'icon_extensions.json'), JSON.stringify(extensions, null, 2));
    fs.writeFileSync(path.join(OUTPUT_DIR, 'icon_filenames.json'), JSON.stringify(filenames, null, 2));
    fs.writeFileSync(path.join(OUTPUT_DIR, 'icon_folders.json'), JSON.stringify(folders, null, 2));
    
    console.log('Done!');
    console.log(`Generated ${Object.keys(extensions).length} extension icons`);
    console.log(`Generated ${Object.keys(filenames).length} filename icons`);
    console.log(`Generated ${Object.keys(folders).length} folder icons`);
    
} catch (e) {
    console.error('Error:', e);
    process.exit(1);
}
