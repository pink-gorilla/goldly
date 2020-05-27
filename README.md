# shiny-clj

##Overview

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

# Demo

```
  npm install
  lein shadow-compile
  lein demo
```


data <- attach(readRDS("data.rds"))
    for (prefix in names(resources)) {
        shiny::addResourcePath(prefix, resources[[prefix]])
    }
     



