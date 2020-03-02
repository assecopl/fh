const Merge = require('webpack-merge');

module.exports = function (baseConfig) {
        return Merge(baseConfig, {
                devtool: 'eval-source-map'
        });
};
