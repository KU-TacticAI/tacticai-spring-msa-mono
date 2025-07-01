package com.example.gameservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = ["com.example"])
class GameServiceApplication

fun main(args: Array<String>) {
    runApplication<GameServiceApplication>(*args)
}
