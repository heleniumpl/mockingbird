package pl.helenium.mockingbird

infix fun <A, B, C> ((A) -> B).then(then: (B) -> C) : (A) -> C = { then(this(it)) }
