/**
 * Main configuration file for webpack .
 * */

const Path = require('path');
const Webpack = require('webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const PostCssWrapper = require('postcss-wrapper-loader');
require('@babel/polyfill');
const Merge = require('webpack-merge');


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
        entry: ['@babel/polyfill', './Module.js'],
        mode: 'development',
        output: {
            path: Path.resolve('./../../../fhCoreLite/defaultApplication/target/classes/static'),
            filename: 'fhApplication.bundle.js'
        },
        module: {
            rules: [{
                test: /\.css$/,
//                exclude: [/node_modules/, /dist/, /build/],
                use: [{
                    loader: MiniCssExtractPlugin.loader,
                    options: {}
                }, {
                    loader: 'css-loader'
                }]
            }, {
                test: /\.(d.)?tsx?$/,
                exclude: [/node_modules/, /dist/, /build/],
                use: [{
                    loader: 'babel-loader'
                }, {
                    loader: 'ts-loader'
                }]
            }, {
                test: /\.js$/,
                exclude: [/node_modules/, /dist/, /build/],
                use: {
                    loader: 'babel-loader'
                }
            }, {
                test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'url-loader?limit=10000&mimetype=application/font-woff'
            }, {
                test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'url-loader?limit=10000&mimetype=application/octet-stream'
            }, {
                test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'file-loader'
            }, {
                test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
                loader: 'url-loader?limit=10000&mimetype=image/svg+xml'
            }, {
                test: /\.jpg$/,
                use: ["file-loader"]
            }, {
                test: /\.png$/,
                use: ["url-loader?mimetype=image/png"]
            }]
        },
        plugins: [
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
            })
        ]
    };

    /**
     * Add css wrapp plugin if needed.
     */
    if(cmdEnv.wrapped == 'true') {
        console.log("Adding wrapping css logic" , cmdEnv.wrapped);
        baseConfig =  Merge(baseConfig, {
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
