# R - shiny

The design of goldly was inspired by R/shiny.


https://react-r.github.io/reactR/articles/intro_htmlwidgets.html
https://www.htmlwidgets.org/

R literate programming and web visualization:
Shiny - https://shiny.rstudio.com - client-server visualization apps in R
Rmarkdown - https://rmarkdown.rstudio.com - rmarkdown - markdown-based notebooks
Htmlwidgets - https://www.htmlwidgets.org
a collection of declarative R wrappers for js libraries
see the gallery: http://gallery.htmlwidgets.org/
can be used for creating standalone htmls, but also inside Shiny apps
we mentioned that we could potentially use it in two ways:
use it through R
use just the client side, and wrap it with Clojure (no R dependency)

add-dependencies
library(shiny)
library(sparklines)

Frontend UI templating
ui <- fluidPage(
  titlePanel("Sparklines library"),
  sliderInput("n", label = "Number of samples", min = 2, max = 1000, value = 100),
  sparklinesOutput("myWidget")
)

server <- function(input, output, session) {
    output$myWidget <- renderSparklines({
        sparklines(
            rnorm(input$n),
            sparklinesLine()
        )
    })
}

shinyApp(ui, server)
