package api.ranieriiuri.credit.application.system.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [MaxFutureDateValidator::class])
annotation class MaxFutureDate(
    val message: String = "the limit for the date of the first installment is up to 3 months from the current date.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
