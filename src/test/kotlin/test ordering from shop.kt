import com.github.exerosis.amex.ArrayEvent
import com.github.exerosis.amex.invoke
import com.github.exerosis.amex.shop
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.*

@TestInstance(PER_CLASS) class `test ordering from shop` {
    @Test fun `shop should not respond to no orders`() {
        val orders = ArrayEvent<(Sequence<String>) -> (Unit)>()
        val listener = mockk<(String) -> (Unit)>("listener")
        every { listener(any()) } just Runs
        shop(orders)(listener)
        confirmVerified(listener)
    }
    @Test fun `shop should respond error to invalid order item`() {
        val orders = ArrayEvent<(Sequence<String>) -> (Unit)>()
        val listener = mockk<(String) -> (Unit)>("listener")
        every { listener(any()) } just Runs
        shop(orders)(listener)
        orders(sequenceOf("apple", "orange", "pear", "apple"))
        verifyAll { listener("Could not find item: pear") }
        confirmVerified(listener)
    }
    @Test fun `shop should respond to empty order with 0`() {
        val orders = ArrayEvent<(Sequence<String>) -> (Unit)>()
        val listener = mockk<(String) -> (Unit)>()
        every { listener(any()) } just Runs
        shop(orders)(listener)
        orders(emptySequence())
        verify(exactly = 1) { listener("$0.00") }
        confirmVerified(listener)
    }
    @Test fun `shop should respond to each order individually`() {
        val orders = ArrayEvent<(Sequence<String>) -> (Unit)>()
        val listener = mockk<(String) -> (Unit)>()
        every { listener(any()) } just Runs
        shop(orders)(listener)
        orders(sequenceOf("apple", "orange", "apple", "apple"))
        orders(sequenceOf("apple", "orange", "pear", "apple"))
        verifySequence {
            listener("$2.05")
            listener("Could not find item: pear")
        }
        confirmVerified(listener)
    }
    @Test fun `shop should respond correctly regardless of item case`() {
        val orders = ArrayEvent<(Sequence<String>) -> (Unit)>()
        val listener = mockk<(String) -> (Unit)>()
        every { listener(any()) } just Runs
        shop(orders)(listener)
        orders(sequenceOf("orange", "orange"))
        orders(sequenceOf("ApPlE", "ORANge"))
        orders(sequenceOf("pEAcH", "oRaNge", "APPLE"))
        verifySequence {
            listener("$0.50")
            listener("$0.85")
            listener("Could not find item: pEAcH")
        }
        confirmVerified(listener)
    }
}