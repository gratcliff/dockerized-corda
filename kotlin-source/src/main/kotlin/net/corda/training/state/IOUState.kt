package net.corda.training.state

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.Amount
import net.corda.core.identity.Party
import java.util.*

data class IOUState(
  val amount: Amount<Currency>,
  val lender: Party,
  val borrower: Party,
  val paid: Amount<Currency> = Amount(0, amount.token),
  override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState {
    override val participants: List<Party> get() = listOf(lender, borrower)

    fun pay(newAmount: Amount<Currency>): IOUState {
      if (newAmount.quantity > 0) {
        val amountPaidToLoan = paid.plus(newAmount)
        val updatedAmountOwed = amount.minus(newAmount)
        return this.copy(amount = updatedAmountOwed, paid = amountPaidToLoan)
      }
      return this
    }

    fun withNewLender(newLender: Party): IOUState {
      return this.copy(lender = newLender)
    }
}
