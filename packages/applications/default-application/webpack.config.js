/**
 * Main configuration file for webpack .
 * */
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
const Path = require('path');
const Webpack = require('webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const PostCssWrapper = require('postcss-wrapper-loader');
require('@babel/polyfill');
const { merge } = require('webpack-merge');
const { DuplicatesPlugin } = require("inspectpack/plugin");
const { StatsWriterPlugin } = require("webpack-stats-plugin");
const WebpackBundleSizeAnalyzerPlugin = require('webpack-bundle-size-analyzer').WebpackBundleSizeAnalyzerPlugin;


/**
 * @param cmdEnv Object {env:string, wraped:boolean}
 * @returns {*}
 */
module.exports = function (cmdEnv) {
    console.log("Command line parameters", cmdEnv);
    console.log(`This is a ${(cmdEnv.env === 'production') ? "production" : "development"} build`);
    /**
     *  Base config for webpack-s builds. Development build use exactly this parameters. Other enviroments change or extend it.
     * @type {{mode: string, devtool: string, output: {path: *, filename: string}, entry: string[], resolve: {extensions: string[]}, plugins: *[], module: {rules: *[]}}}
     */
    let baseConfig = {
        entry: ['@babel/polyfill', './Module.ts'],
        mode: 'development',
        output: {
            path: Path.resolve('./../../../fhdp/fhdp-example/target/classes/static'),
            filename: 'fhApplication.bundle.js'
        },
        module: {
            rules: [{
                test: /\.css$/,
                use: [MiniCssExtractPlugin.loader,  'css-loader']
            }, {
                test: /\.ts$/,
                exclude: /node_modules/,
                use: ['babel-loader', {
                    loader: 'ts-loader',
                    options:{allowTsInNodeModules: true}
                }]
            }, {
                test: /\.js$/,
                use: ['babel-loader']
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
            },{
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
        stats: {
            all:undefined,
            source: true
        },
        plugins: [
            new NodePolyfillPlugin(),
            // Relative paths work(to output path), but absolute paths do not currently.
            new WebpackBundleSizeAnalyzerPlugin('./webpack_size.txt'),
            new StatsWriterPlugin({
                // Relative paths work(to output path), but absolute paths do not currently.
                filename: "./webpack_stats.json",
                fields: null
            }),
            new Webpack.ProvidePlugin({
                $: 'jquery',
                jQuery: 'jquery'
            }),
            new Webpack.DefinePlugin({
                ENV_IS_DEVELOPMENT: !(cmdEnv.env === 'production'),
                ENV_IS: JSON.stringify(cmdEnv.env)
            }),
            new MiniCssExtractPlugin({
                filename: 'fhApplication.bundle.css'
            }),
            new DuplicatesPlugin({
                // Emit compilation warning or error? (Default: `false`)
                emitErrors: false,
                // Handle all messages with handler function (`(report: string)`)
                // Overrides `emitErrors` output.
                emitHandler: undefined,
                // List of packages that can be ignored. (Default: `[]`)
                // - If a string, then a prefix match of `{$name}/` for each module.
                // - If a regex, then `.test(pattern)` which means you should add slashes
                //   where appropriate.
                //
                // **Note**: Uses posix paths for all matching (e.g., on windows `/` not `\`).
                ignoredPackages: undefined,
                // Display full duplicates information? (Default: `false`)
                verbose: true
            }),
        ],
        optimization: {
            minimize: false,
        }
    };

    /**
     * Add css wrapp plugin if needed.
     */
    if(cmdEnv.wrapped == 'true') {
        console.log("Adding wrapping css logic" , cmdEnv.wrapped);
        baseConfig =  merge(baseConfig, {
            plugins :[
                new PostCssWrapper('fhApplication.bundle.css', '#fhApplication')
            ]
        })
    }

    if (cmdEnv.env) {
        return require(`./webpack.${cmdEnv.env}.js`)(baseConfig)
    } else {
        return require(`./webpack.development.js`)(baseConfig)
    }
};
