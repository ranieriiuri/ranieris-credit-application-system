package api.ranieriiuri.credit.application.system.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDate

class MaxFutureDateValidator : ConstraintValidator<MaxFutureDate, LocalDate> {
    override fun isValid(value: LocalDate?, context: ConstraintValidatorContext): Boolean {
        val currentDate = LocalDate.now()
        val threeMonthsLater = currentDate.plusMonths(3)
        return value == null || !value.isAfter(threeMonthsLater)
    }
}
