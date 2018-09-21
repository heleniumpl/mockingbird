package pl.helenium.mockingbird.definition

fun <T> identity(arg: T) = arg

infix fun <A, B, C> ((A) -> B).then(then: (B) -> C) : (A) -> C = { then(this(it)) }
