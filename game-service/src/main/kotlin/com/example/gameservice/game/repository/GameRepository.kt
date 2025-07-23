package com.example.gameservice.game.repository

import com.example.commonmodule.exceptions.NotFoundException
import com.example.gameservice.exceptions.GameException
import com.example.gameservice.game.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameRepository : JpaRepository<Game, Long> {
    fun save(game: Game): Game;

    // 모든 "삭제되지 않은" 게임 조회
    fun findAllByDeletedAtIsNull(): List<Game>

    // 기본 메서드 (default method)
    fun findGameById(id: Long): Game {
        return this.findByIdAndDeletedAtIsNull(id)
            .orElseThrow { NotFoundException(GameException.NOT_FOUND_AI) }
    }

    // 커스텀 조건 쿼리
    fun findByIdAndDeletedAtIsNull(id: Long): Optional<Game>
}
