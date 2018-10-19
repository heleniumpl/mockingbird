package pl.helenium.mockingbird.model

import java.util.concurrent.atomic.AtomicLong

interface Type {

    fun generate(): Any?

    fun validate(path: String, value: Any?, errors: MutableList<ModelError>) = Unit

}

object AnyType : Type {

    override fun generate(): Any? = null

}

@Suppress("ClassName")
object long : Type {

    private val sequence = AtomicLong(1)

    override fun generate() = sequence.getAndIncrement()

    override fun validate(path: String, value: Any?, errors: MutableList<ModelError>) {
        if (value !is Long)
            errors += "Property `$path` = `$value` is not of `${javaClass.simpleName}` type!"
    }

}
