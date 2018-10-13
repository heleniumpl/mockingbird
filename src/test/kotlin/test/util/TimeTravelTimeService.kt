package pl.helenium.mockingbird.test.util

import pl.helenium.mockingbird.model.TimeService
import java.time.Instant

class TimeTravelTimeService(var now: Instant? = null) : TimeService {

    override fun now(): Instant = now ?: Instant.now()

}
