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
  - visualizers can be easily extended. you can access the entire npm + clojurescript ecosystem.

- visualize edn datastructures (either from a repl or in the web-app (via scratchpad) 

Whichever method you use to start goldly, you should reach it at [`http://localhost:8080/`](http://localhost:8080/).

## goldly docs - in your project 

You need to include the **goldly-docs** artefact, which already includes many ui-renderers 
and contains a pre-built javascript bundle. In all other ways it is identical to goldly.

You can:
- add custom pages to goldly-docs easily.
- use your clj functions to render ui visualizations.

- [demo goldly](https://github.com/pink-gorilla/demo-goldly) Please see goldly-demo for how to use it.
- [trateg](https://github.com/clojure-quant/trateg) quantitative backtesting framework
- [EDGAR](https://github.com/clojure-quant/edgar) visualise mutual fund holdings


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

Please see `bb tasks` for all available options. 


