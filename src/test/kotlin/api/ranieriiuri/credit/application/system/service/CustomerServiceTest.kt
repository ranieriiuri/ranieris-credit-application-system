package api.ranieriiuri.credit.application.system.service

import api.ranieriiuri.credit.application.system.entity.Address
import api.ranieriiuri.credit.application.system.entity.Customer
import api.ranieriiuri.credit.application.system.exception.BusinessException
import api.ranieriiuri.credit.application.system.repository.CustomerRepository
import api.ranieriiuri.credit.application.system.service.impl.CustomerService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.Optional
import java.util.Random

//@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)                                                  // indica ao spring que iremos usar o Mockk para mockar
class CustomerServiceTest {
    @MockK
    lateinit var customerRepository: CustomerRepository                      // mockando o customerRepository(fake)
    @InjectMockKs
    lateinit var customerService: CustomerService                     // injetando a class que de fato queremos testar

    @Test                                                // testando 'save'
    fun `should create customer`() {
        //given
        val fakeCustomer: Customer = buildCustomer()
        every { customerRepository.save(any()) } returns fakeCustomer               // 'every' é um método do Mockk. Em suma estamos falando que cada vez(every) que chamarmos o customerRepository passando qualquer customer retorne o 'fakeCustomer'
        //when
        val actual: Customer =
            customerService.save(fakeCustomer)                   // nessa fase passamos exatamente o cenario que definimos na 'given' acima
        //then
        Assertions.assertThat(actual).isNotNull                                     // aqui usamos o método 'assertThat' da class Assertions, do 'assertj core api' para conferir se a asserção passada realmente cumpre o que se espera
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }                // 'verify' é um método do mockk que usaremos p verificar se esse fakeCustomer só será salvo 1 vez(já que esse é o comportamento esperado)
    }

    @Test                                               // testando 'findById' (caminho feliz)
    fun `should find customer by id`() {
        //given
        val fakeId: Long =
            Random().nextLong()                                       // vai gerar um id randômico p usarmos nos testes
        val fakeCustomer: Customer =
            buildCustomer(id = fakeId)                      // criamos o fake customer com a fun designada p tal, só q mudamos o id pra o id da 'val fakeId'
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        //when
        val actual: Customer = customerService.findById(fakeId)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual)
            .isExactlyInstanceOf(Customer::class.java)     // se o customer em 'actual' é uma instância exata da class Customer
        Assertions.assertThat(actual)
            .isSameAs(fakeCustomer)                        // se o customer salvo no DB é o mesmo fakeCustomer
        verify(exactly = 1) { customerRepository.findById(fakeId) }                  // se ele só está sendo chamado 1 vez
    }

    @Test                                                // testando 'findById' (caminho com exceções)
    fun `should not find customer by id and throw BusinessException`() {
        //given
        val fakeId: Long = Random().nextLong()
        every { customerRepository.findById(fakeId) } returns Optional.empty()      // o retorno será um Optional vazio, justamente p teste nesse cenário onde dispara exceção
        //when e then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)         // vai verificar se a exceção disparada é desse tipo
            .isThrownBy { customerService.findById(fakeId) }                        //... se está sendo lançada pelo findById
            .withMessage("Id $fakeId not found")                                    //... e se vem com a mensagem exata que tem no retorno de exceção nesses casos lá no método
        verify(exactly = 1) { customerRepository.findById(fakeId) }                  //... se é chamado apenas uma vez
    }

    @Test                                           // testando o método delete, que utiliza o findById p achar o customer em questão, retorna um Customer e o deleta pela val que o retornou
    fun `should delete customer by id`() {
        //given
        val fakeId: Long = Random().nextLong()      // cria um id fake para 'achar o customer no db'
        val fakeCustomer: Customer =
            buildCustomer(id = fakeId)     // simula o retoro desse customer do DB pelo id achado pelo findById
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)     // define o comportamento qnd mock esse customer q será retornado
        every { customerRepository.delete(fakeCustomer) } just runs     // define o comportamento qnd mocka a exclusão desse customer em questão e não retorna nada (usamos 'just runs' para indicar isto)
        //when
        customerService.delete(fakeId)                  // chama esse método para mockar a exclusão
        //then
        verify(exactly = 1) { customerRepository.findById(fakeId) }          // como o delete não tem retorno, verificamos apenas se o findById e o delete serão chamados apenas 1 vez!
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }

    companion object {
         fun buildCustomer(
            firstName: String = "Iuri",
            lastName: String = "Ranieri",
            cpf: String = "05844195469",
            email: String = "iuriranierioliveira@gmail.com",
            password: String = "1234",
            zipCode: String = "54517300",
            street: String = "Av Almirante Paulo Moreira",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            id: Long = 1
        ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                zipCode = zipCode,
                street = street
            ),
            income = income,
            id = id
        )
    }
}