package pl.helenium.mockingbird.test.mock.getbase

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.httpPost
import io.kotlintest.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode.SCOPE
import org.spekframework.spek2.style.specification.describe
import pl.helenium.mockingbird.*

class ContactsMockSpec : Spek({

    describe("POST contact") {

        val mock by memoized {
            Mockingbird()
                .mocks(::ContactsMock)
                .start()
        }

        context("when contact is created") {

            val response by memoized(mode = SCOPE) {
                "http://localhost:${mock.context.server.port()}/v2/contacts"
                    .httpPost()
                    // language=json
                    .body("""{
  "data": {
    "first_name": "Marc",
    "last_name": "Marquez"
  }
}""")
                    .responseString()
            }

            it("returns 200") {
                response.status() shouldBe 200
            }

            it("response contains all the properties") {
                val model = Model(ObjectMapper().readMap(response.body())).embeddedModel("data")

                model["first_name"] shouldBe "Marc"
                model["last_name"] shouldBe "Marquez"
            }

        }

        afterEach { mock.stop() }

    }

})
