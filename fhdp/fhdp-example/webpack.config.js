const Path = require('path');
const Webpack = require('webpack');
const {merge} = require('webpack-merge');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const TerserPlugin = require('terser-webpack-plugin');
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
const { StatsWriterPlugin } = require("webpack-stats-plugin");
const { DuplicatesPlugin } = require("inspectpack/plugin");
const WebpackBundleSizeAnalyzerPlugin = require('webpack-bundle-size-analyzer').WebpackBundleSizeAnalyzerPlugin;

require("@babel/polyfill");

module.exports = function (env) {
    const isProductionMode = env.env === 'production';
    console.log(`This is a ${isProductionMode ? "production" : "development"} build`);
    const packagesVersion = process.env.npm_package_version;
    console.log(`packagesVersion = ${packagesVersion}`);

    var entry = ['@babel/polyfill', './src/main/resources/static/Application.ts'];
    let baseConfig = {
        entry: entry,
        mode: 'development',
        devtool: 'inline-source-map',
        output: {
            path: Path.resolve('./target/classes/META-INF/resources/lib'),
            filename: '[name].js'
            // filename: 'lib/singlewindow_'+packagesVersion+'.js'
        },
        stats: {
            all:undefined,
            source: true
        },
        module: {
            rules: [{
                test: /\.(scss|css)$/,
                exclude: /node_modules/,
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
                use: [{
                    loader: 'babel-loader'
                }, {
                    loader: 'ts-loader',
                    options:{allowTsInNodeModules: true}
                }]
            }, {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader'
                }
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
            }
            ]
        },
        resolve: {
            extensions: ['.ts', '.tsx', '.js']
        },
        plugins: [
            new NodePolyfillPlugin(),
            new StatsWriterPlugin({
                filename: "../../../../../webpack_stats.json",
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
                filename: '[name].[chunkhash].bundle.css',
                chunkFilename: '[id].[chunkhash].css'
            
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
            new WebpackBundleSizeAnalyzerPlugin('./../../../../../webpack_size.txt')
        ],
        // optimization: {
        //     minimize: false,
        //     splitChunks: {
        //         cacheGroups: {
        //             commons: {
        //                 test: /[\\/]node_modules[\\/]/,
        //                 // test(module) {
        //                 //
        //                 //     // Only node_modules are needed
        //                 //     if (!module.context || !module.context.includes('node_modules')) {
        //                 //         return false;
        //                 //     }
        //                 //     // But not node modules that contain these key words in the path
        //                 //     if ([ 'pkwd-controls', 'pkwd-extenders', 'pkwd-charts', 'fh-basic-controls'].some(str => module.context.includes(str))) {
        //                 //         return false;
        //                 //     }
        //                 //     console.log("Vendor included module "+ module.context)
        //                 //     return true;
        //                 // },
        //                 name: 'vendors',
        //                 chunks: 'all'
        //             }
        //         },
        //     }
        // }
    };

    if (isProductionMode) {
        delete baseConfig["devtool"];
        baseConfig = merge(baseConfig, {
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
                    exclude: /node_modules/
                })],
            },
        });
    }

    return baseConfig;
};
