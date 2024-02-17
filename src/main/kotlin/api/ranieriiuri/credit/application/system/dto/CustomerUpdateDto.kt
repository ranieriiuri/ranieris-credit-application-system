package api.ranieriiuri.credit.application.system.dto

import api.ranieriiuri.credit.application.system.entity.Customer
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CustomerUpdateDto(
    @field:NotEmpty(message = "Invalid input. Insert a valid first name") val firstName: String,
    @field:NotEmpty(message = "Invalid input. Insert a valid last name") val lastName: String,
    @field: NotNull(message = "This income isn't nullable") val income: BigDecimal,
    @field:NotEmpty(message = "Invalid input. Insert the valid zipcode") val zipCode: String,
    @field:NotEmpty(message = "Invalid input. Insert the valid name of your street") val street: String
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street
        return customer
    }
}
