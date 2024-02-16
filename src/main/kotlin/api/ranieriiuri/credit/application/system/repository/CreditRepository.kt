package api.ranieriiuri.credit.application.system.repository

import api.ranieriiuri.credit.application.system.entity.Credit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.hibernate.validator.constraints.UUID
import org.springframework.data.jpa.repository.Query

@Repository
interface CreditRepository: JpaRepository<Credit, Long> {
    fun findByCreditCode(creditCode: UUID): Credit?
    @Query(value = "SELECT * FROM CREDIT WHERE CUSTOMER_ID = ?1", nativeQuery = true)     //Passamos como 'value' a query sql, esse '= ?1' refere ao parametro que utilizaremos pra essa query...
                                                                                        //...que nesse caso, é apenas um(1) e o 'true'(é padrão)
    fun findAllByCustomer(customerId: Long): List<Credit>
}