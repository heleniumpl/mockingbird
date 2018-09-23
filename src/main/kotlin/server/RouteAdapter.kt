package pl.helenium.mockingbird.server

import spark.Response

typealias RouteAdapter = (request: RequestAdapter, response: Response) -> Any?
