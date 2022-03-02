const fs = require('fs');
const utils = require('./Utils');
const network = require('./Network');
const path = require('path')

const REGISTRY_PATH = process.env.REGISTRY_PATH || 'https://registry.npmjs.org';
const LOCAL_REGISTRY_PATH = process.env.LOCAL_REGISTRY_PATH || `${require('os').homedir()}/.node_registry`;

const _isFH = (name) => {
  const fhModules = require('../data/packages_list.json');
  return fhModules.pkgs.includes(name);
}

const _extractVersionFromFileName = (file) => {
  file = file.split(path.sep)[file.split(path.sep).length - 1];
  const libName = file.split(/-\d+\./)[0];
  const version = file.replace(`${libName}-`, '').replace('.tgz', '');
  return version;
}

const getLocalRegistry = () => {
  if (!fs.existsSync(LOCAL_REGISTRY_PATH)) {
    fs.mkdirSync(LOCAL_REGISTRY_PATH);
  }
  const subdirs = fs.readdirSync(LOCAL_REGISTRY_PATH);
  const objs = subdirs.map(file => {
    if (file.endsWith('.tgz')) {
      const libName = file.split(/-\d+\./)[0];
      const version = file.replace(`${libName}-`, '').replace('.tgz', '');
      return {
        name: libName,
        version,
        absolutePath: `${LOCAL_REGISTRY_PATH}/${file}`
      }
    }
    return null;
  }).filter(el => !!el);
  const libs = [];
  objs.forEach(lib => {
    const exLib = libs.find(el => el.name === lib.name);
    if (!exLib) {
      libs.push({
        name: lib.name,
        versions: [
          {
            version: lib.version,
            absolutePath: lib.absolutePath
          }
        ]
      });
    } else {
      exLib.versions.push({
        version: lib.version,
        absolutePath: lib.absolutePath
      })
    }
  });
  return libs;
}

const useLocalRegistry = async (packagePath, emergencyAction = 'none') => {
  if (!fs.existsSync(packagePath)) {
    console.error(`File ${packagePath} not extists!`);
    return;
  }

  if (!['none', 'next', 'latest'].includes(emergencyAction)) {
    console.error(`Priority should be one of: [none, next, latest]`);
    return
  }

  const pkg = utils.readJSON(packagePath);
  const localRegistry = getLocalRegistry();
  if (pkg.dependencies) {
    for (const pkgDep of Object.entries(pkg.dependencies)) {
      let [name, version] = pkgDep;
      if (!version.startsWith('file:') && _isFH(name)) {
        let desiredVersion = `file:${LOCAL_REGISTRY_PATH}${path.sep}${name}-${version}.tgz`;
        let canChange = true;
        version = version.replace('^', '');
        let pkgExists = true;
        let lPkgObjTemp = localRegistry.find(lpkg => lpkg.name === name);
        let lPkgObj;
        if (!lPkgObjTemp) {
          pkgExists = false;
        } else {
          lPkgObj = lPkgObjTemp.versions.find(lpkg => lpkg.version === version);
          if (!lPkgObj) {
            pkgExists = false;
          }
        }
        if (!pkgExists) {
          const remoteData = await network.get({
            address: `${REGISTRY_PATH}/${name}`,
          });
          let remoteVersion = remoteData.versions[version];
          if (!remoteVersion) {
            console.error(`Version ${version} of package ${name} not exists localy or remotely!`);
            if (emergencyAction === 'none') {
              console.log('leaving as it is!');
              canChange = false;
            } else if (['next', 'latest'].includes(emergencyAction)) {
              const v = remoteData['dist-tags'][emergencyAction];
              if (!v) {
                console.log('leaving as it is!');
                canChange = false;
              } else {
                remoteVersion = remoteData.versions[v];
                if (!remoteVersion) {
                  console.log('leaving as it is!');
                  canChange = false;
                } else {
                  desiredVersion = `file:${LOCAL_REGISTRY_PATH}${path.sep}${name}-${v}.tgz`
                  const pkgUrl = remoteVersion.dist.tarball;
                  console.log(`Downloading (${emergencyAction}) ${name}@${v}`);
                  await network.download(pkgUrl, LOCAL_REGISTRY_PATH, `${name}-${v}.tgz`);
                }
              }
            }
          } else {
            const pkgUrl = remoteVersion.dist.tarball;
            console.log(`Downloading ${name}@${version}`);
            await network.download(pkgUrl, LOCAL_REGISTRY_PATH, `${name}-${version}.tgz`);
          }
        }
        if (canChange) {
          pkg.dependencies[name] = desiredVersion;
        }
      }
    }
  }
  utils.writeJSON(packagePath, pkg);
}

