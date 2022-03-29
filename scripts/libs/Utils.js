const {get} = require('./Network');
const fs = require('fs');
const path = require('path');
const exec = require('child_process').execSync;

const REGISTRY_PATH = process.env.REGISTRY_PATH || 'https://registry.npmjs.org';

const getNewestPackageSubVersion = async (packageName, baseVersion) => {
  const remoteData = await get({
    address: `${REGISTRY_PATH}/${packageName}`,
  });
  if (baseVersion.split('-').length === 3) {
    const parts = baseVersion.split('-');
    parts.pop();
    baseVersion = parts.join('-');
  }

  let version = Object.keys(remoteData.versions);
  version = version.filter(el => el.startsWith(baseVersion) && el.split('-').length === 3);
  version = version.map(el => {
    const l = el.split('-');
    return Number(l[l.length - 1]);
  });
  const latest = Math.max(...version);
  const latest_ver = `${baseVersion}-${latest}`
  return latest_ver;
}

const getNewestFhSubVersion = async (baseVersion) => {
  return await getNewestPackageVersion('fh-forms-handler', baseVersion);
}

const getPackageVersion = async (packageName, tag = 'latest') => {
  if (!['latest', 'next'].includes(tag)) {
    console.error('tag can be only latest or next');
    return;
  }
  const remoteData = await get({
    address: `${REGISTRY_PATH}/${packageName}`,
  });
  return remoteData['dist-tags'][tag];
}

const _getFiles = async (dir, matchPatternFn, ignorePatternFn, verbose) => {
  if (verbose) {
    printSameLine(`Scanning ${dir}`);
  }
  const subdirs = fs.readdirSync(dir);
  const files = await Promise.all(subdirs.map(async (subdir) => {
    const res = path.resolve(dir, subdir);
    if (!ignorePatternFn(res)) {
      if ((fs.statSync(res)).isDirectory()) {
        return _getFiles(res, matchPatternFn, ignorePatternFn, verbose);
      } else {
        if (matchPatternFn(res)) {
          return res;
        }
        return null;
      }
    }
    return null;
  }));
  return files.filter(el => !!el).reduce((a, f) => a.concat(f), []);
}

const getPackages = async (relativePath, verbose) => {
  console.log('Scanning packages');
  const currentPath = process.cwd();
  const searchPath = `${currentPath}/${relativePath}`
  matchPattern = (pth) => pth.endsWith('/package.json');
  ignorePattern = (pth) => pth.endsWith('node_modules') || pth.endsWith('node');
  const files = await _getFiles(searchPath, matchPattern, ignorePattern, verbose);
  return files;
}

const readJSON = (pth) => {
  if (!fs.existsSync(pth)) {
    console.error(`File at path ${pth} not exists!`);
    return {};
  }
  const dataBuffer = fs.readFileSync(pth);
  return JSON.parse(dataBuffer.toString());
}

const writeJSON = (pth, data) => {
  fs.writeFileSync(pth, JSON.stringify(data, null, 2));
}

const parseParams = () => {
  const store = {
    processName: process.argv[2],
    has: (key) => {
      return store.hasOwnProperty(`--${key}`);
    },
    get: (key) => {
      return store[`--${key}`];
    }
  };
  for (const arg of process.argv) {
    if (arg.startsWith('--') && arg.includes('=')) {
      const [argKey, argVal] = arg.split('=');
      store[argKey] = argVal;
    }
    if (arg.startsWith('--') && !arg.includes('=')) {
      store[arg] = '';
    }
  }
  return store;
}

const validURL = (str) => {
  var pattern = new RegExp('^(https?:\\/\\/)?'+ // protocol
    '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'+ // domain name
    '((\\d{1,3}\\.){3}\\d{1,3}))'+ // OR ip (v4) address
    '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // port and path
    '(\\?[;&a-z\\d%_.~+=-]*)?'+ // query string
    '(\\#[-a-z\\d_]*)?$','i'); // fragment locator
  return !!pattern.test(str);
}

const runProcess = (command, verbose, needEnter) => {
  console.log(command)
  const process = exec(command, {stdio: verbose ? 'inherit' : ['ignore', 'ignore', 'ignore']});
  if (needEnter) {
    process.stdin.setEncoding = 'utf-8';
    process.stdin.write("\n");
    process.stdin.end();
  }
}

const printSameLine = (text) => {
  const platform = require('os').platform();
  process.stdout.write((platform == 'win32') ? "\033[0G": "\r");
  process.stdout.write(" ".repeat(process.stdout.columns));
  process.stdout.write((platform == 'win32') ? "\033[0G": "\r");
  process.stdout.write(text);
}

module.exports = {
  getNewestFhSubVersion, 
  getNewestPackageSubVersion, 
  getPackages, 
  readJSON,
  writeJSON,
  getPackageVersion,
  parseParams,
  validURL,
  runProcess,
  printSameLine
};