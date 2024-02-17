package api.ranieriiuri.credit.application.system.dto

import api.ranieriiuri.credit.application.system.entity.Credit
import api.ranieriiuri.credit.application.system.entity.Customer
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "Invalid input") val creditValue: BigDecimal,
    @field:Future(message = "Invalid input") val dayFirstOfInstallment: LocalDate,
    @field:Min(value = 1, message = "Invalid input") val numberOfInstallments: Int,
    @field:NotNull(message = "Invalid input")val customerId: Long
) {
    fun toEntity(): Credit =
        Credit(                        // essa fun permitirá pegarmos o dto que vem na req e transforma-lo em uma entity 'Credit'(forma que o DB aceita)
            creditValue = this.creditValue,
            dayFirstInstallment = this.dayFirstOfInstallment,
            numberOfInstallments = this.numberOfInstallments,
            customer = Customer(id = this.customerId)           // é instanciado um customer, apenas com o id
        )
}
