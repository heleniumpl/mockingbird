package pl.helenium.mockingbird.model

import io.kotlintest.shouldThrow
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OrderSpec : Spek({

    describe("Order") {

        it("should not allow to sort by not sortable property") {
            shouldThrow<IllegalArgumentException> {
                Order(
                    Property("name") {
                        sortable(false)
                    }
                )
            }
        }

    }

})
