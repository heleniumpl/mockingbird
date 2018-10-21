package pl.helenium.mockingbird.server

interface Request {

    fun param(name: String): String?

    fun queryParam(name: String): String?

    fun header(name: String): String?

    fun body(): String

}
