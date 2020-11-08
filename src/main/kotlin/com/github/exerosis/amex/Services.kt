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
    val stock = object { var apples = 200; var oranges = 3000 }
    onOrderVerified { order, items ->
        val counts = items.groupingBy { it.toLowerCase() }.eachCount()
        stock.apples -= counts["apple"] ?: 0
        stock.oranges -= counts["orange"] ?: 0
        //We can pretty easily support stocking with the same system
        //we previously used for try catch. (perhaps rename refund)
        onStatusUpdated(order, when {
            stock.apples < 0 -> Status.Refunded("Apples are out of stock!")
            stock.oranges < 0 -> Status.Refunded("Oranges are out of stock!")
            else -> Status.Shipped(Instant.now().plusSeconds(30))
        })
    }; return onStatusUpdated
}