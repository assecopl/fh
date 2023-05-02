const { merge } = require('webpack-merge');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

module.exports = function (baseConfig) {
    return merge(baseConfig, {
        mode: 'production',
        devtool: 'source-map',
        plugins: [
            new OptimizeCSSAssetsPlugin({})
        ]
    });
};
