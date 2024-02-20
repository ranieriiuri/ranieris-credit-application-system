package api.ranieriiuri.credit.application.system.controller

import api.ranieriiuri.credit.application.system.dto.CustomerDto
import api.ranieriiuri.credit.application.system.dto.CustomerUpdateDto
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
        val customerDto: CustomerDto = builderCustomerDto()
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
        customerRepository.save(builderCustomerDto().toEntity())      // usamos a fun builder com 'toEntity' p transformar esse Dto em entity e salvar no DB
        val customerDto: CustomerDto = builderCustomerDto()
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
        val customerDto: CustomerDto = builderCustomerDto(firstName = "")     // instanciando o Dto, mas passando um dos campos vazio
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

    @Test       // testando findById(merry way)
    fun `should find customer by id and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))                // p esse mock do get, usamos o método 'accept'
            .andExpect(MockMvcResultMatchers.status().isOk)     // se retorna 200
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("kakaxcnb@gmail.com"))   // comparando campos
            .andDo(MockMvcResultHandlers.print())               // imprime
    }

    @Test       // testando findById quando o customer do id passado não exist no DB
    fun `should not find customer by and return 400 status`() {
        //given & when & then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${2}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))  // se retorna essa msg
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())      // se tem 'timestamp'
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))   // se o status é o 400
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("BusinessException"))            // se retona a Exception q criamos
            .andDo(MockMvcResultHandlers.print())
    }

    @Test       // testando delete
    fun `should find and delete customer by id and return 204 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())           // cria um customer e coloca no DB
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")            // mocka uma req passando delete e o id do customer pra excluir
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)                                  // espera retorno de status 'no content'
            .andDo(MockMvcResultHandlers.print())

    }

    @Test       // testando delete no cenário de não conseguir delete por causa do id inválido
    fun `should find invalid id, not delete customer and return 400 status`() {
        //given & when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${1}")            // mocka uma req passando delete e o id do customer pra excluir
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)                                  // espera retorno de status 'no content'
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))  // se retorna essa msg
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())      // se tem 'timestamp'
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))   // se o status é o 400
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("BusinessException"))            // se retona a Exception q criamos
            .andDo(MockMvcResultHandlers.print())

    }

    @Test           // testando update customer (caminho feliz)
    fun `should find customer by id, update this customer and returns 200 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())       // cria o customer e insere no DB
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()                   // cria o customer Dto
        val valueAsString: String =
            objectMapper.writeValueAsString(customerUpdateDto)                                  // transforma o customer Dto em json p enviar na req
        //then & when
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")      // como usamos o '@RequestParam' a sintaxe é com a '?'
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))                                                                   // passa esse customer Dto transformado em json
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Apollo"))   // comparando campos
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(2000))   // comparando campos
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("4th Avenue Boulevard"))   // comparando campos
            .andDo(MockMvcResultHandlers.print())
    }

    @Test           // testando update (cenário de id inválido)
    fun `should not update a customer with invalid id and return 400 status`() {
        //given
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()                   // cria o customer Dto
        val valueAsString: String =
            objectMapper.writeValueAsString(customerUpdateDto)                                  // transforma o customer Dto em json p enviar na req
        //then & when
        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${1}")      // como usamos o '@RequestParam' a sintaxe é com a '?'
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )                                                                   // passa esse customer Dto transformado em json
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))  // se retorna essa msg
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())      // se tem 'timestamp'
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))   // se o status é o 400
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("BusinessException"))            // se retona a Exception q criamos
            .andDo(MockMvcResultHandlers.print())
    }

    // OBS.: Ainda podemos fazer testes do update, como 'passar campos em branco', entre outros...


    // fun que cria um Dto de teste
    fun builderCustomerDto(
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

    // fun que será usada nos testes de update (novo Dto)
    fun builderCustomerUpdateDto(
        firstName: String = "Apollo",
        lastName: String = "twelve",
        income: BigDecimal = BigDecimal.valueOf(2000),
        zipCode: String = "12345-000",
        street: String = "4th Avenue Boulevard"
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )

}