const Path = require('path');
const Webpack = require('webpack');
const Merge = require('webpack-merge');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
require("@babel/polyfill");

module.exports = function (env) {
    const isProductionMode = false;//env.envMode !== 'development';
    console.log(`This is a ${isProductionMode ? "production" : "development"} build`);
    var entry = ['@babel/polyfill','./src/main/resources/static/Application.ts'];
    let baseConfig = {
        entry: entry,
        mode: 'development',
        devtool: 'source-map',
        output: {
            path: Path.resolve('./target/classes/static'),
            filename: 'fhApplication.bundle.js'
        },
        module: {
            rules: [{
                test: /\.(scss|css)$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    "css-loader",
                    "sass-loader"
                ]
            }, {
                test: /\.ts(x?)$/,
                exclude: /node_modules/,
                use: ['babel-loader', {
                    loader: 'ts-loader',
                    options:{allowTsInNodeModules: true}
                }]
            }, {
                test: /\.js$/,
                use: ['babel-loader',
            ]
            }, {
                test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/,
                type: 'asset'
            }, {
                test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
                type: 'asset'
            }, {
                test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,
                type: 'asset'
            }, {
                test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
                type: 'asset'
            }, {
                test: /\.jpg$/,
                type: 'asset'
            }, {
                test: /\.png$/,
                type: 'asset'
            }, {
                test: /jquery-mousewheel/,
                use: [
                    {
                        loader: "imports-loader",
                        options: {
                           wrapper:"window",
                            additionalCode:
                                "var define = false; /* Disable AMD for misbehaving libraries */",
                        },
                    },
                ]
            }]
        },
        resolve: {
            extensions: ['.ts', '.tsx', '.js']
        },
        plugins: [
            new NodePolyfillPlugin(),
            new Webpack.ProvidePlugin({
                $: 'jquery',
                jQuery: 'jquery'
            }),
            new Webpack.DefinePlugin({
                ENV_IS_DEVELOPMENT: !isProductionMode,
                ENV_IS: JSON.stringify(env)
            }),
            new MiniCssExtractPlugin({
                filename: 'fhApplication.bundle.css'
            })
        ]
    };

    if (isProductionMode) {
        return Merge(baseConfig, {
            mode: 'production',
            devtool: 'nosources-source-map',
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
    }

    return baseConfig;
};
