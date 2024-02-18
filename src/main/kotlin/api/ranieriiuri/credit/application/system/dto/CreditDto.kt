package api.ranieriiuri.credit.application.system.dto

import api.ranieriiuri.credit.application.system.entity.Credit
import api.ranieriiuri.credit.application.system.entity.Customer
import api.ranieriiuri.credit.application.system.validation.MaxFutureDate
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "the credit value inserted is invalid") val creditValue: BigDecimal,
    @field:MaxFutureDate(message = "the limit for the date of the first installment is up to 3 months from the current date.") val dayFirstOfInstallment: LocalDate,
    @field:Min(value = 1, message = "choose a number of installments above 0") @field:Max(value = 48, message = "choose a number of installments below 49") val numberOfInstallments: Int,
    @field:NotNull(message = "the id of this customer inserted is invalid") val customerId: Long
) {
    fun toEntity(): Credit =
        Credit(                        // essa fun permitirá pegarmos o dto que vem na req e transforma-lo em uma entity 'Credit'(forma que o DB aceita)
            creditValue = this.creditValue,
            dayFirstInstallment = this.dayFirstOfInstallment,
            numberOfInstallments = this.numberOfInstallments,
            customer = Customer(id = this.customerId)           // é instanciado um customer, apenas com o id
        )
}
