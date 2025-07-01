package com.example.gameservice.game_session.entity

import jakarta.persistence.*

@Entity
@Table(name = "AIExecutionLog")
data class AIExecutionLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "executiontimems")
    val executionTimeMs: Int?,

//    @Column(name = "status")
//    val status: ExecutionStatus?,

    @Column(name = "logoutput")
    val logOutput: String?,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "gamesession_id")
    val gameSessionId: Long? = null,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "aiagent_id")
    val aiAgentId: Long? = null
)