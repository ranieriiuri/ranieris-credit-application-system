package api.ranieriiuri.credit.application.system.exception

import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@RestControllerAdvice                       // indica ao spring que essa class conterá os métodos de controle das exceptions
class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)       // notação q indica que essa fun gerencia exceções do tipo 'MethodArgum...' que é uma 'java class' e é o tipo de exceção padrão lançada qnd funcionalidades tem a notação '@Valid' e essa validação falha!
    fun handlerValidException(exception: MethodArgumentNotValidException): ResponseEntity<ExceptionDetails> {  // receberá uma exceção do tipo designado e retornará uma 'ResponseEntity' do tipo 'ExceptionDetails'(classe criada na mesma pasta para definir como será esse detalhamento)
        val errors: MutableMap<String, String?> =
            HashMap()     // inicializa a variável que receberá a lista de erros disparados, contando que chave(será String) e valor (poderá ser String ou null) já instanciando uma HashMap pra armazenar chave-valor...
        exception.bindingResult.allErrors.stream()
            .forEach {    // na exceção recebida, procura os resultados de todos os erros e cria uma stream, onde, para cada...
                    error: ObjectError ->                               // ...erro recebido...
                val fieldName: String =
                    (error as FieldError).field // extrai o nome do campo de onde advém o erro, assumindo que todo erro que virá é uma instancia do FieldError(representa validação de erros associadas com campos específicos)
                val messageError: String? =
                    error.defaultMessage    // obtém a mensagem padrão do erro em questão (defaultMessage é uma instancia de FieldError, assim como 'field')
                errors[fieldName] =
                    messageError                    // adiciona essa obtenção de 'chave-valor' a 'errors' criada acima
            }

        // Esta é uma forma alternativa de criar a ResponseEntity de retorno!
        return ResponseEntity(                                  // retorna ao front uma ResposeEntity, que será uma ExceptionDetails(como criada),
            ExceptionDetails(                                   // passando um ExceptionDetails (como a ExceptionDetails espera):
                title = "Bad Request! Consult the documentation",   // um titulo,
                timestamp = LocalDateTime.now(),                    // a hora do erro,
                status = HttpStatus.BAD_REQUEST.value(),            // um status numérico (será 0, indicando Bad Request)
                exception = exception.javaClass.simpleName,         // o nome da exceção(que é uma java class)
                details = errors                                    // a HashMap(nova instância chave-valor) criada a partir do processo feito com a stream
            ), HttpStatus.BAD_REQUEST                               // ... e tbm o status padrõa ao front.
        )
    }

    @ExceptionHandler(DataAccessException::class)       // Faremos o mesmo processo, só que agr, para tratar a DataAccessException (que trata de quando tentamos inserir um mesmo objeto 2x
    fun handlerValidException(exception: DataAccessException): ResponseEntity<ExceptionDetails> {

        // Aqui fizemos a forma mais parecida com os outros retornos do ResponseEntity(encadeado com '.status(...).body(...)
        //... ou seja, podemos passar o status code que será retornado desta forma(encadeado) ou como um segundo arqumento (como fiz acima)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ExceptionDetails(
                    title = "Conflict! Consult the documentation",
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.CONFLICT.value(),                                               // usamos o status de retorno 'conflict' ao invés de Bad Request
                    exception = exception.javaClass.simpleName,
                    details = mutableMapOf(exception.cause.toString() to exception.message)             // passamos como a causa como chave, e a mensagem como valor, levando em conta que esse tipo de exceção trata um erro de cada vez!
                )
            )
    }
    // handler p a Exception criada (BusinessException)
    @ExceptionHandler(BusinessException::class)       // trataremos a exception criada 'BusinessException'
    fun handlerValidException(exception: BusinessException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ExceptionDetails(
                    title = "Bad Request! Consult the documentation",
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    exception = exception.javaClass.simpleName,
                    details = mutableMapOf(exception.cause.toString() to exception.message)             // passamos como a causa como chave, e a mensagem como valor, levando em conta que esse tipo de exceção trata um erro de cada vez!
                )
            )
    }
    @ExceptionHandler(IllegalArgumentException::class)       // tratando IllegalStateException igualmente os outros handlers
    fun handlerValidException(exception: IllegalArgumentException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ExceptionDetails(
                    title = "Bad Request! Consult the documentation",
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    exception = exception.javaClass.simpleName,
                    details = mutableMapOf(exception.cause.toString() to exception.message)
                )
            )
    }
}