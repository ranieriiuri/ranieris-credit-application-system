package api.ranieriiuri.credit.application.system.entity

import api.ranieriiuri.credit.application.system.enummeration.Status
import jakarta.persistence.*
import java.util.UUID
import java.math.BigDecimal
import java.time.LocalDate
@Entity
//@Table(name = "Crédito")
data class Credit(
    @Column(nullable =  false, unique = true) val creditCode: UUID = UUID.randomUUID(),
    @Column(nullable =  false) val creditValue: BigDecimal = BigDecimal.ZERO,
    @Column(nullable =  false) val dayFirstInstallment: LocalDate,
    @Column(nullable =  false) val numberOfInstallments : Int = 0,
    @Enumerated val status: Status = Status.IN_PROGRESS,
    @ManyToOne val customer: Customer? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)
