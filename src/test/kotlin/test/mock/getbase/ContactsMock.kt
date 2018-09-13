package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.DslMock
import pl.helenium.mockingbird.GenericRoute

object ContactsMock : DslMock({

    post("/v2/contacts") {
        GenericRoute()
    }

})
