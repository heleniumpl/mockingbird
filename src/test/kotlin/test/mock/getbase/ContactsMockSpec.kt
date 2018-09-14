package pl.helenium.mockingbird.test.mock.getbase

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.httpPost
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
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
                    .body(
                        """{
  "data": {
    "contact_id": 1,
    "first_name": "Mark",
    "last_name": "Johnson",
    "title": "CEO",
    "description": "I know him via Tom",
    "industry": "Design Services",
    "website": "http://www.designservice.com",
    "email": "mark@designservices.com",
    "phone": "508-778-6516",
    "mobile": "508-778-6516",
    "fax": "+44-208-1234567",
    "twitter": "mjohnson",
    "facebook": "mjohnson",
    "linkedin": "mjohnson",
    "skype": "mjohnson",
    "address": {
      "line1": "2726 Smith Street",
      "city": "Hyannis",
      "postal_code": "02601",
      "state": "MA",
      "country": "US"
    },
    "tags": [
      "contractor",
      "early-adopter"
    ],
    "custom_fields": {
      "referral_website": "http://www.example.com"
    }
  }
}"""
                    )
                    .responseString()
            }

            val model by memoized(mode = SCOPE) { Model(ObjectMapper().readMap(response.body())).embeddedModel("data") }

            it("returns 200") {
                response.status() shouldBe 200
            }

            it("response contains all the properties") {
                assertSoftly {
                    with(model) {
                        property<Long>("contact_id") shouldBe 1L
                        property<String>("first_name") shouldBe "Mark"
                        property<String>("last_name") shouldBe "Johnson"
                        property<String>("title") shouldBe "CEO"
                        property<String>("description") shouldBe "I know him via Tom"
                        property<String>("industry") shouldBe "Design Services"
                        property<String>("website") shouldBe "http://www.designservice.com"
                        property<String>("email") shouldBe "mark@designservices.com"
                        property<String>("phone") shouldBe "508-778-6516"
                        property<String>("mobile") shouldBe "508-778-6516"
                        property<String>("fax") shouldBe "+44-208-1234567"
                        property<String>("twitter") shouldBe "mjohnson"
                        property<String>("facebook") shouldBe "mjohnson"
                        property<String>("linkedin") shouldBe "mjohnson"
                        property<String>("skype") shouldBe "mjohnson"
                    }

                    with(model.embeddedModel("address")) {
                        property<String>("line1") shouldBe "2726 Smith Street"
                        property<String>("city") shouldBe "Hyannis"
                        property<String>("postal_code") shouldBe "02601"
                        property<String>("state") shouldBe "MA"
                        property<String>("country") shouldBe "US"
                    }

                    model.embeddedList<String>("tags").shouldContainExactly("contractor", "early-adopter")
                    model.embeddedMap<String, String>("custom_fields")
                        .shouldContainExactly(mapOf("referral_website" to "http://www.example.com"))
                }
            }

            it("has ID") {
                model.property<Long>("id") shouldBeGreaterThan 0
            }

        }

        afterEach { mock.stop() }

    }

})
