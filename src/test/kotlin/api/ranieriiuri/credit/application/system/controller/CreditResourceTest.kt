package api.ranieriiuri.credit.application.system.controller

import api.ranieriiuri.credit.application.system.dto.CreditDto
import api.ranieriiuri.credit.application.system.entity.Address
import api.ranieriiuri.credit.application.system.entity.Credit
import api.ranieriiuri.credit.application.system.entity.Customer
import api.ranieriiuri.credit.application.system.repository.CreditRepository
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
import java.util.*

@SpringBootTest
@ActiveProfiles("test")             // utilizará as infos do DB definidas no arq application de sufixo 'test'
@ContextConfiguration               //
@AutoConfigureMockMvc              // pra mockar as reqs
class CreditResourceTest {
    @Autowired
    lateinit var customerRepository: CustomerRepository          // injetando o DB dp customer

    @Autowired
    lateinit var customerService: CustomerService

    @Autowired
    lateinit var creditRepository: CreditRepository              // injeta o DB do credit

    @Autowired
    lateinit var mockMvc: MockMvc                                // que mockará nossas reqs

    @Autowired
    lateinit var objectMapper: ObjectMapper                      // transformará as entities em json p enviar na req mockada

//    val creditToFindByCreditCode: Credit = Credit(
//        creditCode = UUID.fromString("5f3ccaea-1120-4210-ab7d-0cadf25ac25c"),
//        creditValue = BigDecimal.valueOf(3500.0),
//        dayFirstInstallment = LocalDate.now().plusMonths(2),
//        numberOfInstallments = 20,
//        id = 1
//    )

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

