package pl.helenium.mockingbird

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result

typealias StringResponse = Triple<Request, Response, Result<String, FuelError>>

fun StringResponse.status() = second.statusCode

fun StringResponse.body() = third.get()
