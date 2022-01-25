const fs = require('fs');

const getVersion = (file) => {
  const package = JSON.parse(fs.readFileSync(file));
  return package.version;
}

const toggleDevelopment = () => {
  const path = '../../../fhdp-common/fhdp-commons/fhdp-commons-fh-starter/package.json';
  const package = JSON.parse(fs.readFileSync(path));
  const fileNode = '../../../npm-packages/components/fhdp-controls/fhdp-controls-v';
  let isDevelop = false;
  if (package.dependencies['fhdp-controls'].includes(fileNode)) {
    isDevelop = true;
  }

  if (!isDevelop) {
    package.dependencies['fhdp-controls'] = `${fileNode}${getVersion('package.json')}.tgz`;
    console.log('now its DEVELOPMENT main');
  } else {
    package.dependencies['fhdp-controls'] = `^${getVersion('package.json')}`;
    console.log('now its RELEASE main');
  }
  fs.writeFileSync(path, JSON.stringify(package, null, 2));
}

const toggleDevelopment2 = () => {
  const path = '../../../fhdp-pl/fhdp-pl-seap/fhdp-pl-seap-module/package.json'
  const package = JSON.parse(fs.readFileSync(path));
  const fileNode = '../../../npm-packages/components/fhdp-controls/fhdp-controls-v';
  let isDevelop = false;
  if (package.dependencies['fhdp-controls'].includes(fileNode)) {
    isDevelop = true;
  }

  if (!isDevelop) {
    package.dependencies['fhdp-controls'] = `${fileNode}${getVersion('package.json')}.tgz`;
    console.log('now its DEVELOPMENT SEAP');
  } else {
    package.dependencies['fhdp-controls'] = `^${getVersion('package.json')}`;
    console.log('now its RELEASE SEAP');
  }
  fs.writeFileSync(path, JSON.stringify(package, null, 2));
}

toggleDevelopment();
toggleDevelopment2();
