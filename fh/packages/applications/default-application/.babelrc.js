module.exports = {
    plugins: [
        ["transform-es2015-template-literals", {
            "loose": true,
            "spec": true
        }]
    ],
    sourceType: 'unambiguous',
    presets: [['@babel/preset-env', {
        debug: true,
        modules: false,
        targets: {
            browsers: [
                'IE 11',
                'last 3 Firefox versions',
                'last 3 Chrome versions',
                'last 3 Edge versions',
                'Firefox ESR'
            ]
        },
        useBuiltIns: 'usage',
        corejs: 2
    }]]
};
