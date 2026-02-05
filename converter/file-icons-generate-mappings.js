// @ts-check
const fs = require('fs');
const path = require('path');

// Paths
const THEME_FILE = path.join(__dirname, 'vscode', 'icons', 'file-icons-icon-theme.json');
const OUTPUT_DIR = path.join(__dirname, '../src/main/resources/settings/file-icons');

/**
 * file-icons-icon-theme.jsonからマッピングを抽出してJSONファイルを生成
 * Material形式(iconName: [extensions])に変換
 */
function generateMappings() {
    console.log('Reading file-icons theme...');
    
    if (!fs.existsSync(THEME_FILE)) {
        console.error(`Theme file not found: ${THEME_FILE}`);
        process.exit(1);
    }

    const themeContent = fs.readFileSync(THEME_FILE, 'utf8');
    const theme = JSON.parse(themeContent);

    // Extract mappings (extension -> iconId)
    const extensionsRaw = theme.fileExtensions || {};
    const filenamesRaw = theme.fileNames || {};
    const foldersRaw = theme.folderNames || {};

    // Icon定義とフォント情報も保存
    const iconDefinitions = theme.iconDefinitions || {};
    const fonts = theme.fonts || [];

    // Convert to Material format (iconName -> [extensions])
    /** @type {Record<string, string[]>} */
    const extensions = {};
    for (const [ext, iconId] of Object.entries(extensionsRaw)) {
        if (!extensions[iconId]) {
            extensions[iconId] = [];
        }
        extensions[iconId].push(ext);
    }

    /** @type {Record<string, string[]>} */
    const filenames = {};
    for (const [filename, iconId] of Object.entries(filenamesRaw)) {
        if (!filenames[iconId]) {
            filenames[iconId] = [];
        }
        filenames[iconId].push(filename);
    }

    /** @type {Record<string, string[]>} */
    const folders = {};
    for (const [foldername, iconId] of Object.entries(foldersRaw)) {
        if (!folders[iconId]) {
            folders[iconId] = [];
        }
        folders[iconId].push(foldername);
    }

    console.log('Writing output files...');
    if (!fs.existsSync(OUTPUT_DIR)) {
        fs.mkdirSync(OUTPUT_DIR, { recursive: true });
    }

    // iconName -> [extensions] 形式で保存
    fs.writeFileSync(
        path.join(OUTPUT_DIR, 'icon_extensions.json'),
        JSON.stringify(extensions, null, 2)
    );

    // iconName -> [filenames] 形式で保存
    fs.writeFileSync(
        path.join(OUTPUT_DIR, 'icon_filenames.json'),
        JSON.stringify(filenames, null, 2)
    );

    // iconName -> [foldernames] 形式で保存
    fs.writeFileSync(
        path.join(OUTPUT_DIR, 'icon_folders.json'),
        JSON.stringify(folders, null, 2)
    );

    console.log('Done!');
    console.log(`Generated ${Object.keys(extensions).length} extension icon groups`);
    console.log(`Generated ${Object.keys(filenames).length} filename icon groups`);
    console.log(`Generated ${Object.keys(folders).length} folder icon groups`);
}

try {
    generateMappings();
} catch (e) {
    console.error('Error:', e);
    process.exit(1);
}
