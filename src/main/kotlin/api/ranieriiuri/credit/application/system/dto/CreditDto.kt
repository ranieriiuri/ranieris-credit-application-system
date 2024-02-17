package api.ranieriiuri.credit.application.system.dto

import api.ranieriiuri.credit.application.system.entity.Credit
import api.ranieriiuri.credit.application.system.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    val creditValue: BigDecimal,
    val dayFirstOfInstallment: LocalDate,
    val numberOfInstallments: Int,
    val customerId: Long
) {
    fun toEntity(): Credit = Credit(                        // essa fun permitirá pegarmos o dto que vem na req e transforma-lo em uma entity 'Credit'(forma que o DB aceita)
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstOfInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)           // é instanciado um customer, apenas com o id
    )
}
