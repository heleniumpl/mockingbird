package pl.helenium.mockingbird.test.mock.getbase.model.contact

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.collections.shouldHaveSingleElement
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.string.beEmpty
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import pl.helenium.mockingbird.Mockingbird
import pl.helenium.mockingbird.json.defaultObjectMapper
import pl.helenium.mockingbird.json.readStringKeyMap
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.test.commons.StatusAndBody
import pl.helenium.mockingbird.test.commons.TimeTravelTimeService
import pl.helenium.mockingbird.test.commons.execute
import pl.helenium.mockingbird.test.mock.getbase.data
import pl.helenium.mockingbird.test.mock.getbase.id
import pl.helenium.mockingbird.test.mock.getbase.items
import pl.helenium.mockingbird.test.mock.getbase.meta
import pl.helenium.mockingbird.test.mock.getbase.model
import pl.helenium.mockingbird.test.mock.getbase.randomLong
import pl.helenium.mockingbird.test.mock.getbase.toJson
import java.time.Instant
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.SECONDS

@Suppress("ConvertCallChainIntoSequence")
object ContactsMockSpec : Spek({

    describe("Contacts Mock") {

        val time by memoized { TimeTravelTimeService() }

        val mock by memoized {
            Mockingbird()
                .setup {
                    actors {
                        scope("basePublic") {
                            actor(12345L, "very_secret_auth_token", "CEO of Base")
                        }
                    }
                    mocks(::ContactsMock)
                }
                .apply { context.services.time = time }
                .start()
        }

        val context by memoized { mock.context }

        val metaModel by memoized { context.metaModels.byName("contact") }

        describe("POST contact") {

            context("when contact is created") {

                val createdAt by memoized { Instant.now().minus(23, HOURS) }

                val response by memoized {
                    time.now = createdAt
                    mock.createContact()
                }

                it("returns 200") {
                    response.status shouldBe 200
                }

                it("is available through collection") {
                    context
                        .modelCollections
                        .byMetaModel(metaModel)
                        .get(response.model().data().id()) shouldNotBe null
                }

                it("creator_id is set") {
                    response.model().data().getProperty<Long>("creator_id") shouldBe 12345L
                }

                it("created_at is set to now") {
                    response
                        .model()
                        .data()
                        .getProperty<String>("created_at")
                        .toInstant() shouldBe createdAt.truncatedTo(SECONDS)
                }

                it("updated_at = created_at") {
                    with(response.model().data()) {
                        getProperty<String>("created_at").toInstant() shouldBe getProperty<String>("updated_at").toInstant()
                    }
                }

                behavesLikeRemoteContact(response)

            }

            context("when multiple contacts are created") {

                val responses by memoized { List(5) { mock.createContact() } }

                val models by memoized {
                    responses
                        .map(StatusAndBody::body)
                        .map { Model(defaultObjectMapper.readStringKeyMap(it)) }
                }

                it("every create returns 200") {
                    responses
                        .map(StatusAndBody::status)
                        .distinct() shouldHaveSingleElement 200
                }

                it("every created model should have distinct ID") {
                    models
                        .map(Model::data)
                        .map(Model::id)
                        .distinct() shouldHaveSize 5
                }

                it("every created model should be available through Collection") {
                    models
                        .map(Model::data)
                        .map(Model::id)
                        .mapNotNull {
                            context
                                .modelCollections
                                .byMetaModel(metaModel)
                                .get(it)
                        } shouldHaveSize 5
                }

            }

            context("when created contact is an individual and has no last name") {

                val response by memoized {
                    mock.createContact(
                        mapOf(
                            "is_organization" to false
                        )
                    )
                }

                it("returns 422") {
                    response.status shouldBe 422
                }

                it("response contains error details") {
                    response.body shouldBe "Property 'last_name' is required for an individual!"
                }

            }

            context("when created contact is an organization and has no name") {

                val response by memoized {
                    mock.createContact(
                        mapOf(
                            "is_organization" to true
                        )
                    )
                }

                it("returns 422") {
                    response.status shouldBe 422
                }

                it("response contains error details") {
                    response.body shouldBe "Property 'name' is required for an organization!"
                }

            }

        }

        describe("GET contacts") {

            context("when no contact exists") {

                val response by memoized { mock.getContacts() }

                it("returns 200") {
                    response.status shouldBe 200
                }

                it("has items envelope") {
                    with(response.model()) {
                        items() shouldHaveSize 0
                        with(meta()) {
                            getProperty<String>("type") shouldBe "collection"
                            getProperty<Long>("count") shouldBe 0
                        }
                    }
                }

            }

        }

        describe("GET contact") {

            whenContactDoesNotExist { mock.getContact(it) }

            context("when contact exists") {

                val response by memoized { mock.createContact() }

                val contact by memoized { response.model().data() }

                it("returns 200") {
                    mock.getContact(contact.id()).status shouldBe 200
                }

                behavesLikeRemoteContact(response)

            }

        }

        describe("PUT contact") {

            whenContactDoesNotExist { mock.putContact(it, emptyMap<String, Any>()) }

            context("when contact exists") {

                val createdAt by memoized { Instant.now().minus(23, HOURS) }

                val updatedAt by memoized { createdAt.plus(42, MINUTES) }

                val contact by memoized {
                    time.now = createdAt
                    mock
                        .createContact()
                        .also { time.now = updatedAt }
                        .model()
                        .data()
                }

                context("when empty update is done") {

                    val response by memoized { mock.putContact(contact.id(), emptyMap<String, Any>()) }

                    it("returns 200") {
                        response.status shouldBe 200
                    }

                    it("existing attributes are preserved") {
                        with(response.model().data()) {
                            assertSoftly {
                                getProperty<Long>("contact_id") shouldBe 1
                                embeddedModel("address").asMap() shouldBe mapOf(
                                    "line1" to "2726 Smith Street",
                                    "city" to "Hyannis",
                                    "postal_code" to "02601",
                                    "state" to "MA",
                                    "country" to "US"
                                )
                                embeddedList<String>("tags") shouldBe listOf(
                                    "contractor",
                                    "early-adopter"
                                )
                            }
                        }
                    }

                }

                context("when significant update is done") {

                    val response by memoized {
                        mock.putContact(
                            contact.id(), mapOf(
                                "contact_id" to 42,
                                "address" to mapOf(
                                    "city" to "Kraków",
                                    "state" to null
                                ),
                                "tags" to listOf(
                                    "developer"
                                )

                            )
                        )
                    }

                    it("returns 200") {
                        response.status shouldBe 200
                    }

                    it("attributes are changed") {
                        with(response.model().data()) {
                            assertSoftly {
                                getProperty<Long>("contact_id") shouldBe 42
                                embeddedModel("address").asMap() shouldBe mapOf(
                                    "line1" to "2726 Smith Street",
                                    "city" to "Kraków",
                                    "postal_code" to "02601",
                                    "state" to null,
                                    "country" to "US"
                                )
                                embeddedList<String>("tags") shouldBe listOf(
                                    "developer"
                                )
                            }
                        }
                    }

                    it("created_at is set to original date") {
                        response
                            .model()
                            .data()
                            .getProperty<String>("created_at")
                            .toInstant() shouldBe createdAt.truncatedTo(SECONDS)
                    }

                    it("updated_at is set to now") {
                        response
                            .model()
                            .data()
                            .getProperty<String>("updated_at")
                            .toInstant() shouldBe updatedAt.truncatedTo(SECONDS)
                    }

                }

            }

        }

        describe("DELETE contact") {

            whenContactDoesNotExist { mock.deleteContact(it) }

            context("when contact exists") {

                val contact by memoized { mock.createContact().model().data() }

                val response by memoized { mock.deleteContact(contact.id()) }

                it("returns 204") {
                    response.status shouldBe 204
                }

                it("response body should be empty") {
                    response.body should beEmpty()
                }

                it("model is no longer available through API") {
                    @Suppress("UNUSED_EXPRESSION") response
                    mock.getContact(contact.id()).status shouldBe 404
                }

                it("model is no longer available through Collection") {
                    @Suppress("UNUSED_EXPRESSION") response
                    mock
                        .context
                        .modelCollections
                        .byMetaModel(metaModel)
                        .get(contact.id()) shouldBe null
                }

            }

        }

        afterEach { mock.stop() }

    }

})

