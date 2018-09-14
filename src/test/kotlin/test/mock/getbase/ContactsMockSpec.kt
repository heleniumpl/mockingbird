package pl.helenium.mockingbird.test.mock.getbase

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.httpPost
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.maps.shouldContainExactly
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

            it("returns 200") {
                response.status() shouldBe 200
            }

            it("response contains all the properties") {
                val model = Model(ObjectMapper().readMap(response.body())).embeddedModel("data")

                assertSoftly {
                    model["contact_id"] shouldBe 1L
                    model["first_name"] shouldBe "Mark"
                    model["last_name"] shouldBe "Johnson"
                    model["title"] shouldBe "CEO"
                    model["description"] shouldBe "I know him via Tom"
                    model["industry"] shouldBe "Design Services"
                    model["website"] shouldBe "http://www.designservice.com"
                    model["email"] shouldBe "mark@designservices.com"
                    model["phone"] shouldBe "508-778-6516"
                    model["mobile"] shouldBe "508-778-6516"
                    model["fax"] shouldBe "+44-208-1234567"
                    model["twitter"] shouldBe "mjohnson"
                    model["facebook"] shouldBe "mjohnson"
                    model["linkedin"] shouldBe "mjohnson"
                    model["skype"] shouldBe "mjohnson"

                    val address = model.embeddedModel("address")
                    address["line1"] shouldBe "2726 Smith Street"
                    address["city"] shouldBe "Hyannis"
                    address["postal_code"] shouldBe "02601"
                    address["state"] shouldBe "MA"
                    address["country"] shouldBe "US"

                    model.embeddedList<String>("tags").shouldContainExactly("contractor", "early-adopter")
                    model.embeddedMap<String, String>("custom_fields")
                        .shouldContainExactly(mapOf("referral_website" to "http://www.example.com"))
                }
            }

        }

        afterEach { mock.stop() }

    }

})
