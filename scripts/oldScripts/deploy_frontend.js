const validURL = (str) => {
  var pattern = new RegExp('^(https?:\\/\\/)?'+ // protocol
    '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'+ // domain name
    '((\\d{1,3}\\.){3}\\d{1,3}))'+ // OR ip (v4) address
    '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // port and path
    '(\\?[;&a-z\\d%_.~+=-]*)?'+ // query string
    '(\\#[-a-z\\d_]*)?$','i'); // fragment locator
  return !!pattern.test(str);
}

const nodeV = process.version.match(/^v(\d+)/)[1];
if (nodeV !== '12') {
  console.error('Please use node js in version 12!');
  return;
}
const exec = require('child_process').execSync;
const fs = require('fs');
const pth = require('path');
let address = 'https://registry.npmjs.org/';
const store = {};
for (const arg of process.argv) {
  if (arg.startsWith('--') && arg.includes('=')) {
    const [argKey, argVal] = arg.split('=');
    store[argKey] = argVal;
  }
  if (arg.startsWith('--') && !arg.includes('=')) {
    store[arg] = '';
  }
}

const snapId = +new Date();

// if (!Object.keys(store).includes('--fhVer')) {
//   console.error('Arguments --fhVer are obligatory!');
//   return;
// }

if (Object.keys(store).includes('--address')) {
  if (validURL(store['--address'])) {
    address = store['--address'];
  } else {
    console.error(`Address ${store['--address']} is invalid!`);
    return;
  }
}

let FH_PACKAGES = ["fh-basic-controls", "fh-charts-controls", "fh-designer",
                       "fh-forms-handler", "fh-maps-controls", "fh-printer-agent", 
                       "fh-sensors", 'fhdp-charts', 'fhdp-controls', 'fhdp-extenders',
                       'fhdp-fh-starter'];


let FH_DIRS = [
  '../packages/components/core-lite/',
  '../packages/components/basic-controls/', 
  '../packages/components/charts-controls/',
  '../packages/applications/default-application/',
  '../packages/components/fhdp-charts/',
  '../packages/components/fhdp-controls/',
  '../packages/components/fhdp-extenders/',
  '../packages/components/fhdp-fh-starter/'
];

const SKIP_PUBLISH_FH_DIRS = [
  '../packages/applications/default-application/'
]

let fhVer = store['--fhVer'];

const verbose = Object.keys(store).includes('--verbose');

let isDev = true;
const isProd = Object.keys(store).includes('--prod');
const isSnapshot = Object.keys(store).includes('--snap');

if (isProd) {
  isDev = false;
  if (!fhVer) {
    console.log(__dirname)
    const cPack = JSON.parse(fs.readFileSync(`${__dirname}/${FH_DIRS[0]}package.json`));
    fhVer = cPack.version.split('-')[0];
  }
}
if (isDev && !isSnapshot) {
  if (!fhVer) {
    console.log(__dirname)
    const cPack = JSON.parse(fs.readFileSync(`${__dirname}/${FH_DIRS[0]}package.json`));
    fhVer = cPack.version.split('-')[0];
  }

  fhVer = `${fhVer}-${require("os").userInfo().username}-${snapId}`;
} else if (isDev && isSnapshot) {
  if (!fhVer) {
    console.log(__dirname)
    const cPack = JSON.parse(fs.readFileSync(`${__dirname}/${FH_DIRS[0]}package.json`));
    console.log(cPack);
    fhVer = cPack.version.split('-')[0];
  }
  fhVer = `${fhVer}-SNAPSHOT-${snapId}`;
}
let shouldPublish = true;
if (!isDev && Object.keys(store).includes('--dryRun')) {
  shouldPublish = false;
}


console.log( {
  fhVer,
  address,
  isSnapshot,
  isProd,
  isDev
});
// return;

const runProcess = (command, needEnter) => {
  console.log(command)
  const process = exec(command, {stdio: verbose ? 'inherit' : ['ignore', 'ignore', 'ignore']});
  if (needEnter) {
    process.stdin.setEncoding = 'utf-8';
    process.stdin.write("\n");
    process.stdin.end();
  }
}

const publishProcess = (path, onlyUpdate) => {
  process.chdir(pth.join(__dirname, path));
  const name = path.split(pth.sep).splice(-2, 1);
  console.log('\n\n-----------------------------------------------')
  console.log(`${`-- deploing: ${name} ${fhVer}`.padEnd(45, ' ')}--`)
  console.log('-----------------------------------------------\n\n')
  let pack;
  if (fhVer) {
    pack = JSON.parse(fs.readFileSync('package.json'));
    pack.version = fhVer;
    if (pack.publishConfig && pack.publishConfig.registry) {
      pack.publishConfig.registry = address;
    }
    for (const dep of Object.keys(pack.dependencies)) {
      if (FH_PACKAGES.includes(dep)) {
        pack.dependencies[dep] = fhVer;
      }
    }
    fs.writeFileSync('package.json', JSON.stringify(pack, null, 2));
  }
  runProcess('rm -fr node_modules package-lock.json');
  if (shouldPublish && !onlyUpdate) {
    runProcess(`npm install --registry ${address}`);
    
    // try {
    //   runProcess(`npm unpublish ${pack.name}@${pack.version} --registry ${address}`);
    // } catch {}
    
    let tag;
    if (isDev && !isSnapshot) {
      tag = 'dev';
    } else if (isSnapshot) {
      tag = 'next';
    } else if (isProd) {
      tag = 'latest';
    }

    runProcess(`npm run build`);
    runProcess(`npm publish --force --tag ${tag} --registry ${address}`);
    runProcess('rm -fr node_modules package-lock.json');
  }
}

const searchForFile = (mainDir, packageFiles) => {
  fs.readdirSync(mainDir).forEach(file => {
    if (file === 'node_modules' || file === 'target') {
      return;
    }
    if (file === 'package.json') {
      // console.log(pth.join(mainDir, file))
      packageFiles.push(pth.join(mainDir, file));
    } else {
      const fPath = pth.join(mainDir, file);
      if (fs.statSync(fPath).isDirectory()) {
        searchForFile(fPath, packageFiles);
      }
    }
  })
  return packageFiles;
}

// clear cache
runProcess('rm -fr /home/teamcity/.cache/yarn/*');
runProcess('rm -fr /home/teamcity/.npm/cache');
runProcess('rm -fr /home/teamcity/.npm/.cache');
// publish fh
for (const path of FH_DIRS) {
  publishProcess(path, SKIP_PUBLISH_FH_DIRS.includes(path));
}

console.log(`NEW FH VERSION: ${fhVer}`);
