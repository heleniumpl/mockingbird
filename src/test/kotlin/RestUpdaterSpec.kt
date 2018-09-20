package pl.helenium.mockingbird

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowExactly
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RestUpdaterSpec : Spek({

    describe("Updates model") {

        val updater by memoized { RestUpdater() }

        val targetMap by memoized {
            mapOf(
                "number" to 1234,
                "string" to "some string",
                "boolean" to true,
                "null" to null,
                "map" to mapOf(
                    "direct" to "direct",
                    "embeddedList" to listOf(
                        "a",
                        "b"
                    ),
                    "embeddedMap" to mapOf(
                        "a" to 1,
                        "b" to "bee",
                        "null" to null
                    )
                ),
                "list" to listOf(
                    "a",
                    "b"
                )
            )
        }

        val target by memoized { Model(targetMap).toMutable() }

        it("empty update should not change the model") {
            updater.update(target, Model(emptyMap()))

            target.getProperty<Int>("number") shouldBe 1234
        }

        it("new attributes should be added to the model") {
            updater.update(
                target, Model(
                    mapOf(
                        "newOne" to "newSimple",
                        "newTwo" to listOf("one", "two")
                    )
                )
            )

            target.getProperty<Int>("number") shouldBe 1234
            target.getProperty<String>("newOne") shouldBe "newSimple"
            target.embeddedList<String>("newTwo") shouldBe listOf("one", "two")
        }

        it("existing simple attribute should be set to null") {
            updater.update(
                target, Model(
                    mapOf(
                        "boolean" to null
                    )
                )
            )

            target.getProperty<Boolean>("boolean") shouldBe null
            target.getProperty<String>("string") shouldBe "some string"
        }

        it("existing map attribute should be set to null") {
            updater.update(
                target, Model(
                    mapOf(
                        "map" to null
                    )
                )
            )

            target.maybeEmbeddedModel("map") shouldBe null
            target.getProperty<String>("string") shouldBe "some string"
        }

        it("existing list attribute should be set to null") {
            updater.update(
                target, Model(
                    mapOf(
                        "list" to null
                    )
                )
            )

            target.maybeEmbeddedList<String>("list") shouldBe null
            target.getProperty<String>("string") shouldBe "some string"
        }

        it("existing simple attribute should be set to new value") {
            updater.update(
                target, Model(
                    mapOf(
                        "null" to "no longer null"
                    )
                )
            )

            target.getProperty<String>("null") shouldBe "no longer null"
            target.getProperty<String>("string") shouldBe "some string"
        }

        it("new key should be added to existing map") {
            updater.update(
                target, Model(
                    mapOf(
                        "map" to mapOf(
                            "newKey" to "newValue"
                        )
                    )
                )
            )

            with(target.embeddedModel("map")) {
                getProperty<String>("newKey") shouldBe "newValue"
                getProperty<String>("direct") shouldBe "direct"
                embeddedList<String>("embeddedList") shouldBe listOf("a", "b")
            }
            target.getProperty<String>("string") shouldBe "some string"
        }

        it("new key should be added to existing embedded map") {
            updater.update(
                target, Model(
                    mapOf(
                        "map" to mapOf(
                            "embeddedMap" to mapOf(
                                "newKey" to "newValue"
                            )
                        )
                    )
                )
            )

            with(target.embeddedModel("map")) {
                with(embeddedModel("embeddedMap")) {
                    getProperty<String>("newKey") shouldBe "newValue"
                    getProperty<Int>("a") shouldBe 1
                }
                getProperty<String>("direct") shouldBe "direct"
                embeddedList<String>("embeddedList") shouldBe listOf("a", "b")
            }
            target.getProperty<String>("string") shouldBe "some string"
        }

        it("content of existing list should be replaced") {
            updater.update(
                target, Model(
                    mapOf(
                        "list" to listOf(
                            1,
                            2
                        )
                    )
                )
            )

            target.embeddedList<Int>("list") shouldBe listOf(1, 2)
            target.getProperty<String>("string") shouldBe "some string"
        }

        it("content of existing embedded list should be replaced") {
            updater.update(
                target, Model(
                    mapOf(
                        "map" to mapOf(
                            "embeddedList" to listOf(1, 2)
                        )
                    )
                )
            )

            target.embeddedModel("map").embeddedList<Int>("embeddedList") shouldBe listOf(1, 2)
            target.embeddedModel("map").getProperty<String>("direct") shouldBe "direct"
        }

        it("should not be possible to replace list with map") {
            shouldThrowExactly<IllegalArgumentException> {
                updater.update(
                    target, Model(
                        mapOf(
                            "list" to mapOf(
                                "one" to 1,
                                "two" to 2
                            )
                        )
                    )
                )
            }
        }

        it("should not be possible to replace list with simple value") {
            shouldThrowExactly<IllegalArgumentException> {
                updater.update(
                    target, Model(
                        mapOf(
                            "list" to "simple"
                        )
                    )
                )
            }
        }

        it("should not be possible to replace map with list") {
            shouldThrowExactly<IllegalArgumentException> {
                updater.update(
                    target, Model(
                        mapOf(
                            "map" to listOf(
                                1,
                                2
                            )
                        )
                    )
                )
            }
        }

        it("should not be possible to replace map with simple value") {
            shouldThrowExactly<IllegalArgumentException> {
                updater.update(
                    target, Model(
                        mapOf(
                            "map" to "simple"
                        )
                    )
                )
            }
        }

        it("should not be possible to replace simple value with list") {
            shouldThrowExactly<IllegalArgumentException> {
                updater.update(
                    target, Model(
                        mapOf(
                            "number" to listOf(
                                1,
                                2
                            )
                        )
                    )
                )
            }
        }

        it("should not be possible to replace simple value with map") {
            shouldThrowExactly<IllegalArgumentException> {
                updater.update(
                    target, Model(
                        mapOf(
                            "number" to mapOf(
                                "one" to 1,
                                "two" to 2
                            )
                        )
                    )
                )
            }
        }

    }

})
