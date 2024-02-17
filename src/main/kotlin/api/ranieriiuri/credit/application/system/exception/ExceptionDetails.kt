package api.ranieriiuri.credit.application.system.exception

import java.time.LocalDateTime

data class ExceptionDetails(
    val title: String,
    val timestamp: LocalDateTime,
    val status: Int,
    val exception: String,
    val details: MutableMap<String, String?>            // Detalhará qual foi o campo em que ocorreu a exception, será uma MutableMap formada por chave(será uma String) e valor(String que pode ser null)
)
