package api.ranieriiuri.credit.application.system.repository

import api.ranieriiuri.credit.application.system.entity.Address
import api.ranieriiuri.credit.application.system.entity.Credit
import api.ranieriiuri.credit.application.system.entity.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@ActiveProfiles("test")                                         // indica o sufixo do arq com as configs do DB que usaremos
@DataJpaTest                                                       // usamos p testar repositorios JPA e criar integração de testes das aplicações spring
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    //setando autoconfigure do test de DB como 'none'(já que usaremos as configs do 'application-test.properties'
class CreditRepositoryTest {
    @Autowired
    lateinit var creditRepository: CreditRepository              // injetando o creditReṕository
    @Autowired
    lateinit var testEntityManager: TestEntityManager                // essa class do jpa nos ajuda a persistir dados no DB

    private lateinit var customer: Customer                         // declarando a val que instanciará um customer
    private lateinit var credit1: Credit                            // '' '' '' o primeiro credito desse customer
    private lateinit var credit2: Credit                            // '' '' '' o segundo '' '' ''

    @BeforeEach fun setup() {                                        // essa notação indica que, a cada vez que antes de rodarmos cada teste deverá. Neste caso...
        customer = testEntityManager.persist(buildCustomer())           //... persistir um customer (com a fun build criada abaixo)...
        credit1 = testEntityManager.persist(buildCredit(customer = customer))   //... o primeiro credito p este customer
        credit2 = testEntityManager.persist(buildCredit(customer = customer))   //... o segundo credito p este customer

    }

    @Test
    fun `should find credit by credit code`(){
        //given
        // criamos e atribuitremos credit codes conhecidos aos credits, pra q possamos pesquisar usando eles
        val creditCode1 = UUID.fromString("5f3ccaea-1120-4210-ab7d-0cadf25ac25c")
        val creditCode2 = UUID.fromString("a6ca1ce2-94d5-4e59-ba50-1ce71759ba2c")
        credit1.creditCode = creditCode1
        credit2.creditCode = creditCode2

        //when
        val fakeCredit1: Credit = creditRepository.findByCreditCode(creditCode1)!!      // testa o método(grantindo que os credit codes de retorno serão 'not null')
        val fakeCredit2: Credit = creditRepository.findByCreditCode(creditCode2)!!

        //then
        // comparando se estão mesmo retornando
        Assertions.assertThat(fakeCredit1).isNotNull
        Assertions.assertThat(fakeCredit2).isNotNull
        // se é mesmo o credit criado que está retornando
        Assertions.assertThat(fakeCredit1).isSameAs(credit1)
        Assertions.assertThat(fakeCredit2).isSameAs(credit2)
        // se é do mesmo customer
        Assertions.assertThat(fakeCredit1.customer).isSameAs(credit1.customer)
        Assertions.assertThat(fakeCredit2.customer).isSameAs(credit2.customer)
    }

    @Test
    fun `should find all credits by customer id`() {
        //given
        val customerId: Long = 1L               // será o 1, pq só tem 1 customer e sempre que acaba de rodar o test ele desaparece e sempre q rodado voltará a ser 1
        //when
        val creditList: List<Credit> = creditRepository.findAllByCustomer(customerId)
        //then
        Assertions.assertThat(creditList).isNotEmpty        // se a lista não retornará vazia
        Assertions.assertThat(creditList.size).isEqualTo(2) // testando se tem 2 credits na lista
        Assertions.assertThat(creditList).contains(credit1, credit2)    // se contém exatmente os 2 credits criados
    }

    // fun que cria um customer p testes
    private fun buildCustomer(
        firstName: String = "Iuri",
        lastName: String = "Ranieri",
        cpf: String = "12345678900",
        email: String = "iuriranierioliveira@gmail.com",
        password: String = "password",
        zipCode: String = "54517300",
        street: String = "Av almirante blabla",
        income: BigDecimal = BigDecimal.valueOf(15000.0)
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street,
        ),
        income = income
    )
}

// fun que cria creditos do customer p testes
private fun buildCredit(
    creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
    dayFirstInstallment: LocalDate = LocalDate.now().plusDays(30),
    numberOfInstallments: Int = 12,
    customer: Customer
): Credit =
    Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = customer
    )