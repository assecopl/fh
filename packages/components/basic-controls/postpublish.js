const fs = require('fs');
const path = require('path');
const ORIG_PKG_PATH = path.resolve(__dirname, 'package.json');
const CACHED_PKG_PATH = path.resolve(__dirname, 'cached-package.json');
const pkgData = JSON.stringify(require(CACHED_PKG_PATH), null, 2) + '\n';

fs.writeFile(ORIG_PKG_PATH, pkgData, function (err) {
    if (err) throw err;
});

fs.unlink(CACHED_PKG_PATH, function (err) {
    if (err) throw err;
});
