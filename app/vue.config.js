const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    configureWebpack: {
        // No need for splitting 最终打包文件为1，不分块
        optimization: {
            splitChunks: false
        }
    }
})