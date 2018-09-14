package pl.helenium.mockingbird

import spark.Route

class HelloWorldMock(context: Context) : DslMock(context, {

    get("/hello_world") {
        Route { _, _ ->
            "Hello World!"
        }
    }

})
