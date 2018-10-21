package pl.helenium.mockingbird.server

interface Response {

    fun status(status: Int)

    fun contentType(value: String) = header("Content-Type", value)

    fun header(name: String, value: String)

}
