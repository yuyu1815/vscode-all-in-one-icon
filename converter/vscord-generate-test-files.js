#!/usr/bin/env node
/**
 * VSCode Icons用アイコンテストファイル構造を生成するスクリプト
 */
const fs = require('fs');
const path = require('path');

/**
 * JSONファイルを読み込む
 */
function loadJson(filePath) {
    try {
        const content = fs.readFileSync(filePath, 'utf-8');
        return JSON.parse(content);
    } catch (error) {
        console.error(`Error loading ${filePath}:`, error.message);
        return {};
    }
}

/**
 * Icon -> [ext1, ext2] 形式を ext1 -> Icon, ext2 -> Icon に変換
 */
function flattenMapping(iconMap) {
    const result = {};
    for (const [iconName, extensions] of Object.entries(iconMap)) {
        for (const ext of extensions) {
            result[ext] = iconName;
        }
    }
    return result;
}

/**
 * VSCode Icons用テスト構造を作成
 */
function createVSCodeTestStructure() {
    // 設定ファイルのパス
    const settingsDir = path.join(__dirname, '..', 'src', 'main', 'resources', 'settings', 'vscord');
    const testDir = path.join(__dirname, '..', 'icon_test', 'vscode');
    
    // マッピングを読み込み
    const extensionsMap = flattenMapping(loadJson(path.join(settingsDir, 'icon_extensions.json')));
    const filenamesMap = flattenMapping(loadJson(path.join(settingsDir, 'icon_filenames.json')));
    const foldersMap = flattenMapping(loadJson(path.join(settingsDir, 'icon_folders.json')));
    
    // テスト用ディレクトリをクリアして作成
    if (fs.existsSync(testDir)) {
        fs.rmSync(testDir, { recursive: true, force: true });
    }
    fs.mkdirSync(testDir, { recursive: true });
    
    console.log('🎨 Generating VSCode Icons test files...\n');
    
    // ファイルを作成（拡張子ベース） - サンプルを150個
    const extSampleSize = Math.min(150, Object.keys(extensionsMap).length);
    console.log(`📄 Creating ${extSampleSize} files by extension...`);
    let count = 0;
    for (const [ext, iconName] of Object.entries(extensionsMap)) {
        if (count >= extSampleSize) break;
        const filePath = path.join(testDir, `test${String(count).padStart(3, '0')}.${ext}`);
        fs.writeFileSync(filePath, `// VSCode Icons test file\n// Icon: ${iconName}\n`, 'utf-8');
        count++;
    }
    
    // ファイルを作成（ファイル名ベース） - サンプルを150個
    const fileSampleSize = Math.min(150, Object.keys(filenamesMap).length);
    console.log(`📄 Creating ${fileSampleSize} files by name...`);
    count = 0;
    for (const [filename, iconName] of Object.entries(filenamesMap)) {
        if (count >= fileSampleSize) break;
        let filePath = path.join(testDir, filename);
        if (filename.includes('/') || filename.includes('\\')) {
            const normalizedFilename = filename.replace(/\//g, path.sep).replace(/\\/g, path.sep);
            filePath = path.join(testDir, normalizedFilename);
            fs.mkdirSync(path.dirname(filePath), { recursive: true });
        }
        fs.writeFileSync(filePath, `// VSCode Icons test file\n// Icon: ${iconName}\n`, 'utf-8');
        count++;
    }
    
    // フォルダーを作成 - サンプルを150個
    const folderSampleSize = Math.min(150, Object.keys(foldersMap).length);
    console.log(`📁 Creating ${folderSampleSize} folders...`);
    count = 0;
    for (const [folderName, iconName] of Object.entries(foldersMap)) {
        if (count >= folderSampleSize) break;
        let folderPath = path.join(testDir, folderName);
        if (folderName.includes('/') || folderName.includes('\\')) {
            const normalizedFolderName = folderName.replace(/\//g, path.sep).replace(/\\/g, path.sep);
            folderPath = path.join(testDir, normalizedFolderName);
        }
        try {
            fs.mkdirSync(folderPath, { recursive: true });
        } catch (error) {
            // 既に存在する場合は無視
            if (error.code !== 'EEXIST') throw error;
        }
        // ディレクトリが確実に存在することを確認
        if (!fs.existsSync(folderPath)) {
            fs.mkdirSync(folderPath, { recursive: true });
        }
        try {
            fs.writeFileSync(path.join(folderPath, '.gitkeep'), `# VSCode Icons test folder\n# Icon: ${iconName}\n`, 'utf-8');
        } catch (error) {
            // ファイルの作成に失敗した場合はスキップ
            if (error.code !== 'ENOENT') throw error;
        }
        count++;
    }
    
    console.log(`\n✅ VSCode Icons test files generated successfully!`);
    console.log(`📍 Location: ${testDir}`);
    console.log(`\n📊 Summary:`);
    console.log(`   • Files by extension: ${extSampleSize}`);
    console.log(`   • Files by name: ${fileSampleSize}`);
    console.log(`   • Folders: ${folderSampleSize}`);
}

createVSCodeTestStructure();
