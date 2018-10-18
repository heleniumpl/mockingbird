package pl.helenium.mockingbird.test.commons

import java.net.ServerSocket

fun freeTcpPort() = ServerSocket(0).use {
    with(it) {
        reuseAddress = true
        localPort
    }
}
