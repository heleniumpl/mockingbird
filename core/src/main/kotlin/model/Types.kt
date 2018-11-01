package pl.helenium.mockingbird.model

import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

interface Type {

    fun generate(): Any? = null

    fun validate(path: String, value: Any, errors: MutableList<ModelError>) = Unit

}

object AnyType : Type

abstract class AbstractType(private val type: KClass<*>) : Type {

    override fun validate(path: String, value: Any, errors: MutableList<ModelError>) {
        if (!type.isInstance(value))
            errors += "Property `$path` = `$value` (of type `${value.javaClass.name}`) is not of `${javaClass.simpleName}` type!"
    }

}

@Suppress("ClassName")
object long : AbstractType(Long::class) {

    private val sequence = AtomicLong(1)

    override fun generate() = sequence.getAndIncrement()

}

@Suppress("ClassName")
object string : AbstractType(String::class)

@Suppress("ClassName")
object boolean : AbstractType(Boolean::class)

// TODO list
// TODO map
//
// TODO with generator
