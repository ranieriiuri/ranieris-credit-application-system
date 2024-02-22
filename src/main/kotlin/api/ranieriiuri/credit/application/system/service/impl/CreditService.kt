package api.ranieriiuri.credit.application.system.service.impl

import api.ranieriiuri.credit.application.system.entity.Credit
import api.ranieriiuri.credit.application.system.exception.BusinessException
import api.ranieriiuri.credit.application.system.repository.CreditRepository
import api.ranieriiuri.credit.application.system.service.ICreditService
import java.util.UUID
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.time.LocalDate

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService        //Injetamos tbm o CustomerService para utilizarmos o método dele de encontrar pelo id pra fazer a verificação nesse método 'save' abaixo
): ICreditService {
    override fun save(credit: Credit): Credit {
        this.validDayFirstInstallment(credit.dayFirstInstallment)
        //Primeiro fará uma verificação, se o campo 'customer' do credit inserido realmente existe no BD...
        credit.apply {
            customer =
                customerService.findById(credit.customer?.id!!) //então, customer receberá esse método do "CustomerService" injetado acima, q irá buscar por esse id no BD!
        }
        return this.creditRepository.save(credit) // Apenas se o método acima bater, retorna salvando o crédito no campo do customer em questão no BD
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> = this.creditRepository.findAllByCustomer(customerId) //método que recebe o id de um cliente e retorna uma lista dos creditos dele
    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        //Receberá o id do cliente e o codigo do crédito pra buscar
        val credit: Credit =
            (this.creditRepository.findByCreditCode(creditCode)     //Buscar se o crédito existe no BD pelo método do 'CreditRepository' injetado na class, passando o codigo dele...
                ?: throw BusinessException("Credit code $creditCode not found"))         //...Se existir, passa esse retorno p variável 'credit', Se não tiver, lança exceção criada de "não existe"
        //Faz uma verificação, se o id do cliente atrelado ao cod. de credito recebido na val 'credit' for igual ao id do customer(cliente) passado como param do metodo, retorna o crédito...
        return if (credit.customer?.id == customerId) credit
        else throw IllegalArgumentException("Contact the administration")       //...senão, não retorna e dispara exceção para reportar à administração!
    }

    private fun validDayFirstInstallment(dayFirstInstallment: LocalDate): Boolean {
        return if (dayFirstInstallment.isBefore(LocalDate.now().plusMonths(3))) true
        else throw BusinessException("the limit for the date of the first installment is up to 3 months from the current date.")
    }
}