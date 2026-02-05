#!/usr/bin/env node
/**
 * file-icons用アイコンテストファイル構造を生成するスクリプト
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
 * file-icons用テスト構造を作成
 */
function createFileIconsTestStructure() {
    // 設定ファイルのパス
    const settingsDir = path.join(__dirname, '..', 'src', 'main', 'resources', 'settings', 'file-icons');
    const testDir = path.join(__dirname, '..', 'icon_test', 'file-icons');
    
    // マッピングを読み込み (extension/filename -> iconId)
    const extensionsMap = loadJson(path.join(settingsDir, 'icon_extensions.json'));
    const filenamesMap = loadJson(path.join(settingsDir, 'icon_filenames.json'));
    const foldersMap = loadJson(path.join(settingsDir, 'icon_folders.json'));
    
    // テスト用ディレクトリをクリアして作成
    if (fs.existsSync(testDir)) {
        fs.rmSync(testDir, { recursive: true, force: true });
    }
    fs.mkdirSync(testDir, { recursive: true });
    
    console.log('🎨 Generating file-icons test files...\n');
    
    // ファイルを作成（拡張子ベース） - サンプルを150個
    // extensionsMap形式: { iconId: [ext1, ext2, ...] }
    console.log(`📄 Creating files by extension...`);
    let count = 0;
    const extSampleSize = 150;
    for (const [iconId, extensions] of Object.entries(extensionsMap)) {
        if (count >= extSampleSize) break;
        // 各アイコンIDに対応する拡張子リストから1つ選んでファイルを作成
        const extArray = /** @type {string[]} */ (extensions);
        if (extArray && extArray.length > 0) {
            const ext = extArray[0]; // 最初の拡張子を使用
            const filePath = path.join(testDir, `test${String(count).padStart(3, '0')}.${ext}`);
            fs.writeFileSync(filePath, `// file-icons test file\n// Icon ID: ${iconId}\n`, 'utf-8');
            count++;
        }
    }
    
    // ファイルを作成（ファイル名ベース） - サンプルを150個
    // filenamesMap形式: { iconId: [filename1, filename2, ...] }
    console.log(`📄 Creating files by name...`);
    let fileCount = 0;
    const fileSampleSize = 150;
    for (const [iconId, filenames] of Object.entries(filenamesMap)) {
        if (fileCount >= fileSampleSize) break;
        const filenameArray = /** @type {string[]} */ (filenames);
        if (filenameArray && filenameArray.length > 0) {
            const filename = filenameArray[0]; // 最初のファイル名を使用
            let filePath = path.join(testDir, filename);
            if (filename.includes('/') || filename.includes('\\')) {
                const normalizedFilename = filename.replace(/\//g, path.sep).replace(/\\/g, path.sep);
                filePath = path.join(testDir, normalizedFilename);
                fs.mkdirSync(path.dirname(filePath), { recursive: true });
            }
            fs.writeFileSync(filePath, `// file-icons test file\n// Icon ID: ${iconId}\n`, 'utf-8');
            fileCount++;
        }
    }
    
    // フォルダーを作成 - サンプルを150個
    // foldersMap形式: { iconId: [foldername1, foldername2, ...] }
    console.log(`📁 Creating folders...`);
    let folderCount = 0;
    const folderSampleSize = 150;
    for (const [iconId, foldernames] of Object.entries(foldersMap)) {
        if (folderCount >= folderSampleSize) break;
        const foldernameArray = /** @type {string[]} */ (foldernames);
        if (foldernameArray && foldernameArray.length > 0) {
            const folderName = foldernameArray[0]; // 最初のフォルダ名を使用
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
                fs.writeFileSync(path.join(folderPath, '.gitkeep'), `# file-icons test folder\n# Icon ID: ${iconId}\n`, 'utf-8');
            } catch (error) {
                // ファイルの作成に失敗した場合はスキップ
                if (error.code !== 'ENOENT') throw error;
            }
            folderCount++;
        }
    }
    
    console.log(`\n✅ file-icons test files generated successfully!`);
    console.log(`📍 Location: ${testDir}`);
    console.log(`\n📊 Summary:`);
    console.log(`   • Files by extension: ${count}`);
    console.log(`   • Files by name: ${fileCount}`);
    console.log(`   • Folders: ${folderCount}`);
}

createFileIconsTestStructure();
