package api.ranieriiuri.credit.application.system.dto

import api.ranieriiuri.credit.application.system.entity.Address
import api.ranieriiuri.credit.application.system.entity.Customer
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto (
    @field:NotEmpty(message = "Invalid input. Insert a valid first name") val firstName: String,
    @field:NotEmpty(message = "Invalid input. Insert a valid last name") val lastName: String,
    @field:CPF(message = "This CPF is invalid") val cpf: String,
    @field:NotNull(message = "This income isn't nullable")val income: BigDecimal,
    @field:Email(message = "Invalid input. Insert a valid email") val email: String,
    @field:NotEmpty(message = "Invalid input. Insert a valid password") val password: String,
    @field:NotEmpty(message = "Invalid input. Insert the valid zipcode") val zipCode: String,
    @field:NotEmpty(message = "Invalid input. Insert the valid name of your street") val street: String

) {
   //fun que tranforma o dto recebido em um customer para o DB
    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        income = this.income,
        email = this.email,
        password = this.password,
        address = Address(
                zipCode = this.zipCode,
                street = this.street
                )
    )
}