private fun Suite.whenContactDoesNotExist(action: (id: Long) -> StatusAndBody) {
    context("when contact does not exist") {

        val id by memoized { randomLong() }

        val response by memoized { action(id) }

        it("returns 404") {
            response.status shouldBe 404
        }

        it("returns proper message") {
            response.body shouldBe "Model contact#$id not found!"
        }

    }

}

private fun Suite.behavesLikeRemoteContact(response: StatusAndBody) {
    it("response contains all the properties") {
        assertSoftly {
            with(response.model().data()) {
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

                kotlin.with(embeddedModel("address")) {
                    getProperty<String>("line1") shouldBe "2726 Smith Street"
                    getProperty<String>("city") shouldBe "Hyannis"
                    getProperty<String>("postal_code") shouldBe "02601"
                    getProperty<String>("state") shouldBe "MA"
                    getProperty<String>("country") shouldBe "US"
                }

                embeddedList<String>("tags").shouldContainExactly("contractor", "early-adopter")
                embeddedModel("custom_fields").asMap()
                    .shouldContainExactly(kotlin.collections.mapOf("referral_website" to "http://www.example.com"))
            }
        }
    }

    it("has ID") {
        response.model().data().id() shouldBeGreaterThan 0
    }

    it("has meta type") {
        response
            .model()
            .meta()
            .getProperty<String>("type") shouldBe "contact"
    }
}

