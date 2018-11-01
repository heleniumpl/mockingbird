package pl.helenium.mockingbird.model

import java.util.Comparator.naturalOrder
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

interface Type {

    fun generate(): Any? = null

    fun validate(path: String, value: Any, errors: MutableList<ModelError>) = Unit

    fun comparator(): Comparator<Any> = throw IllegalStateException("Type has no comparator specified!")

}

object AnyType : Type

abstract class AbstractType<T : Comparable<T>>(private val type: KClass<T>) : Type {

    override fun validate(path: String, value: Any, errors: MutableList<ModelError>) {
        if (!type.isInstance(value))
            errors += "Property `$path` = `$value` (of type `${value.javaClass.name}`) is not of `${javaClass.simpleName}` type!"
    }

    @Suppress("UNCHECKED_CAST")
    override fun comparator() = naturalOrder<T>() as Comparator<Any>

}

@Suppress("ClassName")
object long : AbstractType<Long>(Long::class) {

    private val sequence = AtomicLong(1)

    override fun generate() = sequence.getAndIncrement()

}

@Suppress("ClassName")
object string : AbstractType<String>(String::class)

@Suppress("ClassName")
object boolean : AbstractType<Boolean>(Boolean::class)

// TODO list
// TODO map
//
// TODO with generator
