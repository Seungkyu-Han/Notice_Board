package org.seungkyu.board

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SecurityApplication

fun main(args: Array<String>) {
    SpringApplication.run(SecurityApplication::class.java, *args)
}