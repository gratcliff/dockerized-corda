package net.corda.training.contract

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction
import net.corda.training.state.IOUState

class IOUContract : Contract {
    companion object {
        @JvmStatic
        val IOU_CONTRACT_ID = "net.corda.training.contract.IOUContract"
    }

    interface Commands : CommandData {
        class Issue: TypeOnlyCommandData(), Commands
        class Settle: TypeOnlyCommandData(), Commands
        class Transfer: TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
      val command = tx.findCommand<Commands> { true }

      when (command.value) {
        is Commands.Issue -> requireThat {
          val iou = tx.outputStates.single() as IOUState
          "There should be no existing inputs when issuing currency" using(tx.inputs.isEmpty())
          "The issue command should produce 1 output of new currency" using(tx.outputs.size == 1)
          "The amount of the IOU issued should be greater than 0" using(iou.amount.quantity > 0)
          "The lending and borrowing parties should not be the same" using(iou.borrower != iou.lender)
          "The contract requires signatures of both involved parties" using
            (command.signers.toSet() == iou.participants.map { it.owningKey }.toSet())
        }
      }
    }
}
