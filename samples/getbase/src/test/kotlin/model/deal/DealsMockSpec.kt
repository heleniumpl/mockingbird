package pl.helenium.mockingbird.test.mock.getbase.model.deal

import com.github.kittinunf.fuel.httpPost
import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import pl.helenium.mockingbird.Mockingbird
import pl.helenium.mockingbird.test.commons.execute
import pl.helenium.mockingbird.test.mock.getbase.randomLong
import pl.helenium.mockingbird.test.mock.getbase.toJson

object DealsMockSpec : Spek({

    describe("Deals Mock") {

        val mock by memoized {
            Mockingbird()
                .setup {
                    actors {
                        scope("basePublic") {
                            actor(12345L, "very_secret_auth_token", "CEO of Base")
                        }
                    }
                    mocks(::DealsMock)
                }
                .start()
        }

        describe("POST deal") {

            context("when deal w/ proper attributes is created") {

                val response by memoized {
                    mock.createDeal(mapOf(
                        "name" to "200 bottles of wine",
                        "contact_id" to randomLong(),
                        "hot" to true
                    ))
                }

                it("returns 200") {
                    response.status shouldBe 200
                }

            }

            context("when deal w/o name nor contact_id is created") {

                val response by memoized {
                    mock.createDeal(mapOf<String, Any>())
                }

                it("returns 422") {
                    response.status shouldBe 422
                }

                it("response contains error details") {
                    response.body should contain("Property 'name' is required but was not given!")
                    response.body should contain("Property 'contact_id' is required but was not given!")
                }

            }

            context("when deal w/o name is created") {

                val response by memoized {
                    mock.createDeal(mapOf("contact_id" to randomLong()))
                }

                it("returns 422") {
                    response.status shouldBe 422
                }

                it("response contains error details") {
                    response.body should contain("Property 'name' is required but was not given!")
                    response.body shouldNot contain("Property 'contact_id' is required but was not given!")
                }

            }

            context("when deal w/o contact_id is created") {

                val response by memoized {
                    mock.createDeal(mapOf("name" to "200 bottles of wine"))
                }

                it("returns 422") {
                    response.status shouldBe 422
                }

                it("response contains error details") {
                    response.body shouldNot contain("Property 'name' is required but was not given!")
                    response.body should contain("Property 'contact_id' is required but was not given!")
                }

            }

            context("when deal w/ contact_id not being a number is created") {

                val response by memoized {
                    mock.createDeal(mapOf(
                        "name" to "200 bottles of wine",
                        "contact_id" to "not a number"
                    ))
                }

                it("returns 422") {
                    response.status shouldBe 422
                }

                it("response contains error details") {
                    response.body shouldBe "Property `contact_id` = `not a number` is not of `long` type!"
                }

            }

            context("when deal w/ name not being a string is created") {

                val response by memoized {
                    mock.createDeal(mapOf(
                        "name" to false,
                        "contact_id" to randomLong()
                    ))
                }

                it("returns 422") {
                    response.status shouldBe 422
                }

                it("response contains error details") {
                    response.body shouldBe "Property `name` = `false` is not of `string` type!"
                }

            }

            context("when deal w/ `hot` not being a boolean is created") {

                val response by memoized {
                    mock.createDeal(mapOf(
                        "name" to "3 dogs & 4 cats",
                        "contact_id" to randomLong(),
                        "hot" to "cold"
                    ))
                }

                it("returns 422") {
                    response.status shouldBe 422
                }

                it("response contains error details") {
                    response.body shouldBe "Property `hot` = `cold` is not of `boolean` type!"
                }

            }

        }

    }

})

private fun Mockingbird.createDeal(body: Any) =
    "http://localhost:${context.port}/v2/deals"
        .httpPost()
        .header("Authorization" to "Bearer very_secret_auth_token")
        .body(mapOf("data" to body).toJson())
        .execute()
