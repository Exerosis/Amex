package com.github.exerosis.amex

import Items
import Order
import Status


val PRICES = mapOf<String, (Int) -> (Double)>(
    "apple" to { it / 2 * 0.60 + it % 2 * 0.60 },
    "orange" to { it / 3 * 0.50 + it % 3 * 0.25 }
)
fun Shop() {
    val onOrderVerified = ArrayEvent<(Order, Items) -> (Unit)>()
    val onStatusUpdated = ArrayEvent<(Order, Status) -> (Unit)>()
    ProcessingService(onOrderVerified)(onStatusUpdated::invoke)
    NotificationService(onStatusUpdated)
    OrderingService()() { order, items ->
        try {
            val cost = items.groupBy {
                PRICES[it.toLowerCase()] ?: throw Exception("Could not find item: $it")
            }.asSequence().sumByDouble { (price, group) -> price(group.size) }
            onStatusUpdated.invoke(order, Status.Invoiced(cost))
            onOrderVerified(order, items)
        } catch (reason: Throwable) {
            onStatusUpdated(order, Status.Rejected(reason.message!!))
        }
    }
}