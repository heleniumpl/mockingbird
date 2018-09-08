package pl.helenium.mockingbird

import io.kotlintest.matchers.collections.shouldBeUnique
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.ServerSocket

class MockingbirdSpec : Spek({

    describe("mock lifecycle") {

        context("when mock is started with no given port") {
            val mock by memoized { Mockingbird() }
            beforeEach { mock.start() }

            it("starts on random free port > 1024") {
                mock.port().shouldBeGreaterThan(1024)
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

            afterEach { mock.stop() }
        }

        context("when multiple mocks are run") {
            val mocks by memoized { List(5) { Mockingbird() } }

            beforeEach { mocks.forEach(Mockingbird::start) }

            it("every mock should have different port") {
                mocks.map(Mockingbird::port).shouldBeUnique()
            }

            afterEach { mocks.forEach(Mockingbird::stop) }
        }

    }

})

private fun freeTcpPort() = ServerSocket(0).use {
    it.reuseAddress = true
    it.localPort
}
