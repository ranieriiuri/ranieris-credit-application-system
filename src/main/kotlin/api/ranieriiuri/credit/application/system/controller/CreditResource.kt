package api.ranieriiuri.credit.application.system.controller

import api.ranieriiuri.credit.application.system.dto.CreditDto
import api.ranieriiuri.credit.application.system.dto.CreditView
import api.ranieriiuri.credit.application.system.dto.CreditViewList
import api.ranieriiuri.credit.application.system.dto.CustomerView
import api.ranieriiuri.credit.application.system.entity.Credit
import api.ranieriiuri.credit.application.system.service.impl.CreditService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditResource(
    private val creditService: CreditService
) {
    @PostMapping
    fun saveCredit(@RequestBody @Valid creditDto: CreditDto): ResponseEntity<CreditView> {
        val credit: Credit =
            this.creditService.save(creditDto.toEntity())                          //transforma o dto da req no modelo que o DB aceita(Credit), constante na fun 'toEntity'
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreditView(credit))         //Retornará codigo do credito salvo e nome do cliente desse credito
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<CreditViewList>> {   // Recebe o id do cliente da req feita no endpoint
        val creditViewList: List<CreditViewList> = this.creditService.findAllByCustomer(customerId)
            .stream()                                    // usa o método que busca creditos e informações relacionadas pelo id do cliente recebido e já encadea com o método de 'stream' (que cria um fluxo com dados de listas, onde podemos) manipular sem alterar a lista original...
            .map { credit: Credit -> CreditViewList(credit) }                                               // o map trabalhará dentro da stream, p cada 'credit' que será recebido na stream, será passado como param para criar uma 'CreditViewList'...
            .collect(Collectors.toList())                                                                   //... por fim, coletamos e criamos uma nova lista com todos os dados processados acima (que já será retornado)

        return if (creditViewList.isEmpty()) {                                                               // se a lista vier vazia retorna 'No_Content'
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.OK).body(creditViewList)                                        // senão, retorna ok e a lista com os creditos
        }
    }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(
        @RequestParam(value = "customerId") customerId: Long,
        @PathVariable creditCode: UUID
    ): ResponseEntity<CreditView> {
        val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode)
        return ResponseEntity.status(HttpStatus.OK).body(CreditView(credit))
    }

    // A ultima parte desta etapa foi a refatoração, passando ao padrão de retornar uma 'ResponseEntity' para cada fun criada, padrão do Kotlin e dentro dela seus respectivos tipos!
    // ... *Fizemos o mesmo tbm para a etapa da camada de controle do customer (CustomerService)
}
