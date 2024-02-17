package api.ranieriiuri.credit.application.system.dto

import api.ranieriiuri.credit.application.system.entity.Customer
import java.math.BigDecimal

data class CustomerView(
    val firstname: String,
    val lastName: String,
    val cpf: String,
    val income: BigDecimal,
    val email: String,
    val zipCode: String,
    val street: String
) {
    // Contruiremos então, através do contructor uma view apenas com os dados que queremos do Customer pego
    constructor(customer: Customer): this (
        firstname = customer.firstName,
        lastName = customer.lastName,
        cpf = customer.cpf,
        income = customer.income,
        email = customer.email,
        zipCode = customer.address.zipCode,
        street = customer.address.street
    )
}
