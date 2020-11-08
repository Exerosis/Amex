package com.github.exerosis.amex

//Leaks without being unregistered, in a large project this might be a problem.
typealias Event<Listener> = (Listener) -> (Unit)

//Simple implementation of an event that can have multiple listeners registered.
class ArrayEvent<Listener> : Event<Listener> {
    internal val listeners = ArrayList<Listener>()
    override fun invoke(listener: Listener) {
        listeners.add(listener)
    }
}

//Methods to fire events with various numbers of parameters.
operator fun <First> ArrayEvent<(First) -> (Unit)>.invoke(first: First)
    = listeners.forEach { it(first) }
operator fun <First, Second> ArrayEvent<(First, Second) -> (Unit)>.invoke(first: First, second: Second)
    = listeners.forEach { it(first, second) }
//Additional specializations as required.