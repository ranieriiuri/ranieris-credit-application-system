package api.ranieriiuri.credit.application.system.service.impl

import api.ranieriiuri.credit.application.system.entity.Customer
import api.ranieriiuri.credit.application.system.repository.CustomerRepository
import api.ranieriiuri.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository      //Injetando a interface que faz a ligação com o BD dentro dessa class, o q permite fazer as operações!
): ICustomerService {
    override fun save(customer: Customer): Customer = this.customerRepository.save(customer)


    override fun findById(id: Long): Customer = this.customerRepository.findById(id).orElseThrow {
        throw RuntimeException("Id $id not found")
    }       // Em suma, vai tentar encontrar pelo id, se não encontrar, dispara uma exceção com mensagem de não encontrado. Isso pq qnd ele retorna, pode ser um Customer, ou um 'optional'

    override fun delete(id: Long) = this.customerRepository.deleteById(id)  //Não retorna, é 'Unit'(void) por inferência pq esse é o padrão do delete!
}