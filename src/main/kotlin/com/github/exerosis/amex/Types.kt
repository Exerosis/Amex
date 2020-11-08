import java.time.temporal.Temporal
import java.util.*


typealias Order = UUID
typealias Items = List<String>
sealed class Status {
    data class Invoiced(val cost: Double): Status()
    data class Rejected(val reason: String): Status()
    data class Shipped(val eta: Temporal): Status()
    data class Refunded(val reason: String): Status()
}