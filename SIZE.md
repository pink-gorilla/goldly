
  ; goldly core (no modules)
  ; watch:      15.0MB
  ; compile:     7.5MB             (transit warning.)
  ; compile-adv  2.5MB !!!!!
  ; ==> ** adv build is important for our own code **

  ;                  advanced
  ; webly/picasso    1300k
  ; goldly core      3000k
  ; goldly test      2400k
  ; goldly bundel    2400k
  ;              cljs 497k    
  ;           specter 215k
  ;         react-dom 130k
  ;               sci 590k 
  ;            jsjoda 180k  -> todo tick/repl lazy loader
  ; -- modules (lazy loaded)
  ; math                 1765k (from mathjax in resources)
  ; vega                 1300k (200k is the arrow format loader)
  ; aggrid                999k
  ; cytoscape             532k
  ; highcharts            402k
  ; markdown/prosemirror  401k
  ; input                 286k (ionslider jquery)
  ; codemirror            247k
  ; leaflet               173k
  ; ui-gorilla            169k (player)
  ; highlightjs            26k
  ; clock:                  2k

  ; quil - na, util fns
  ; TODO: add blueprintjs https://blueprintjs.com/docs/#select/multi-select + toast

  ; check bundel size on: https://bundlephobia.com/
  ; gfonts      700
  ; karma      1000
  ; highlight   900 * 
  ; prosemirror 800
  ; codemirror  200
  ; aggrid     1000 *
  ; vega        800 *
  ; cytoscope   800 *
  ; highcharts  300
  ; video       100
