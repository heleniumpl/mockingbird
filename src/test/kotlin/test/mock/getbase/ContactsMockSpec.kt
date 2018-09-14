package pl.helenium.mockingbird.test.mock.getbase

import com.github.kittinunf.fuel.httpPost
import io.kotlintest.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode.SCOPE
import org.spekframework.spek2.style.specification.describe
import pl.helenium.mockingbird.Mockingbird
import pl.helenium.mockingbird.status

class ContactsMockSpec : Spek({

    describe("POST contact") {

        val mock by memoized {
            Mockingbird()
                .mocks(::ContactsMock)
                .start()
        }

        context("when contact is created") {

            val response by memoized(mode = SCOPE) {
                "http://localhost:${mock.server.port()}/v2/contacts"
                    .httpPost()
                    // language=json
                    .body("""{
  "data": {
    "last_name": "Marquez"
  }
}""")
                    .responseString()
            }

            it("returns 200") {
                response.status() shouldBe 200
            }

        }

        afterEach { mock.stop() }

    }

})
