# Goldly 
[![GitHub Actions status |pink-gorilla/goldly](https://github.com/pink-gorilla/goldly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/goldly/actions?workflow=CI)
[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly.svg)](https://clojars.org/org.pinkgorilla/goldly)

- goldly uses the sci clojurescript interpreter to create a clojurescript kernel.
- goldly uses webly lazy shadow-cljs lazy modules.

## goldly - in your  project **with custom ui-renderers**

You need to add the **goldly** artefact to build javascript bundle from scratch. 
This takes more time (npm dependencies have to be downloaded, javascript bundle needs to be compiled), 
but it allows you to add custom ui renderers to your goldly app.

This configures goldly with your set of ui-renderers.

You have to include the goldly and ui-renderer dependencies, and then add the
namespace of the ui-renderer to goldly/extensions. This allows goldly to 
add the ui extensions to the javascript bundle.

Have a look at [ui-binary-clock](https://github.com/pink-gorilla/ui-binary-clock) to
see how you can build a custom javascript js bundle with goldly. 



## for goldly developers 

Run inside cloned goldly git repo.

This option is mainly there for development of goldly. 
For regular use, the long compile-times are not really sensible.

```
cd goldly-test
clj -X:goldly-build:npm-install
clj -X:goldly-build:compile
clj -X:goldly
```

