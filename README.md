# Goldly 
[![GitHub Actions status |pink-gorilla/goldly](https://github.com/pink-gorilla/goldly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/goldly/actions?workflow=CI)
[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly.svg)](https://clojars.org/org.pinkgorilla/goldly)
[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly-bundel.svg)](https://clojars.org/org.pinkgorilla/goldly-bundel)

- goldly is a vizualization tool for clojure
- inspired by [R/shiny](https://shiny.rstudio.com/)
- brings many vizualizers 
  - vega and highcharts for 2d plotting
  - aggrid for tables/ grids
  - leaflet for geographic map
  - quil for 2d dynamic animations
  - vizualizers can be easily extended. you can access the entire npm + clojurescript ecosystem.
    An example for a simple ui extension is [ui-binaryclock](https://github.com/pink-gorilla/ui-binary-clock)
- uses the sci clojurescript interpreter for dynamic ui interactions at runtime
- load data from clojure (from the frontend app)

**use cases**
- visualize edn datastructures (either from a repl or in the web-app (via sratchpad) 
- quickly write a web dashboards (by using only clojure). 
  - [EDGAR](https://github.com/clojure-quant/edgar) uses it to vizualise mutual fund holdings
  - [trateg](https://github.com/clojure-quant/trateg) uses it to vizualize swing charts
- goldly systems can be used in a [Notebook](https://github.com/pink-gorilla/gorilla-notebook).

# run the demo (in this project)

**run goldly without ui-extensions**

```
clojure -X:goldly
```

- Then open browser `http://localhost:8000`
- In the browser window click on `running systems` and then `snippet registry`
- You are able to click on all the systems in he registry.

The source code of the registry systems is in `src/systems`.
The snippets are in `resources/snippets/`

The snippets are primitive, but demonstrate certain features of goldly:
- hello: demonstrates the simplest hiccup rendering usecase
- click-counter demonstrates dynamic ui intteractions.
- greeter: shows how to create links from one system to another; this can be 
  used for master-detail type of navigation.
- fortune: demonstrates how to load data from clojure (could be a database)
- time: demonstrates to push data from clojure

**run goldly with bundel ui-extensions**

```
clojure -X:goldly-bundel
```

This is the same config as in goldly-bundel, but the javascript bundel is generated on the fly. 
It gives you a lot more snippets.



# Use goldly in your project

You can include one of two artefacts:
- **goldly-bundel** dependency, which ships a pre-built javascript bundle and already includes many ui-renderers. 
- **goldly** to build javascript bundle from scratch. 
  This takes more time (npm dependencies have to be downloaded, javascript bundle needs to be compiled), 
  but it allows you to add custom ui renderers to your goldly app.

The two artefacts are completely identical to use.

## Setup Goldly-Bundel

To start the goldly via goldly-bundel:

```
clojure -Sdeps '{:deps {org.pinkgorilla/goldly-bundel {:mvn/version "RELEASE"}}}' -m bundel.run
```

## Setup Goldly with custom ui-renderers:

This configures goldly with your set of ui-renderers.

You have to include the goldly and ui-renderer dependencies, and then add the
namespace of the ui-renderer to goldly/extensions. This allows goldly to 
add the ui extensions to the javascript bundle.

This example adds gorilla-ui to goldly:

**for tools.deps**
Add this alias to your deps.edn:

```
 :goldly
  {:extra-deps {org.pinkgorilla/goldly {:mvn/version "RELEASE"}
                org.pinkgorilla/gorilla-ui {:mvn/version "RELEASE"}}
   :exec-fn goldly-server.app/goldly-server-run!
   :exec-args {:profile "watch"
               :config {:goldly {:extensions [[pinkgorilla.ui.goldly]
                                              ]}}}}
```

**for leiningen**

See [demo-goldly-bundel](https://github.com/pink-gorilla/demo-goldly-bundel) for the complete project-
Add the alias to project.clj
```
{:alias
   "goldly"
   ["with-profile" "+goldly" "run" "-m" "goldly-server.app" "watch" "goldly-gorillaui.edn"]}

```
Add a goldly-gorillaui.edn:
```
{:goldly {:extensions [[pinkgorilla.ui.goldly]]}}
```

# API

You can get and set the scratchpad data via http api.
Please execute `./script/scratchpad-get.sh` or `./script/scratchpad-set.sh`

