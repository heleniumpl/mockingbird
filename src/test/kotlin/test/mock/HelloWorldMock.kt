package pl.helenium.mockingbird.test.mock

import pl.helenium.mockingbird.Context
import pl.helenium.mockingbird.definition.DslMock
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
