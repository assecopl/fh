const fs = require('fs');
const path = require('path');
const ORIG_PKG_PATH = path.resolve(__dirname, 'package.json');
const CACHED_PKG_PATH = path.resolve(__dirname, 'cached-package.json');
const pkgData = require(ORIG_PKG_PATH);

fs.writeFile(CACHED_PKG_PATH, JSON.stringify(pkgData), function (err) {
    if (err) throw err;
});

pkgData.main = 'dist/Module.js';

fs.writeFile(ORIG_PKG_PATH, JSON.stringify(pkgData, null, 2), function (err) {
    if (err) throw err;
});
