package pl.helenium.mockingbird

import com.github.kittinunf.fuel.httpGet
import io.kotlintest.matchers.collections.shouldBeUnique
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.startWith
import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pl.helenium.mockingbird.test.mock.HelloWorldMock
import pl.helenium.mockingbird.test.util.execute
import pl.helenium.mockingbird.test.util.freeTcpPort

class MockingbirdSpec : Spek({

    describe("mock lifecycle") {

        fun ensureHandlesHttp(port: Int) {
            "http://localhost:$port/not_existing_uri"
                .httpGet()
                .execute()
                .status shouldBe 404
        }

        context("when mock is started with no given port") {
            val mock by memoized { Mockingbird().start() }

            it("starts on random free port > 1024") {
                mock.context.server.port().shouldBeGreaterThan(1024)
            }

            it("handles HTTP") {
                ensureHandlesHttp(mock.context.server.port())
            }

            afterEach { mock.stop() }
        }

        context("when mock is started with given (free) port") {
            val freePort by memoized { freeTcpPort() }
            val mock by memoized { Mockingbird(freePort).start() }

            it("starts on given port") {
                mock.context.server.port() shouldBe freePort
            }

            it("handles HTTP") {
                ensureHandlesHttp(mock.context.server.port())
            }

            afterEach { mock.stop() }
        }

        context("when multiple mocks are run") {
            val mocks by memoized { List(5) { Mockingbird() }.map(Mockingbird::start) }

            it("every mock should have different port") {
                mocks.map { it.context.server.port() }.shouldBeUnique()
            }

            it("handles HTTP") {
                mocks.forEach {
                    ensureHandlesHttp(it.context.server.port())
                }
            }

            afterEach { mocks.forEach(Mockingbird::stop) }
        }

    }

    describe("routes") {

        val mock by memoized {
            Mockingbird()
                .mocks(::HelloWorldMock)
                .start()
        }

        context("when /hello_world is called") {

            val response by memoized {
                "http://localhost:${mock.context.server.port()}/hello_world"
                    .httpGet()
                    .execute()
            }

            it("returns 200") {
                response.status shouldBe 200
            }

            it("returns configured response text") {
                response.body shouldBe "Hello World!"
            }

        }

        context("when /hello_world/exception is called") {

            val response by memoized {
                "http://localhost:${mock.context.server.port()}/hello_world/exception"
                    .httpGet()
                    .execute()
            }

            it("returns 500") {
                response.status shouldBe 500
            }

            it("response contains stacktrace") {
                with(response.body) {
                    this should startWith("java.lang.RuntimeException: Hello World Exception!")
                    this should contain("at pl.helenium.mockingbird")
                    this should contain("""\(HelloWorldMock.kt:\d+\)""".toRegex())
                }
            }

        }

        afterEach { mock.stop() }

    }

})
