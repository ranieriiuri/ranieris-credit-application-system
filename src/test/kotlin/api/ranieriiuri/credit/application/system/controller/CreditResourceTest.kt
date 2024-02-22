package api.ranieriiuri.credit.application.system.controller

import api.ranieriiuri.credit.application.system.dto.CreditDto
import api.ranieriiuri.credit.application.system.entity.Address
import api.ranieriiuri.credit.application.system.entity.Customer
import api.ranieriiuri.credit.application.system.repository.CustomerRepository
import api.ranieriiuri.credit.application.system.service.impl.CustomerService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")             // utilizará as infos do DB definidas no arq application de sufixo 'test'
@ContextConfiguration               //
@AutoConfigureMockMvc              // pra mockar as reqs
class CreditResourceTest {
    @Autowired
    lateinit var customerRepository: CustomerRepository          // injetando o DB dp customer
    @Autowired
    lateinit var customerService: CustomerService

    //@Autowired lateinit var creditRepository: CreditRepository              // injeta o DB do credit

    @Autowired
    lateinit var mockMvc: MockMvc                                // que mockará nossas reqs

    @Autowired
    lateinit var objectMapper: ObjectMapper                      // transformará as entities em json p enviar na req mockada

    @BeforeEach
    fun setup() {                                        // essa notação indica que, a cada vez que antes de rodarmos cada teste deverá persistir um customer no DB
        customerService.save(buildCustomer())

    }

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()     // a cada termino de teste limpa o DB

    // save(caminho feliz)
    @Test
    fun `should create a credit associated to a existent customer and return 201 status`() {
        //given
        val creditDto: CreditDto = buildCreditDto()                                       // criando um credit dto
        val valueAsString: String =
            objectMapper.writeValueAsString(creditDto)              // passando o dto criado p o formato json
        //when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/credits").contentType(MediaType.APPLICATION_JSON).content(valueAsString)
        )     // mockando a req
            .andExpect(MockMvcResultMatchers.status().isCreated)                        // esperando 201...
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(12)
            )      // conferindo campos de retorno da CreditView...
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("iuriranierioliveira@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(1000.00))
            .andDo(MockMvcResultHandlers.print())               // imprimindo
    }


    fun buildCustomer(
        firstName: String = "Iuri",
        lastName: String = "Ranieri",
        cpf: String = "05844195469",
        email: String = "iuriranierioliveira@gmail.com",
        password: String = "1234",
        zipCode: String = "54517300",
        street: String = "Av Almirante Paulo Moreira",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        id: Long = 1L
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

    fun buildCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(1500),
        dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(1),
        numberOfInstallments: Int = 12,
        customerId: Long = 1L
    ): CreditDto = CreditDto(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

}