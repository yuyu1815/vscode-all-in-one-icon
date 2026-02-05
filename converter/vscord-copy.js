const fs = require('fs');
const path = require('path');

const SRC = path.join(__dirname, 'vscode-icons', 'icons');
const DEST = path.join(__dirname, '../src/main/resources/icons/vscode');

function copyIcons() {
    console.log('Processing vscode...');
    
    if (!fs.existsSync(SRC)) {
        console.error(`Source directory not found: ${SRC}`);
        return;
    }

    if (!fs.existsSync(DEST)) {
        console.log(`Creating directory: ${DEST}`);
        fs.mkdirSync(DEST, { recursive: true });
    }

    const files = fs.readdirSync(SRC);
    let count = 0;

    for (const file of files) {
        if (file.endsWith('.svg')) {
            const srcFile = path.join(SRC, file);
            const destFile = path.join(DEST, file);
            
            fs.copyFileSync(srcFile, destFile);
            count++;
        }
    }
    
    console.log(`Copied ${count} icons to ${DEST}`);
}

copyIcons();
