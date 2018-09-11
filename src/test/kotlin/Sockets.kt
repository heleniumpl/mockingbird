package pl.helenium.mockingbird

import java.net.ServerSocket

fun freeTcpPort() = ServerSocket(0).use {
    with(it) {
        reuseAddress = true
        localPort
    }
}
