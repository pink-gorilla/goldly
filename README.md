# Goldly 
[![GitHub Actions status |pink-gorilla/goldly](https://github.com/pink-gorilla/goldly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/goldly/actions?workflow=CI)
[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly.svg)](https://clojars.org/org.pinkgorilla/goldly)

- goldly uses the sci clojure(script) interpreter to create a clojurescript kernel.
- goldly uses webly to create lazy-loadable-js-modules via shadow-cljs.
- goldly has 3 sub-projects:
  - goldly-sci:  creates a sci-interpreter setup that works with shadow-cljs lazy modules.
  - reval-sci: implements a reval kernel (for cljs)
  - sci-configs: creates modules based on sci-configs namespace mappings.

## demo

The demo is mainly there for development of goldly. 

Clone goldly git repo.


```
cd demo
clj -X:webly:npm-install
clj -X:webly:compile
clj -X:nbeval
clj -X:webly:run

```
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



