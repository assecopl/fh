const utils = require('./Utils');
const registry = require('./Registry');
const packages_list = require('../data/packages_list.json');
const pth = require('path');
const fs = require('fs');

const helpTable = {
  switchToLocal: `
This command swithces all fh paths to local ones.
Supported params: 
--priority none|next|latest - setups action priority of non existing component,
                              Means: when switch will not find local version of 
                              dependency lib and it will be not available on remote 
                              registry, then it will try:
                                
                                none - do nothing, just skip,
                                next - download version tagged as next,
                                latest - download version tagged as latest,
--verbose - enables verbose log
  `,

  switchToRemote: `
This command swithces all fh paths to remote ones.
Supported params: 
--verbose - enables verbose log
--priority none|next|latest - setups action priority of non existing remotely component,
                              Means: when switch will not find remote version of 
                              dependency lib registry, then it will:
                                
                                none - do nothing, just skip,
                                next - download version tagged as next,
                                latest - download version tagged as latest,
  `,

updateToNewestVersion: `
  This command update all fh paths to remote ones with provided tag.
  Supported params: 
  --verbose - enables verbose log
  --priority next|latest - setups action priority of non existing remotely component,
                                Means: when switch will not find remote version of 
                                dependency lib registry, then it will:
                                  
                                next - download version tagged as next,
                                latest - download version tagged as latest,
    `,

  deploy: `
This command deploys all packages to npm repository.
Supported params: 
--address={verdaccio address} - default is set to npmjs registry,
--fhVer={new fh version} - if not setted will be getted from registry,
--prod - setup as prod
--snap - setup as snapshot
--verbose - enables verbose log
--dryRun - enables test build without publish
--noPublish - disable publish process
  `,

  deployLocal: `
This command deploys all packages to local registry.
Supported params: 
--address={verdaccio address} - default is set to npmjs registry,
--verbose - enables verbose log
  `,

  clearLocalRegistry: `
This command clears local registry.
Supported params: 
--verbose - enables verbose log
  `

}

const _switchLerna = (disable) => {
    console.log('cwd', process.cwd());
    const disabledPath = '../packages/disabled-package.json';
    const enabledPath = '../packages/package.json';
    const isDisabled = fs.existsSync(disabledPath);
    console.log(isDisabled, disable)
    if (isDisabled && !disable) {
        fs.renameSync(disabledPath, enabledPath);
    } else if (!isDisabled && disable) {
        fs.renameSync(enabledPath, disabledPath);
    }
}

class Runtime {
  constructor() {
    const nodeV = process.version.match(/^v(\d+)/)[1];
    if (nodeV !== '18') {
      throw new Error('Please use node js in version 18!');
    }
  }

  _run(processName='help', params) {
    const fn = this[processName];
    if (fn) {
      if (params.has('help')) {
        this._help(params.processName);
      } else {
        fn(params);
      }
    }
  }

  _help(pName) {
    console.log(helpTable[pName]);
  }



  deploy(params) {
    _switchLerna(true);
    try {
        const MAIN_PATH = process.cwd();
        const FH_DIRS = packages_list.dirs;
        const FH_PACKAGES = packages_list.pkgs;
        const snapId = +new Date();
        let address = 'https://registry.npmjs.org/';
        if (params.has('address')) {
          if (utils.validURL(params.get('address'))) {
            address = params.get('address');
          } else {
            console.error(`Address ${params.get('address')} is invalid!`);
            return;
          }
        }

        let fhVer = params.get('fhVer');
        const isProd = params.has('prod');
        let isSnapshot = params.has('snap');
        const verbose =  params.has('verbose');
        if (!isProd && !isSnapshot) {
          isSnapshot = true;
        }

        if (isProd) {
          if (!fhVer) {
            const cPack = JSON.parse(fs.readFileSync(`${process.cwd()}/${FH_DIRS[0]}package.json`));
            fhVer = cPack.version.split('-')[0];
          }
        }

        if (isSnapshot) {
          if (!fhVer) {
            const cPack = JSON.parse(fs.readFileSync(`${process.cwd()}/${FH_DIRS[0]}package.json`));
            console.log(cPack);
            fhVer = cPack.version.split('-')[0];
          }
          fhVer = `${fhVer}-SNAPSHOT-${snapId}`;
        }

        let shouldPublish = true;
        if (params.has('dryRun')) {
          shouldPublish = false;
        }

        const publishProcess = (path, onlyUpdate) => {
          process.chdir(pth.join(MAIN_PATH, path));
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
          utils.runProcess('rm -fr node_modules package-lock.json', verbose);
          if (shouldPublish && !onlyUpdate) {
            console.log(process.cwd())
            utils.runProcess(`npm install --registry ${address}`, verbose);

            let tag;
            if (isSnapshot) {
              tag = 'next';
            } else if (isProd) {
              tag = 'latest';
            }

            utils.runProcess(`npm run build`, verbose);
            if (!params.has('noPublish')) {
              utils.runProcess(`npm publish --force --tag ${tag} --registry ${address}`, verbose);
            }
            utils.runProcess('rm -fr node_modules package-lock.json', verbose);
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

        const SKIP_PUBLISH_FH_DIRS = [
          '../packages/applications/default-application/'
        ]

        for (const path of FH_DIRS) {
          publishProcess(path, SKIP_PUBLISH_FH_DIRS.includes(path));
        }

        console.log(`NEW FH VERSION: ${fhVer}`);
    } catch (e) {
        console.error(e);
    } finally {
        _switchLerna(false);
    }
  }

  async switchToLocal(params) {
    const pkgs = await utils.getPackages('../', params.has('verbose'));
    for (const pkg of pkgs) {
      console.log(`Switching ${pkg} to local registry...`);
      await registry.useLocalRegistry(pkg, params.get('priority'));
      console.log(`Switching ${pkg} to local registry - DONE!`);
    }
  }

  async switchToRemote(params) {
    const pkgs = await utils.getPackages('../', params.has('verbose'));
    console.log('\n');
    for (const pkg of pkgs) {
      console.log(`Switching ${pkg} to remote registry...`);
      await registry.useRemoteRegistry(pkg, params.get('priority'));
      console.log(`Switching ${pkg} to remote registry - DONE!`);
    }
  }

  async deployLocal(params) {
    const pkgs = await utils.getPackages('../', params.has('verbose'));
    const tstmp = +new Date();
    const paths = packages_list.dirs.map(path => pth.join(process.cwd(), path));
    console.log(paths);
    for (const path of paths) {
      console.log(`deployng local package on path (${path})`)
      const result = registry.localDeploy(`${path}${pth.sep}package.json`, tstmp, params.get('address'), params.has('verbose'));
      registry.updateDependencies(result, pkgs);
    }
  }

  async updateToNewestVersion(params) {
    const pkgs = await utils.getPackages('../', params.has('verbose'));
    console.log('\n');
    if (!['next', 'latest'].includes(params.get('priority')) || params.get('priority') === undefined) {
      console.log(`--priority is wrong! Possible values are [next, latest]`);
      return;
    }
    for (const pkg of pkgs) {
      console.log(`Switching ${pkg} to remote registry...`);
      await registry.updateToNewestVersion(pkg, params.get('priority'));
      console.log(`Switching ${pkg} to remote registry - DONE!`);
    }
  }

  clearLocalRegistry(params) {
    console.log(`Deleting registry file on path ${registry.LOCAL_REGISTRY_PATH}`);
    utils.runProcess(`rm -fr ${registry.LOCAL_REGISTRY_PATH}/*`, params.has('verbose'));
    console.log('Done!')
  }
}

module.exports = new Runtime();