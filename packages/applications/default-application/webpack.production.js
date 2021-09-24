const Merge = require('webpack-merge');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

module.exports = function (baseConfig) {
    return Merge(baseConfig, {
        mode: 'production',
        devtool: 'source-map',
        plugins: [
            new OptimizeCSSAssetsPlugin({})
        ]
    });
};
