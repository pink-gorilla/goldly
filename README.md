# Goldly 
[![GitHub Actions status |pink-gorilla/goldly](https://github.com/pink-gorilla/goldly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/goldly/actions?workflow=CI)
[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly.svg)](https://clojars.org/org.pinkgorilla/goldly)
[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly-bundel.svg)](https://clojars.org/org.pinkgorilla/goldly-bundel)

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
- goldly systems and the clojurescript kernel can be developed/used in a [Pinkgorilla Notebook](https://github.com/pink-gorilla/notebook)

### use cases
- quickly write a web dashboards (by using only clojure). 
  - [EDGAR](https://github.com/clojure-quant/edgar) uses it to vizualise mutual fund holdings
  - [trateg](https://github.com/clojure-quant/trateg) uses it to vizualize swing charts
- visualize edn datastructures (either from a repl or in the web-app (via scratchpad) 


### Web Interface

Whichever method you use to start goldly, you should reach it at [`http://localhost:8000/`](http://localhost:8000/).

- In the browser window click on `running systems` and then `snippet registry`
- You are able to click on all the systems in he registry.

The snippets are primitive, but demonstrate certain features of goldly:
- hello: demonstrates the simplest hiccup rendering usecase
- click-counter demonstrates dynamic ui intteractions.
- greeter: shows how to create links from one system to another; this can be 
  used for master-detail type of navigation.
- fortune: demonstrates how to load data from clojure (could be a database)
- time: demonstrates to push data from clojure

## Run - demo

Clone this repo, then:

```
cd profiles/demo
clojure -X:goldly
```

Open web-browser on port 8000.


## Run - standalone 

The easiest way to run the notebook locally is leveraging the `clojure` cli

```
clojure -Sdeps '{:deps {org.pinkgorilla/goldly-bundel {:mvn/version "RELEASE"}}}' -m bundel.run
```

This lets you see many snippets that you might like.

## Run - in your project **using readymade default ui renderers**

You need to include the **goldly-bundel** artefact, which ships a pre-built javascript 
bundle and already includes many ui-renderers. 

You can:
- add custom systems to goldly easily.
- use your clj functions to render ui visualizations.

### with tools.deps

Add this alias to your deps.edn:
```
 :goldly
  {:extra-deps {org.pinkgorilla/goldly-bundel {:mvn/version "RELEASE"}}
   :exec-fn goldly-bundel/run
   :exec-args {:config {:goldly {:systems [systems.snippet-registry  ; if you want snippet browser started
                                           systems.snippet-scratchpad
                                           demo.hello-user
                                              ]}}}}
```
then run it with `clojure -X:goldly`.

The source to the demo.hello-user system is in `src/demo`

An example of project that uses goldly this way is: [trateg](https://github.com/clojure-quant/trateg)


### with leiningen

In project.clj add the goldly-bundel dependency: `[org.pinkgorilla/goldly "0.2.78"]`
then add a goldly alias:

```
{:alias
   "goldly"
   ["run" "-m" "goldly-bundel.run" "goldly-user.edn"]}

```
Add a goldly-user.edn file:
```
{:goldly {:systems [systems.snippet-registry  ; if you want snippet browser started
                    systems.snippet-scratchpad
                    demo.hello-user]}}
```

run with: `lein goldly`

## Run - in your  project **with custom ui-renderers**

You need to add the **goldly** artefact to build javascript bundle from scratch. 
This takes more time (npm dependencies have to be downloaded, javascript bundle needs to be compiled), 
but it allows you to add custom ui renderers to your goldly app.

This configures goldly with your set of ui-renderers.

You have to include the goldly and ui-renderer dependencies, and then add the
namespace of the ui-renderer to goldly/extensions. This allows goldly to 
add the ui extensions to the javascript bundle.

This example adds gorilla-ui to goldly:

### for tools.deps
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

### for leiningen

See [demo-goldly-bundel](https://github.com/pink-gorilla/demo-goldly-bundel) for the complete project.
Add the alias to project.clj
```
{:alias
   "goldly"
   ["with-profile" "+goldly" "run" "-m" "goldly-server.app" "goldly-gorillaui.edn" "watch"]}

```
Add a goldly-gorillaui.edn:
```
{:goldly {}}
```

UI Extension [ui-vega](https://github.com/pink-gorilla/ui-vega) uses leiningen to run
goldly with a custom build bundel.

## Run - cloned git repo

This option is mainly there for development of goldly. 
For regular use, the long compile-times are not really sensible.

Clone this repo, then:

run goldly **with bundel ui-extensions**: `clojure -X:goldly`

run goldly **without ui-extensions** `clojure -X:goldly-core`

The source code of the registry systems is in `src/systems`.
The snippets are in `resources/snippets/`


Compile and run:

```
clojure -X:goldly :profile '"release"'
clojure -X:goldly :profile '"jetty"'
```

To test the bundel creation:
```
clojure -X:bundel-config
cd bundel
./bundel-compile.sh
clojure -X:run
```

# for goldly developers

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

# todo

Add more widgets, and have syntax similar to shiny:
http://gallery.htmlwidgets.org/