const useRemoteRegistry = async (packagePath, fallback = 'none') => {
  if (!fs.existsSync(packagePath)) {
    console.error(`File ${packagePath} not extists!`);
    return;
  }

  const pkg = utils.readJSON(packagePath);
  if (pkg.dependencies) {
    for (const pkgDep of Object.entries(pkg.dependencies)) {
      let [name, version] = pkgDep;
      if (version.startsWith('file:') && _isFH(name)) {
        const v = _extractVersionFromFileName(version);
        const remoteData = await network.get({
          address: `${REGISTRY_PATH}/${name}`,
        });
        if (!!remoteData.versions[v]) {
          pkg.dependencies[name] = v;
        } else if (fallback !== 'none') {
          const newV = remoteData['dist-tags'][fallback];
          if (!!remoteData.versions[newV]) {
            pkg.dependencies[name] = newV;
          }
        }
      }
    }
  }
  
  utils.writeJSON(packagePath, pkg);
}

const localDeploy = (packagePath, timestamp, address, verbose) => {
  if (!fs.existsSync(packagePath)) {
    console.error(`File ${packagePath} not extists!`);
    return;
  }
  const pkg = utils.readJSON(packagePath);
  let actV;
  if (pkg.version.includes('-SNAPSHOT')) {
    actV = pkg.version.split('-SNAPSHOT')[0];
  } else {
    actV = pkg.version;
  }
  pkg.version = `${actV}-SNAPSHOT-${timestamp}`;
  utils.writeJSON(packagePath, pkg);

  const dirPath = packagePath.replace('package.json', '');
  process.chdir(dirPath);
  utils.runProcess(`npm install --registry ${address || REGISTRY_PATH}`, verbose);
  utils.runProcess(`npm pack`, verbose);
  const pkgName = `${pkg.name}-${pkg.version}.tgz`
  const tgzSourcePath = `${dirPath}${path.sep}${pkgName}`;
  const tgzDestPath = `${LOCAL_REGISTRY_PATH}${path.sep}${pkgName}`;

  if(require('os').platform() === 'win32') {
    utils.runProcess(`COPY "${tgzSourcePath}" "${tgzDestPath}"`, verbose);
    utils.runProcess(`del /f "${tgzSourcePath}"`, verbose);
  } else {
    utils.runProcess(`cp ${tgzSourcePath} ${tgzDestPath}`, verbose);
    utils.runProcess(`rm ${tgzSourcePath}`, verbose);
  }

  return {name: pkg.name, version: tgzDestPath};
}

const updateDependencies = ({name, version}, pkgs) => {
  for (const pkg of pkgs) {
    if (!fs.existsSync(pkg)) {
      console.error(`File ${pkg} not extists!`);
      return;
    }
  
    const pkgJSON = utils.readJSON(pkg);
    if (pkgJSON.dependencies && pkgJSON.dependencies[name]) {
      pkgJSON.dependencies[name] = `file:${version}`;
    }
    utils.writeJSON(pkg, pkgJSON);
  }
}

const updateToNewestVersion = async (packagePath, fallback = 'next') => {
  if (!fs.existsSync(packagePath)) {
    console.error(`File ${packagePath} not extists!`);
    return;
  }

  const pkg = utils.readJSON(packagePath);
  if (pkg.dependencies) {
    for (const name of Object.keys(pkg.dependencies)) {
      if (_isFH(name)) {
        const remoteData = await network.get({
          address: `${REGISTRY_PATH}/${name}`,
        });
        const newV = remoteData['dist-tags'][fallback];
        if (!!remoteData.versions[newV]) {
          pkg.dependencies[name] = newV;
        } else {
          console.log(`There is no version @${fallback} for lib ${name}`)
        }
      }
    }
  }
  utils.writeJSON(packagePath, pkg);
}

module.exports = {
  LOCAL_REGISTRY_PATH,
  getLocalRegistry,
  useLocalRegistry,
  useRemoteRegistry,
  localDeploy,
  updateDependencies,
  updateToNewestVersion
};