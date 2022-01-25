const fs = require('fs');

const versionUp = (file, main) => {
  if (main) {
    const package = JSON.parse(fs.readFileSync(file));
    let [main, mid, min] = package.version.split('.');
    min++;
    package.version = [main, mid, min].join('.');
    fs.writeFileSync(file, JSON.stringify(package, null, 2));
    return package.version;
  } else {
    const package = JSON.parse(fs.readFileSync(file));
    let [_, version] = package.dependencies['fhdp-controls'].split('fhdp-controls-v');
    let [main, mid, min, ext] = version.split('.');
    min++;
    version = [main, mid, min, ext].join('.');
    package.dependencies['fhdp-controls'] = [_, version].join('fhdp-controls-v');
    fs.writeFileSync(file, JSON.stringify(package, null, 2));
  }
}

const newVersion = versionUp('package.json', true);
versionUp('../../../fhdp-common/fhdp-commons/fhdp-commons-fh-starter/package.json');
versionUp('../../../fhdp-pl/fhdp-pl-seap/fhdp-pl-seap-module/package.json');
console.log(`New version: ${newVersion}`);
