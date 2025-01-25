module.exports = function (config) {
  config.set({
      browsers: ['ChromeHeadless'],
      basePath: 'out/test-ci', // this is the same as the base-path of `:output-to` in `shadow-cljs.edn`
      files: ['ci.js'], // this is the same as the file-name (ending with .js) of `:output-to` in `shadow-cljs.edn`
      frameworks: ['cljs-test'],
      concurrency: 1,
      plugins: ['karma-cljs-test', 'karma-chrome-launcher'],
      colors: true,
      logLevel: config.LOG_DEBUG,
      client: {
        args: ["shadow.test.karma.init"],
        singleRun: true
      },
  })
};
