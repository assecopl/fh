const Path = require('path');
const Webpack = require('webpack');
const Merge = require('webpack-merge');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
require("@babel/polyfill");
const { DuplicatesPlugin } = require("inspectpack/plugin");
const { StatsWriterPlugin } = require("webpack-stats-plugin");
const TerserPlugin = require('terser-webpack-plugin');
const WebpackBundleSizeAnalyzerPlugin = require('webpack-bundle-size-analyzer').WebpackBundleSizeAnalyzerPlugin;



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
                    {
                        loader: "sass-loader",
                        options: {
                          implementation: require("sass"),
                          sassOptions: {
                            fiber: false,
                          },
                        }
                    }
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
        stats: {
            all:undefined,
            source: true
        },
        plugins: [
            new NodePolyfillPlugin(),
            // Relative paths work(to output path), but absolute paths do not currently.
            new WebpackBundleSizeAnalyzerPlugin('../../../webpack_size.txt'),
            new StatsWriterPlugin({
                // Relative paths work(to output path), but absolute paths do not currently.
                filename: "../../../webpack_stats.json",
                fields: null
            }),
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

    if (isProductionMode) {
        return Merge(baseConfig, {
            mode: 'production',
            devtool: 'nosources-source-map',
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
            }
        });
    }

    return baseConfig;
};
