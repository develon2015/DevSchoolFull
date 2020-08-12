const VueLoaderPlugin = require('vue-loader/lib/plugin');

const home = __dirname;
const src = home + '/src';
const dist = home + '/dist';
const config = {
    // mode: 'development',
    mode: 'production',
    entry: {
        bundle: src + '/index.js',
        import: src + '/import.js',
    },
    output: {
        filename: 'js/[name].js',
        path: dist,
    },
    module: {
        rules: [
            { test: /\.vue$/, loader: 'vue-loader', },
            { test: /\.css$/, use: [ 'vue-style-loader', 'css-loader', ], },
            { test: /\.(png|jpg|mp3|mp4|mkv|webm)$/, loader: 'url-loader?limit=6024&name=assets/[name].[ext]', },
            { test: /\.svg$/, loader: 'file-loader?name=assets/[name].[ext]', },
            // { test: /\.(woff2)$/, loader: 'file-loader?name=fonts/[name].[ext]', },
        ],
    },
    plugins: [ new VueLoaderPlugin(), ],
};
module.exports = config;