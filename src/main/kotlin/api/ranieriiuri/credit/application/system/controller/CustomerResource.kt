package api.ranieriiuri.credit.application.system.controller

import api.ranieriiuri.credit.application.system.dto.CustomerDto
import api.ranieriiuri.credit.application.system.dto.CustomerUpdateDto
import api.ranieriiuri.credit.application.system.dto.CustomerView
import api.ranieriiuri.credit.application.system.entity.Customer
import api.ranieriiuri.credit.application.system.service.impl.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController                         // Indica que se trata da camada de controle
@RequestMapping("/api/customers")       // Cria o endpoint
class CustomerResource(
    private val customerService: CustomerService    // Injetando a class que faz essa ligação com o DB
) {
    @PostMapping
    fun saveCustomer(@RequestBody @Valid customerDto: CustomerDto): ResponseEntity<String> {       // Indica q, qnd chegar uma requisição com o 'customerDto' a api deve salvar e retornar uma string
        val savedCustomer =
            this.customerService.save(customerDto.toEntity())   //Já retorna o customer que será salvo no DB, utilizando a fun 'toEntity' designada p/ isto na class 'CustumerDto'(que define isto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Customer ${savedCustomer.email} saved")  // msg string de retorno
    }

    @GetMapping("/{id}")        // Indica que, as operações dessa fun serão do tipo get e virão no endpoint definido no '@RequestMapping' + '/id'
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> {                //indica q este parametro será requisicionado do caminho(endpoint) especificado acima!
        val customer: Customer =
            this.customerService.findById(id)      //faz a busca com o 'customerService' que liga ao BD e salva na val
        return ResponseEntity.status(HttpStatus.OK)
            .body(CustomerView(customer))                                   // Usa a class 'CustomerVier' passando a val acima como parâmetro, p/ retornar a view criada
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)                                  // vai retornar o status de 'no content' ao deletar
    fun deleteCustomer(@PathVariable id: Long) = this.customerService.delete(id)

    @PatchMapping
    fun updateCustomer(
        @RequestParam(value = "customerId") id: Long,        //O "@RequestParam" é uma outra forma de passar a "PathVariable", indicando qual valor vindo na req utilizaremos
        @RequestBody @Valid customerUpdateDto: CustomerUpdateDto
    ): ResponseEntity<CustomerView> {       //Indicamos que esse param será pego do corpo da requisição
        val customer: Customer =
            this.customerService.findById(id)                  // Busca pelo id e identifica nessa val
        val customerToUpdate: Customer =
            customerUpdateDto.toEntity(customer)       // Transforma o dto (trazido da request) em um customer atualizado
        val customerUpdated: Customer =
            this.customerService.save(customerToUpdate) // Salva esse novo customer no id do customer antigo (atualizando-o)
        return ResponseEntity.status(HttpStatus.OK)
            .body(CustomerView(customerUpdated))                                        // Retorna uma view usando o 'CustomerView'
    }
}