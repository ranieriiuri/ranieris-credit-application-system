package api.ranieriiuri.credit.application.system.dto

import api.ranieriiuri.credit.application.system.entity.Credit
import api.ranieriiuri.credit.application.system.enummeration.Status
import jakarta.persistence.Id
import java.math.BigDecimal
import java.util.UUID

data class CreditView(
    val creditCode: UUID,
    val creditValue: BigDecimal,
    val numberOfInstallments: Int,
    val status: Status,
    val emailCustomer: String?,
    val incomeCustomer: BigDecimal?,
    val customerId: Long?
) {
    constructor(credit: Credit): this (
        creditCode = credit.creditCode,
        creditValue = credit.creditValue,
        numberOfInstallments = credit.numberOfInstallments,
        status = credit.status,
        emailCustomer = credit.customer?.email,
        incomeCustomer = credit.customer?.income,
        customerId = credit.customer?.id
    )
}
