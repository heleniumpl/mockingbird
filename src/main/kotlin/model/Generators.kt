package pl.helenium.mockingbird.model

import java.util.concurrent.atomic.AtomicLong

object LongGenerator : () -> Any? {

    private val sequence = AtomicLong(1)

    override fun invoke() = sequence.getAndIncrement()

}
