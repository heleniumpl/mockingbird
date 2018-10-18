package pl.helenium.mockingbird.model

import java.time.Instant

class Services {

    var time: TimeService = SystemClockTimeService()

}

interface TimeService {

    fun now(): Instant

}

class SystemClockTimeService : TimeService {

    override fun now(): Instant = Instant.now()

}
