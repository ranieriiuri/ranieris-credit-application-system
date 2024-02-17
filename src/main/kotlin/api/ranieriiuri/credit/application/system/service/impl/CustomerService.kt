package api.ranieriiuri.credit.application.system.service.impl

import api.ranieriiuri.credit.application.system.entity.Customer
import api.ranieriiuri.credit.application.system.exception.BusinessException
import api.ranieriiuri.credit.application.system.repository.CustomerRepository
import api.ranieriiuri.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository      //Injetando a interface que faz a ligação com o BD dentro dessa class, o q permite fazer as operações!
): ICustomerService {
    override fun save(customer: Customer): Customer = this.customerRepository.save(customer)


    override fun findById(id: Long): Customer = this.customerRepository.findById(id).orElseThrow {
        throw BusinessException("Id $id not found")
    }       // Em suma, vai tentar encontrar pelo id, se não encontrar, dispara uma exceção com mensagem de não encontrado. Isso pq qnd ele retorna, pode ser um Customer, ou um 'optional'

    // deletaremos pelo customer, pq desta forma conseguimos buscar pelo id com 'findById(acima)' e caso não seja achado, já vai retornar a BusinessException...
    override fun delete(id: Long) {
      val customer: Customer = this.findById(id)
        this.customerRepository.delete(customer)
    }
}