package pl.helenium.mockingbird

import spark.Route

object HelloWorldMock : DslMock({

    get("/hello_world") {
        Route { _, _ ->
            "Hello World!"
        }
    }

})
