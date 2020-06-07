# goldly

 [![GitHub Actions status |pink-gorilla/goldly](https://github.com/pink-gorilla/goldly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/goldly/actions?workflow=CI)[![Codecov Project](https://codecov.io/gh/pink-gorilla/goldly/branch/master/graph/badge.svg)](https://codecov.io/gh/pink-gorilla/goldly)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly.svg)](https://clojars.org/org.pinkgorilla/goldly)


## What can goldly do for you?

- goldly allows you to quickly create data driven web applications 
  (dashboards, data-analysis)
- goldly takes care of running web server, client-server communication,
  and compiling your web app. 
- it is somehow similar to R shiny and python dash.
- via gorilla-ui it brings many ready-made components that you can use.

## try it yourself

```
  git clone https://github.com/pink-gorilla/goldly.git
  cd goldly
  lein goldly ; only run app with default systems
  lein demo   ; run demo systems
```

## use it in your project

Add [org.pinkgorilla/goldly "0.0.3"] to your project as a plugin

```
  lein goldly ./my-systems/
```

## Overview

- state is kept in cljs / reagent atom
  initial state is defined at system creation time from clj

- ui components (see gorilla-ui)
  
- widgets can manipulate state directly
  (combobox, radiobox, checkbox, text-input)

- widgets can dispatch reframe events
  (this means widgets can interact with the environment (example theme light/dark switcher)

- register cljs event-handler
  this needs cljs -> js compilation  (options: shadow, embedded, sci)

- register clj event-handler
  the system will forward events via websocket
     sent from cljs, but eventhandler defined in clj -> send from cljs -> clj
     sent from clj, but eventhandler not defined in clj -> sent from clj -> cljs

- server is essentially providing a rest api via websocket, in the context of
  the running system


## developer workflow:


```
  npm install
  lein shadow-compile
  lein goldly
```


     



