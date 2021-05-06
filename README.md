# Goldly [![GitHub Actions status |pink-gorilla/goldly](https://github.com/pink-gorilla/goldly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/goldly/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/goldly.svg)](https://clojars.org/org.pinkgorilla/goldly)

- web dashboards and data-exploration in Clojure.
- inspired by R/shiny
- This project is used in [Notebook](https://github.com/pink-gorilla/gorilla-notebook).

# UI demo

To run goldly with some demonstration systems

```
lein goldly watch    ; runs a webserver on port 8000.
```

The source code of the demo systems is in `src/systems`.

# datamining example

A data-mining project that uses goldly is [EDGAR](https://github.com/clojure-quant/edgar)