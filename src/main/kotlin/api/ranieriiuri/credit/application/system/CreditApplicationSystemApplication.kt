package api.ranieriiuri.credit.application.system

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

@SpringBootApplication
class CreditApplicationSystemApplication

fun main(args: Array<String>) {
	val application = runApplication<CreditApplicationSystemApplication>(*args)
	println("\nRanieri's api is running...\n >> Relax and takes a coffee! ☕ \n")
	}
