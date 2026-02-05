// @ts-check
const fs = require('fs');
const path = require('path');

// fontkitの読み込み
let fontkit;
try {
    fontkit = require('fontkit');
} catch (e) {
    console.error('fontkit is required. Please install it with: npm install fontkit');
    process.exit(1);
}

// Paths - Read directly from source theme file
const THEME_FILE = path.join(__dirname, 'vscode/icons/file-icons-icon-theme.json');
/** @type {string} */
const FONTS_DIR = path.join(__dirname, 'fonts');
const OUTPUT_DIR = path.join(__dirname, '../src/main/resources/icons/file-icons');

/**
 * file-iconsのアイコン定義からSVGファイルを生成
 */
function generateSVGs() {
    console.log('Reading icon definitions from source theme...');
    
    if (!fs.existsSync(THEME_FILE)) {
        console.error(`Theme file not found: ${THEME_FILE}`);
        process.exit(1);
    }

    const theme = JSON.parse(fs.readFileSync(THEME_FILE, 'utf8'));
    const iconDefinitions = theme.iconDefinitions || {};

    // フォントファイルを読み込み (fontkitを使用)
    /** @type {Object.<string, any>} */
    const fontMap = {};
    const fontFiles = {
        'fi': path.join(FONTS_DIR, 'file-icons.woff2'),
        'devicons': path.join(FONTS_DIR, 'devopicons.woff2'),
        'fa': path.join(FONTS_DIR, 'fontawesome.woff2'),
        'mf': path.join(FONTS_DIR, 'mfixx.woff2'),
        'octicons': path.join(FONTS_DIR, 'octicons.woff2')
    };

    console.log('Loading fonts...');
    for (const [fontId, fontPath] of Object.entries(fontFiles)) {
        if (fs.existsSync(fontPath)) {
            try {
                // fontkit.openSyncを使ってフォントを開く
                fontMap[fontId] = fontkit.openSync(fontPath);
                console.log(`Loaded font: ${fontId}`);
            } catch (error) {
                console.warn(`Failed to load font ${fontId}:`, error.message);
            }
        } else {
            console.warn(`Font file not found: ${fontPath}`);
        }
    }

    console.log('Preparing output directory...');
    if (fs.existsSync(OUTPUT_DIR)) {
        // 出力ディレクトリ内の全ファイルを削除してクリーンにする
        console.log('Cleaning output directory...');
        const files = fs.readdirSync(OUTPUT_DIR);
        for (const file of files) {
            const filePath = path.join(OUTPUT_DIR, file);
            if (fs.lstatSync(filePath).isFile()) {
                fs.unlinkSync(filePath);
            }
        }
    } else {
        fs.mkdirSync(OUTPUT_DIR, { recursive: true });
    }

    console.log('Generating SVG files...');
    let count = 0;
    let skipped = 0;

    for (const [iconName, iconDef] of Object.entries(iconDefinitions)) {
        try {
            // フォントを取得
            const font = fontMap[iconDef.fontId];
            if (!font) {
                throw new Error(`Font not loaded: ${iconDef.fontId}`);
            }

            // フォント文字を解析
            const codePoint = parseFontCharacter(iconDef.fontCharacter);
            
            // 色を解析
            const color = iconDef.fontColor || '#ffffff';
            
            // フォントサイズを解析
            const fontSizeMultiplier = iconDef.fontSize 
                ? parseFloat(iconDef.fontSize.replace('%', '')) / 100 
                : 1.0;
            
            // SVG生成
            // fontkitはグリフからパスを取得できる
            const svg = generateSVGFromFont(font, codePoint, color, fontSizeMultiplier);
            
            // ファイル保存
            const fileName = `${iconName}.svg`;
            const filePath = path.join(OUTPUT_DIR, fileName);
            fs.writeFileSync(filePath, svg);
            
            count++;
            
            if (count % 500 === 0) {
                console.log(`Generated ${count} SVG files...`);
            }
        } catch (error) {
            // console.warn(`Failed to generate SVG for ${iconName}:`, error.message);
            skipped++;
        }
    }

    console.log('Done!');
    console.log(`Generated ${count} SVG files`);
    console.log(`Skipped ${skipped} icons due to errors`);
}

/**
 * フォント文字列をUnicodeコードポイントに変換
 * @param {string} charStr
 * @returns {number}
 */
function parseFontCharacter(charStr) {
    let hex = charStr;
    if (hex.startsWith('\\')) {
        hex = hex.substring(1);
    }
    const codePoint = parseInt(hex, 16);
    if (isNaN(codePoint)) {
        throw new Error(`Invalid font character: ${charStr}`);
    }
    return codePoint;
}

/**
 * フォントからSVGを生成 (fontkit使用)
 * @param {any} font
 * @param {number} codePoint
 * @param {string} color
 * @param {number} sizeMultiplier
 * @returns {string}
 */
function generateSVGFromFont(font, codePoint, color, sizeMultiplier) {
    // グリフを取得
    // fontkitでは glyphForCodePoint を使う
    const glyph = font.glyphForCodePoint(codePoint);
    
    if (!glyph || !glyph.path) {
        throw new Error(`Glyph not found for codepoint: ${codePoint.toString(16)}`);
    }

    // どのサイズに収めるか
    const viewBoxSize = 16;
    
    // ベースサイズ (通常100% = 16pxとして計算)
    // ただし、アイコンによっては少し小さめの方が綺麗に見える場合も
    const targetSize = 14 * sizeMultiplier; 

    // スケール計算
    // font.unitsPerEm はフォントの定義サイズ (例: 1000 or 2048)
    const scale = targetSize / font.unitsPerEm;

    // パスデータをSVGのpathコマンドに変換
    const pathData = glyph.path.toSVG();

    // グリフのバウンディングボックスを取得してセンタリング
    // glyph.bbox は {minX, minY, maxX, maxY} などの形式
    const bbox = glyph.bbox;
    // バウンディングボックスがない場合（スペースなど）のガード
    if (!bbox) {
         return `<?xml version="1.0" encoding="UTF-8"?>
<svg width="16" height="16" viewBox="0 0 16 16" xmlns="http://www.w3.org/2000/svg">
</svg>`;
    }

    const glyphWidth = (bbox.maxX - bbox.minX) * scale;
    // @ts-ignore
    const glyphHeight = (bbox.maxY - bbox.minY) * scale;
    
    // センタリングオフセット
    // Y軸はフォント座標系(下がマイナス、上がプラス)とSVG座標系(下がプラス)の違いに注意
    // fontkitのtoSVG()で返ってくるパスはフォント座標系のまま(Y軸上向き)であることが多い
    // そのため、scale(1, -1) で反転させる必要がある
    
    const offsetX = (viewBoxSize - glyphWidth) / 2 - (bbox.minX * scale);
    
    // Y軸のセンタリング調整は少し複雑。ベースラインを中心に調整する
    // バウンディングボックスの中心をビューポートの中心に合わせる
    const midY = (bbox.maxY + bbox.minY) / 2 * scale;
    const offsetY = (viewBoxSize / 2) + midY;

    // SVGテンプレート
    // transformでスケールと反転、移動を適用
    // scale(scale, -scale) でY軸反転しつつサイズ合わせ
    
    return `<?xml version="1.0" encoding="UTF-8"?>
<svg width="16" height="16" viewBox="0 0 16 16" xmlns="http://www.w3.org/2000/svg">
  <g transform="translate(${offsetX}, ${offsetY}) scale(${scale}, -${scale})">
    <path d="${pathData}" fill="${color}" />
  </g>
</svg>`;
}

generateSVGs();
