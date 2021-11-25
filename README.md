# Goldly 
[![GitHub Actions status |pink-gorilla/goldly](https://github.com/pink-gorilla/goldly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/goldly/actions?workflow=CI)
[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly.svg)](https://clojars.org/org.pinkgorilla/goldly)
[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly-docs.svg)](https://clojars.org/org.pinkgorilla/goldly-docs)

- goldly lets you create interactive visualization in a webbrowser in clojure
- visualizations can interact with the clojure server.
- inspired by [R/shiny](https://shiny.rstudio.com/)
- uses the sci clojurescript interpreter for a clojurescript kernel
- brings many visualizers 
  - vega and highcharts for 2d plotting
  - aggrid for tables/ grids
  - leaflet for geographic map
  - quil for 2d dynamic animations
- visualizers can be easily extended. you can access the entire npm + clojurescript ecosystem.
  An example for a simple ui extension is [ui-binaryclock](https://github.com/pink-gorilla/ui-binary-clock)
- visualize edn datastructures (either from a repl or in the web-app (via scratchpad) 

## demo apps

- [demo goldly](https://github.com/pink-gorilla/demo-goldly)
  **NEWBEES: START WITH DEMO-GOLDLY!**
- [trateg](https://github.com/clojure-quant/trateg) quantitative backtesting framework
- [EDGAR](https://github.com/clojure-quant/edgar) visualise mutual fund holdings


### Web Interface

Whichever method you use to start goldly, you should reach it at [`http://localhost:8080/`](http://localhost:8080/).



## Run - standalone 

The easiest way to run the goldly locally is leveraging the `clojure` cli

```
clojure -Sdeps '{:deps {org.pinkgorilla/goldly-docs {:mvn/version "RELEASE"}}}' -m bundel.run
```

This lets you see many snippets that you might like.

## Run - in your project **using readymade default ui renderers**

You need to include the **goldly-docs** artefact, which ships a pre-built javascript 
bundle and already includes many ui-renderers. 

You can:
- add custom systems to goldly easily.
- use your clj functions to render ui visualizations.

Add this alias to your deps.edn:
```
 :goldly
  {:extra-deps {org.pinkgorilla/goldly-docs {:mvn/version "RELEASE"}}
   :exec-fn goldly-docs/run
   :exec-args {:config nil}}}
```
then run it with `clojure -X:goldly-docs`.

The source to the demo.hello-user system is in `src/demo`

An example of project that uses goldly this way is: [trateg](https://github.com/clojure-quant/trateg)

## Run - in your  project **with custom ui-renderers**

You need to add the **goldly** artefact to build javascript bundle from scratch. 
This takes more time (npm dependencies have to be downloaded, javascript bundle needs to be compiled), 
but it allows you to add custom ui renderers to your goldly app.

This configures goldly with your set of ui-renderers.

You have to include the goldly and ui-renderer dependencies, and then add the
namespace of the ui-renderer to goldly/extensions. This allows goldly to 
add the ui extensions to the javascript bundle.

This example adds gorilla-ui to goldly:

Add this alias to your deps.edn:

```
 :goldly
  {:extra-deps {org.pinkgorilla/goldly {:mvn/version "RELEASE"}
                org.pinkgorilla/ui-gorilla {:mvn/version "RELEASE"}}
   :exec-fn goldly-server.app/goldly-server-run!
   :exec-args {:profile "watch"
               :config {:goldly {}}}}
```

[ui-binary-clock](https://github.com/pink-gorilla/ui-binary-clock) and
[ui-quil](https://github.com/pink-gorilla/ui-quil)
both use deps.edn to build a custom goldly bundel (that includes the library that gets built).


## for goldly developers 

Run inside cloned goldly git repo.

This option is mainly there for development of goldly. 
For regular use, the long compile-times are not really sensible.

Clone this repo, then:

run goldly **with bundel ui-extensions**: `clojure -X:goldly`

run goldly **without ui-extensions** `clojure -X:goldly-core`


Compile and run:

```
clojure -X:goldly :profile '"release"'
clojure -X:goldly :profile '"jetty"'
```

To test the docs creation:
```
clojure -X:docs-config
cd profiles/docs
./docs-compile.sh
clojure -X:run
```

core
```
  clojure -X:goldly              - watch
  ./script/clean.sh

  ./script/compile_run.sh        - release
  ./script/clean.sh

  ./script/compile_adv_run.sh    - release-adv
  ./script/clean.sh
```

test
```
  cd profiles/test
  clojure -X:goldly              - watch
```

# API

You can get and set the scratchpad data via http api.
Please execute `./script/scratchpad-get.sh` or `./script/scratchpad-set.sh`
