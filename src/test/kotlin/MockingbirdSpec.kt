package pl.helenium.mockingbird

import com.github.kittinunf.fuel.httpGet
import io.kotlintest.matchers.collections.shouldBeUnique
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.ServerSocket

class MockingbirdSpec : Spek({

    describe("mock lifecycle") {

        fun ensureHandlesHttp(port: Int) {
            "http://localhost:$port/not_existing_uri"
                .httpGet()
                .responseString()
                .status() shouldBe 404
        }

        context("when mock is started with no given port") {
            val mock by memoized { Mockingbird() }
            beforeEach { mock.start() }

            it("starts on random free port > 1024") {
                mock.port().shouldBeGreaterThan(1024)
            }

            it("handles HTTP") {
                ensureHandlesHttp(mock.port())
            }

            afterEach { mock.stop() }
        }

        context("when mock is started with given (free) port") {
            val freePort by memoized { freeTcpPort() }
            val mock by memoized { Mockingbird(freePort) }
            beforeEach { mock.start() }

            it("starts on given port") {
                mock.port() shouldBe freePort
            }

            it("handles HTTP") {
                ensureHandlesHttp(mock.port())
            }

            afterEach { mock.stop() }
        }

        context("when multiple mocks are run") {
            val mocks by memoized { List(5) { Mockingbird() } }

            beforeEach { mocks.map(Mockingbird::start) }

            it("every mock should have different port") {
                mocks.map(Mockingbird::port).shouldBeUnique()
            }

            it("handles HTTP") {
                mocks.forEach {
                    ensureHandlesHttp(it.port())
                }
            }

            afterEach { mocks.forEach(Mockingbird::stop) }
        }

    }

    describe("routes") {

        val mock by memoized { Mockingbird() }

        context("when route is registered") {
            beforeEach {
                mock
                    .route {
                        get("/hello_world") { _, _ ->
                            "Hello World!"
                        }
                    }
                    .start()
            }

            val response by memoized {
                "http://localhost:${mock.port()}/hello_world"
                    .httpGet()
                    .responseString()
            }

            it("returns 200") {
                response.status() shouldBe 200
            }

            it("returns configured response text") {
                response.body() shouldBe "Hello World!"
            }

        }

        afterEach { mock.stop() }

    }

})

private fun freeTcpPort() = ServerSocket(0).use {
    with(it) {
        reuseAddress = true
        localPort
    }
}
