package pl.helenium.mockingbird

import spark.Route

class HelloWorldMock(context: Context) : DslMock(context, {

    routes {
        get {
            uri = "/hello_world"
            handler = Route { _, _ ->
                "Hello World!"
            }
        }
    }

})
