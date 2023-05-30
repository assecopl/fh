const {merge} = require('webpack-merge');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = function (baseConfig) {
    //delete baseConfig["devtool"]; // No source maps
    return merge(baseConfig, {
        mode: 'production',
        plugins: [
            new OptimizeCSSAssetsPlugin({})
        ],
        optimization: {
            minimize: true,
            minimizer: [new TerserPlugin({
                // https://github.com/terser/terser#minify-options
                terserOptions: {
                    toplevel: true,
                    keep_classnames: true,
                    keep_fnames: true,
                },
            })],
        },
    });
};
