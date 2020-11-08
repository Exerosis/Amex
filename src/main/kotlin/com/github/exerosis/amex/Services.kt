package com.github.exerosis.amex

import Items
import Order
import Status
import java.time.Instant
import java.util.*

val PATTERN_LIST = Regex("\\s?[, ]\\s?")
//Handles user input and attempts to order items.
fun OrderingService(): Event<(Order, Items) -> (Unit)> {
    val onOrder = ArrayEvent<(Order, Items) -> (Unit)>()
    System.`in`.reader(Charsets.UTF_8).forEachLine {
        val id = UUID.randomUUID()
        onOrder(id, it.split(PATTERN_LIST))
        println("Placed: $id")
    }
    return onOrder
}
//Notifies users when their order status changes.
fun NotificationService(onStatusUpdated: Event<(Order, Status) -> (Unit)>) {
    onStatusUpdated { order, status ->
        //simulates sending a notification to the user.
        println("Order($order) - $status")
    }
}
//Attempts to process a valid user order.
fun ProcessingService(onOrderVerified: Event<(Order, Items) -> (Unit)>): Event<(Order, Status) -> (Unit)> {
    val onStatusUpdated = ArrayEvent<(Order, Status) -> (Unit)>()
    onOrderVerified { order, items ->
        try {
            //actually handle the order in a real application.
            onStatusUpdated(order, Status.Shipped(Instant.now().plusSeconds(30)))
        } catch (reason: Throwable) {
            onStatusUpdated(order, Status.Refunded(reason.message!!))
        }
    }; return onStatusUpdated
}