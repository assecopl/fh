const Merge = require('webpack-merge');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

module.exports = function (baseConfig) {
    return Merge(baseConfig, {
        mode: 'production',
        devtool: 'source-map',
        optimization: {
            minimizer: [
                new UglifyJsPlugin({
                    cache: true,
                    parallel: true,
                    sourceMap: true,
                    extractComments: true
                })
            ]
        },
        plugins: [
            new OptimizeCSSAssetsPlugin({})
        ]
    });
};
