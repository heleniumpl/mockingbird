package pl.helenium.mockingbird

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result

fun Triple<Request, Response, Result<*, FuelError>>.status() = second.statusCode

fun Triple<Request, Response, Result<String, FuelError>>.body() = third.get()