    // testing in the context of invalid day of first installment
    @Test
    fun `should not create a credit with dayFirstInstallment beyond 3 months and return 400 status`() {
        //given
        val creditDto: CreditDto = buildCreditDto(
            dayFirstInstallment = LocalDate.now().plusMonths(4)
        )            // passando acima do limit p primeiro dia de pgmto das parcelas
        val valueAsString: String =
            objectMapper.writeValueAsString(creditDto)              // passando o dto criado p o formato json
        //when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/credits").contentType(MediaType.APPLICATION_JSON).content(valueAsString)
        )     // mockando a req
            .andExpect(MockMvcResultMatchers.status().isBadRequest)                        // esperando 400...
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation")
            )  // se retorna essa msg
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())      // se tem 'timestamp'
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))   // se o status é o 400
            .andExpect(MockMvcResultMatchers.jsonPath("$.details").isNotEmpty)      // se 'details' não esta vazia
            .andDo(MockMvcResultHandlers.print())       // imprime
    }

    // testing in the context of invalid number of installments
    @Test
    fun `should not create a credit with number of installments beyond 48 and return 400 status`() {
        //given
        val creditDto: CreditDto =
            buildCreditDto(numberOfInstallments = 49)            // passando acima do limit p numero de parcelas
        val valueAsString: String =
            objectMapper.writeValueAsString(creditDto)              // passando o dto criado p o formato json
        //when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/credits").contentType(MediaType.APPLICATION_JSON).content(valueAsString)
        )     // mockando a req
            .andExpect(MockMvcResultMatchers.status().isBadRequest)                        // esperando 400...
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("MethodArgumentNotValidException")
            )      // se é esse tipo de exceção que está sendo retornada
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation")
            )  // se retorna essa msg
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())      // se tem 'timestamp'
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))   // se o status é o 400
            .andExpect(MockMvcResultMatchers.jsonPath("$.details").isNotEmpty)      // se 'details' não esta vazia
            .andDo(MockMvcResultHandlers.print())       // imprime
    }

    // testing in the context of customer id not exists
    @Test
    fun `should not create a credit with customer id not exists on DB`() {
        //given
        val creditDto: CreditDto =
            buildCreditDto(customerId = 2)       // considerando que o id do customer persistido no db em cada execução de método de teste sempre será '1', passaremos um id q não existe
        val valueAsString: String =
            objectMapper.writeValueAsString(creditDto)              // passando o dto criado p o formato json
        //when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/credits").contentType(MediaType.APPLICATION_JSON).content(valueAsString)
        )     // mockando a req
            .andExpect(MockMvcResultMatchers.status().isBadRequest)                        // esperando 400...
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("BusinessException")
            )      // se é esse tipo de exceção que está sendo retornada
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation")
            )  // se retorna essa msg
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())      // se tem 'timestamp'
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))   // se o status é o 400
            .andExpect(MockMvcResultMatchers.jsonPath("$.details").isNotEmpty)      // se 'details' não esta vazia
            .andDo(MockMvcResultHandlers.print())       // imprime
    }

    // testando findAllByCustomer (caminho feliz)
    @Test
    fun `should get all credits by customer id and return 200 status`() {
        //given
        val creditDto1: CreditDto =
            buildCreditDto()       // considerando que o id do customer persistido no db em cada execução de método de teste sempre será '1', passaremos um id q não existe
        val creditDto2: CreditDto = buildCreditDto(creditValue = BigDecimal.valueOf(5100), numberOfInstallments = 40)
        val creditEntity1 =
            creditRepository.save(creditDto1.toEntity())            // persistindo no db esses creditos ao customer já existente
        val creditEntity2 = creditRepository.save(creditDto2.toEntity())
        //val valueAsString: String = "[$creditDto1Json, $creditDto2Json]"

        //when & then
        // mockando a req
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/credits?customerId=1").contentType(MediaType.APPLICATION_JSON)
            //.content(valueAsString) |--> enviamos esse conteúdo antes (nas vals acima) já que o método só busca a lista de créditos já existente no db
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[*].creditCode").isArray)       // se retorna um array de credit codes
            .andExpect(MockMvcResultMatchers.jsonPath("$[*].creditValue").isArray)      // se retorna um array de credit values
            .andDo(MockMvcResultHandlers.print())
    }

    // testando o findAllByCustomerId quando o id do customer não existe
    @Test
    fun `should not find customer by id and return 204 status`() {
        // given & when & then              // não precisamos persistir nada no db, já que vamos buscar um id inexistente
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/credits?customerId=2")     // passando id inexistente (lembrando que o '1' é sempre persistido pelo '@BeforeEach'
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    // testando o findAllByCustomerId qnd a lista estiver vazia
    @Test
    fun `should find empty credit list of a customer and return 204 status`() {
        // given & when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/credits?customerId=1")     // passando id existente, mas a lista de creditos estará vazia
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())

    }

    // testando findByCreditCode (happy path)
    @Test
    fun `should find credit by credit code and return 200`() {
    //given
        val credit: Credit = buildCreditDto().toEntity()                        // usando fun criada p criar um credit dto e já transformando em entity
        credit.creditCode = UUID.fromString("5f3ccaea-1120-4210-ab7d-0cadf25ac25c")     // definindo um credit code conhecido p ele
        creditRepository.save(credit)                   // persistindo no db (lembrando que o id do customer que existe no db é 1 e já está definido na fun build desse credit)
    //when & then

//        val creditCustomer = 1L
//        creditToFindByCreditCode.creditCode = creditCode
//        creditToFindByCreditCode.customer?.id = creditCustomer
//        creditRepository.save(creditToFindByCreditCode)

    //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/credits/5f3ccaea-1120-4210-ab7d-0cadf25ac25c?customerId=1") // passamos credit code e customer id
            .accept(MediaType.APPLICATION_JSON))                // p esse mock do get, usamos o método 'accept'
            .andExpect(MockMvcResultMatchers.status().isOk)     // se retorna 200
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").value("5f3ccaea-1120-4210-ab7d-0cadf25ac25c"))   // comparando campos
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(1500))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("iuriranierioliveira@gmail.com"))
            .andDo(MockMvcResultHandlers.print())               // imprime



    }

    // testando find by credit code

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