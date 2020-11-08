package com.github.exerosis.amex


import java.text.NumberFormat


val FORMAT_CURRENCY = NumberFormat.getCurrencyInstance()!!
//discounts can actually manage to be implemented in these functors.
val PRICES = mapOf<String, (Int) -> (Double)>(
    "apple" to { it / 2 * 0.60 + it % 2 * 0.60 },
    "orange" to { it / 3 * 0.50 + it % 3 * 0.25 }
)
//map orders to responses. (using caps to represent "class like" functionality)
fun shop(onOrder: Event<(Sequence<String>) -> (Unit)>): Event<(String) -> (Unit)> = { listener ->
    onOrder { items ->
        listener(try {
            //group by the price functor then sum by that functor and group count
            FORMAT_CURRENCY.format(items.groupBy {
                PRICES[it.toLowerCase()] ?: throw Exception("Could not find item: $it")
            }.asSequence().sumByDouble { (price, group) -> price(group.size) })
            //alternatively something failed (likely finding the item)
        } catch (reason: Throwable) { reason.message ?: "failed" })
    }
}