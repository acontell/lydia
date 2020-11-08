const path = require('path');

module.exports = {
    entry: './ui/main.js',
    output: {
        filename: 'main.js',
        path: path.resolve(__dirname, './lydia/src/main/resources/static/js')
    },
    mode: 'production',
    resolve: {
        alias: {
            vue: 'vue/dist/vue.js'
        }
    }
};
