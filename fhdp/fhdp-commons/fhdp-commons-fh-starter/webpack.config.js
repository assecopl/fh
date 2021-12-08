const Path = require('path');
const Webpack = require('webpack');
const Merge = require('webpack-merge');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
require("@babel/polyfill");

module.exports = function (env) {
    const isProductionMode = env.envMode === 'production';
    const isTestReg = env.testReg === 'tak';
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
                    {
                        loader: "css-loader",
                        options: {
                            minimize: {
                                safe: true
                            }
                        }
                    },
                    {
                        loader: "sass-loader",
                        options: {}
                    }
                ]
            }, {
                test: /\.ts(x?)$/,
                // exclude: /node_modules/,
                use: [{
                    loader: 'babel-loader'
                }, {
                    loader: 'ts-loader'
                }]
            }, {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader'
                }
            }, {
                test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'file-loader?name=./fonts/[name].[ext]'
            }, {
                test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'file-loader?name=./fonts/[name].[ext]'
            }, {
                test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'file-loader?name=./img/[name].[ext]'
            }, {
                test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'file-loader?name=./img/[name].[ext]'
            }, {
                test: /\.jpg$/,
                use: ["file-loader?name=./img/[name].[ext]"]
            }, {
                test: /\.png$/,
                use: ["file-loader?name=./img/[name].[ext]"]
            }, {
                test: /jquery-mousewheel/,
                loader: 'imports-loader?define=>false&this=>window'
            }]
        },
        resolve: {
            extensions: ['.ts', '.tsx', '.js']
        },
        plugins: [
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
