# Goldly [![GitHub Actions status |pink-gorilla/goldly](https://github.com/pink-gorilla/goldly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/goldly/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly.svg)](https://clojars.org/org.pinkgorilla/goldly)

- goldly is a vizualization tool for clojure
- inspired by [R/shiny](https://shiny.rstudio.com/)
- brings many vizualizers 
  - vega and highcharts for 2d plotting
  - aggrid for tables/ grids
  - leaflet for geographic map
  - quil for 2d dynamic animations
  - vizualizers can be easily extended. you can access the entire npm + clojurescript ecosystem.
- uses the sci clojurescript interpreter for dynamic ui interactions at runtime
- load data from clojure (from the frontend app)

usecases
- visualize edn datastructures (either from a repl or in the web-app (via sratchpad) 
- quickly write a web dashboards (by using only clojure). 
  - [EDGAR](https://github.com/clojure-quant/edgar) uses it to vizualise mutual fund holdings
  - [trateg](https://github.com/clojure-quant/trateg) uses it to vizualize swing charts
- goldly systems can be used in a [Notebook](https://github.com/pink-gorilla/gorilla-notebook).


# How to setup goldly

You have two options:
- use goldly to build javascript bundle from scratch. 
  This takes more time (everything needs to be compiled), 
  but it allows ou to add custom ui renderers to your goldly app
- use [goldly bundel](https://github.com/pink-gorilla/goldly-bundel) which ships
  a pre-built javascript budle and already includes many ui-renderers.


## Goldly with custom ui-renderers in your project:

This configures goldly with your set of ui-renderers.
This example adds gorilla-ui to goldly:

Add this alias to your deps.edn:

```
 :goldly
  {:extra-deps {org.pinkgorilla/goldly {:mvn/version "0.2.39"}
                org.pinkgorilla/gorilla-ui {:mvn/version "0.3.21"}}
   :exec-fn goldly-server.app/goldly-server-run!
   :exec-args {:profile "watch"
               :config {:goldly {:extensions [[pinkgorilla.ui.goldly]
                                              ]}}}}
```

You have to include the ui-renderer dependencies, and then add the
namespace of the ui-renderer to goldly/extensions. This allows goldly to 
add the ui extensions to the javascript bundle.

# run the demo (in this project)

To run goldly with some demonstration systems

```
lein goldly watch    ; runs a webserver on port 8000.
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


# send data from the repl

