package api.ranieriiuri.credit.application.system.controller

import api.ranieriiuri.credit.application.system.dto.CustomerDto
import api.ranieriiuri.credit.application.system.entity.Customer
import api.ranieriiuri.credit.application.system.repository.CustomerRepository
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

@SpringBootTest                             // indica que essa classe vai subir no contexto spring
@ActiveProfiles("test")
@AutoConfigureMockMvc                       // teremos como mockar nossas requisições
@ContextConfiguration                       // ajuda a subir o contexto do spring boot
class CustomerResourceTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository
    @Autowired
    private lateinit var mockMvc: MockMvc                        // com está injeção podemos mockar as requisições
    @Autowired
    private lateinit var objectMapper: ObjectMapper              // qnd quisermos passar uma class como json na req, essa class nos ajudará

    // colocando qual url(endpoint) estaremos sempre acessando a partir dessa class de teste, através do companion object, tipo um atributo estático
    companion object {
        const val URL: String = "/api/customers"
    }

    // ambas as fun de setup abaixo limparão o DB, só que, uma antes de cada teste e a outra depois
    @BeforeEach
    fun setup() = customerRepository.deleteAll()
    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    // test save I (caminho feliz)
    @Test
    fun `should create a customer and returns 201 status`() {
        //given
        val customerDto: CustomerDto = buildCustomerDto()
        val valueAsString: String =
            objectMapper.writeValueAsString(customerDto)                // transforma o customer dto criado em json para a req
        //when e then
        // Explicando abaixo:
        // iremos mockar a req, usando esses métodos do MockMkvc setando:
        // - post(com o endpoint do recurso); - contentType(application json); - content (passando o Dto que temos);
        // ... E encadeamos o que esperamos: - andExpect(que o resultado seja o status 'is created'; verificar se o first name está correto) e mais 'andExpect()' p todos os campos

        mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Caleb"))        // podemos fazer muitos outros desses p verificar cada campo
            .andDo(MockMvcResultHandlers.print())               // esse permite imprimir tudo no teste !
    }

    //Teste p qnd tentamos salvar um customer com o mesmo cpf e email de um customer já salvo no DB
    @Test
    fun `should not save a customer with same cpf and return 409 status`() {
        //given
        customerRepository.save(buildCustomerDto().toEntity())      // usamos a fun builder com 'toEntity' p transformar esse Dto em entity e salvar no DB
        val customerDto: CustomerDto = buildCustomerDto()
        val valueAsString: String =
            objectMapper.writeValueAsString(customerDto)                // transforma o customer dto criado em json para a req
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isConflict)       // mocka a req esperando um retorno 409 (Conflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))  // se retorna essa msg
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())      // se tem 'timestamp'
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))   // se o status é o 409
            .andExpect(MockMvcResultMatchers.jsonPath("$.details").isNotEmpty)      // se 'details' não esta vazia
            .andDo(MockMvcResultHandlers.print())       // imprime
    }


    @Test           // testando salvamento com campos vazios
    fun `should not save a customer with empty fields and return 400 status`() {
        //given
        val customerDto: CustomerDto = buildCustomerDto(firstName = "")     // instanciando o Dto, mas passando um dos campos vazio
        val valueAsString: String =
            objectMapper.writeValueAsString(customerDto)
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)       // mocka a req esperando um retorno 400 (Bad Request)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))  // se retorna essa msg
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())      // se tem 'timestamp'
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))   // se o status é o 400
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("MethodArgumentNotValidException"))      // se é esse tipo de exceção que está sendo retornada
            .andExpect(MockMvcResultMatchers.jsonPath("$.details").isNotEmpty)      // se 'details' não esta vazia
            .andDo(MockMvcResultHandlers.print())       // imprime
    }
    // fun que cria um Dto de teste
    fun buildCustomerDto(
        firstName: String = "Caleb",
        lastName: String = "Neves Batista",
        cpf: String = "05844195469",
        email: String = "kakaxcnb@gmail.com",
        income: BigDecimal = BigDecimal.valueOf(5000),
        password: String = "caleb",
        zipCode: String = "12345-678",
        street: String = "Main Street"
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        income = income,
        email = email,
        password = password,
        zipCode = zipCode,
        street = street
    )


}