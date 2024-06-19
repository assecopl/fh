const {merge} = require('webpack-merge');

module.exports = function (baseConfig) {
    return merge(baseConfig, {
        devtool: 'source-map'
    });
};
