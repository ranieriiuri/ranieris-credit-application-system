package api.ranieriiuri.credit.application.system.service

import api.ranieriiuri.credit.application.system.entity.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer
    fun findById(id: Long): Customer
    fun delete(id: Long)
}