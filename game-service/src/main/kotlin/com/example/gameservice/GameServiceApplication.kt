package com.example.gameservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = ["com.example.gameservice", "com.example.commonmodule"
    ]
)
@EntityScan(
    basePackages = ["com.example.gameservice", "com.example.commonmodule"
    ]
)
@SpringBootApplication(
    scanBasePackages = ["com.example.gameservice", "com.example.commonmodule"
    ]
)
class GameServiceApplication

fun main(args: Array<String>) {
    runApplication<GameServiceApplication>(*args)
}