private fun Mockingbird.createContact(body: Any = exampleModel()) =
    "http://localhost:${context.port}/v2/contacts"
        .httpPost()
        .header("Authorization" to "Bearer very_secret_auth_token")
        .body(mapOf("data" to body).toJson())
        .execute()

private fun Mockingbird.getContacts() =
    "http://localhost:${context.port}/v2/contacts"
        .httpGet()
        .authorized()
        .execute()

private fun Mockingbird.getContact(id: Long) =
    "http://localhost:${context.port}/v2/contacts/$id"
        .httpGet()
        .authorized()
        .execute()

private fun Mockingbird.putContact(id: Long, body: Any) =
    "http://localhost:${context.port}/v2/contacts/$id"
        .httpPut()
        .authorized()
        .body(mapOf("data" to body).toJson())
        .execute()

private fun Mockingbird.deleteContact(id: Long) =
    "http://localhost:${context.port}/v2/contacts/$id"
        .httpDelete()
        .authorized()
        .execute()

private fun Request.authorized() = header("Authorization" to "Bearer very_secret_auth_token")

private fun String.toInstant(): Instant = Instant.parse(this)

@Suppress("SpellCheckingInspection")
private fun exampleModel() = mutableMapOf(
    "contact_id" to 1,
    "first_name" to "Mark",
    "last_name" to "Johnson",
    "title" to "CEO",
    "description" to "I know him via Tom",
    "industry" to "Design Services",
    "website" to "http://www.designservice.com",
    "email" to "mark@designservices.com",
    "phone" to "508-778-6516",
    "mobile" to "508-778-6516",
    "fax" to "+44-208-1234567",
    "twitter" to "mjohnson",
    "facebook" to "mjohnson",
    "linkedin" to "mjohnson",
    "skype" to "mjohnson",
    "address" to mutableMapOf(
        "line1" to "2726 Smith Street",
        "city" to "Hyannis",
        "postal_code" to "02601",
        "state" to "MA",
        "country" to "US"
    ),
    "tags" to mutableListOf(
        "contractor",
        "early-adopter"
    ),
    "custom_fields" to mutableMapOf(
        "referral_website" to "http://www.example.com"
    )
)
