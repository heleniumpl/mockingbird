package pl.helenium.mockingbird.model

import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.mockk.mockk
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ModelCollectionSpec : Spek({

    describe("Listing") {

        val metaModel by memoized {
            MetaModel("apple") {
                properties {
                    id {
                        type(long)
                    }
                }
            }
        }

        val collection by memoized { ModelCollection(mockk(), metaModel) }

        val models by memoized { collection.list().items }

        fun List<Model>.sortedById() = sortedWith(Order(metaModel.id().name).toOrderBy())

        context("0 element collection") {

            it("should return empty page") {
                collection.list().items shouldHaveSize 0
            }

        }

        context("1 element collection") {

            beforeEach {
                collection.create(null, Model(emptyMap()))
            }

            it("should return single element page") {
                collection.list().items shouldHaveSize 1
            }

        }

        context("13 element collection") {

            beforeEach {
                repeat(13) {
                    collection.create(null, Model(emptyMap()))
                }
            }

            it("should return all elements when no paging") {
                collection.list().items shouldHaveSize 13
            }

            describe("default sorting") {

                mapOf(
                    PageRequest(0, 1) to 0..0,
                    PageRequest(1, 1) to 1..1,
                    PageRequest(12, 1) to 12..12,
                    PageRequest(13, 1) to IntRange.EMPTY,
                    PageRequest(0, 3) to 0..2,
                    PageRequest(1, 3) to 3..5,
                    PageRequest(1, 3) to 3..5,
                    PageRequest(4, 3) to 12..12,
                    PageRequest(0, 13) to 0..12,
                    PageRequest(1, 13) to IntRange.EMPTY,
                    PageRequest(0, 26) to 0..12
                ).forEach { request, results ->
                    it("should return $results element when page request = $request") {
                        collection.list(request).items shouldBe models.sortedById().slice(results)
                    }
                }

            }

        }

    }

})
