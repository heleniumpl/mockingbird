package pl.helenium.mockingbird.server

interface RequestAdapter {

    fun param(name: String): String?

    fun header(name: String): String?

    fun body(): String

}
