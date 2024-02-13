package api.ranieriiuri.credit.application.system.repository

import api.ranieriiuri.credit.application.system.entity.Credit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditRepository: JpaRepository<Credit, Long> {
}