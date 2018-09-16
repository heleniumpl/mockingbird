package pl.helenium.mockingbird.test.mock.getbase

import com.github.kittinunf.fuel.httpPost
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.collections.shouldHaveSingleElement
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pl.helenium.mockingbird.*

// language=json
private const val exampleModel = """{
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

@Suppress("ConvertCallChainIntoSequence")
object ContactsMockSpec : Spek({

    describe("POST contact") {

        val mock by memoized {
            Mockingbird()
                .mocks(::ContactsMock)
                .start()
        }

        val context by memoized { mock.context }

        val metaModel by memoized { context.metaModel("contact") }

        fun createContact(body: String = exampleModel) = "http://localhost:${mock.context.server.port()}/v2/contacts"
            .httpPost()
            .body(body)
            .responseString()

        context("when contact is created") {

            val response by memoized { createContact() }

            val model by memoized { Model(objectMapper.readMap(response.body())) }

            it("returns 200") {
                response.status() shouldBe 200
            }

            it("response contains all the properties") {
                assertSoftly {
                    with(model.embeddedModel("data")) {
                        getProperty<Long>("contact_id") shouldBe 1L
                        getProperty<String>("first_name") shouldBe "Mark"
                        getProperty<String>("last_name") shouldBe "Johnson"
                        getProperty<String>("title") shouldBe "CEO"
                        getProperty<String>("description") shouldBe "I know him via Tom"
                        getProperty<String>("industry") shouldBe "Design Services"
                        getProperty<String>("website") shouldBe "http://www.designservice.com"
                        getProperty<String>("email") shouldBe "mark@designservices.com"
                        getProperty<String>("phone") shouldBe "508-778-6516"
                        getProperty<String>("mobile") shouldBe "508-778-6516"
                        getProperty<String>("fax") shouldBe "+44-208-1234567"
                        getProperty<String>("twitter") shouldBe "mjohnson"
                        getProperty<String>("facebook") shouldBe "mjohnson"
                        getProperty<String>("linkedin") shouldBe "mjohnson"
                        getProperty<String>("skype") shouldBe "mjohnson"

                        with(embeddedModel("address")) {
                            getProperty<String>("line1") shouldBe "2726 Smith Street"
                            getProperty<String>("city") shouldBe "Hyannis"
                            getProperty<String>("postal_code") shouldBe "02601"
                            getProperty<String>("state") shouldBe "MA"
                            getProperty<String>("country") shouldBe "US"
                        }

                        embeddedList<String>("tags").shouldContainExactly("contractor", "early-adopter")
                        embeddedMap<String, String>("custom_fields")
                            .shouldContainExactly(mapOf("referral_website" to "http://www.example.com"))
                    }
                }
            }

            it("has ID") {
                model.id() shouldBeGreaterThan 0
            }

            it("has meta type") {
                model
                    .embeddedModel("meta")
                    .getProperty<String>("type") shouldBe "contact"
            }

            it("is available through collection") {
                context
                    .collection(metaModel)
                    .get(model.id()) shouldNotBe null
            }

        }

        context("when multiple contacts are created") {

            val responses by memoized { List(5) { createContact() } }

            val models by memoized {
                responses
                    .map(StringResponse::body)
                    .map { Model(objectMapper.readMap(it)) }
            }

            it("every create returns 200") {
                responses
                    .map(StringResponse::status)
                    .distinct() shouldHaveSingleElement 200
            }

            it("every created model should have distinct ID") {
                models
                    .map(Model::id)
                    .distinct() shouldHaveSize 5
            }

            it("every created model should be available through Collection") {
                models
                    .map(Model::id)
                    .mapNotNull { context.collection(metaModel).get(it) } shouldHaveSize 5
            }

        }

        afterEach { mock.stop() }

    }

})

private fun Model.id() = embeddedModel("data").getProperty<Long>("id")
