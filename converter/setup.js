const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const repositories = [
  {
    name: 'vscode',
    url: 'https://github.com/file-icons/vscode',
    dir: 'vscode'
  },
  {
    name: 'vscode-material-icon-theme',
    url: 'https://github.com/material-extensions/vscode-material-icon-theme',
    dir: 'vscode-material-icon-theme'
  },
  {
    name: 'vscode-icons',
    url: 'https://github.com/vscode-icons/vscode-icons',
    dir: 'vscode-icons'
  }
];

function cloneRepo(repo) {
  const targetPath = path.join(__dirname, repo.dir);
  
  if (fs.existsSync(targetPath)) {
    console.log(`✓ ${repo.name} is already cloned, skipping...`);
    return;
  }
  
  console.log(`Cloning ${repo.name}...`);
  try {
    execSync(`git clone ${repo.url} ${repo.dir}`, {
      cwd: __dirname,
      stdio: 'inherit'
    });
    console.log(`✓ Successfully cloned ${repo.name}`);
  } catch (error) {
    console.error(`✗ Failed to clone ${repo.name}:`, error.message);
    process.exit(1);
  }
}

console.log('Setting up icon repositories...\n');

repositories.forEach(cloneRepo);

console.log('\n✓ Setup complete!');
