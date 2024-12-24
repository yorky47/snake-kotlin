import pt.isel.canvas.Canvas

data class Game(
    val snake: Snake,
    val wall: List<Position>,
    val apple: Position?,
    val score: Int
)

/**
 * Adiciona uma nova posição de tijolo ao jogo.
 *
 * Gera uma nova posição aleatória e verifica se ela não colide com a parede existente,
 * a maçã ou o corpo da cobra. Se a posição for válida, adiciona-a à lista de tijolos.
 *
 * @return Uma nova instância de `Game` com a posição do tijolo atualizada.
 */
fun Game.gameWithNewPosition(): Game {
    while (true) {
        val pos = newpos() // Gera uma nova posição aleatória
        if (!this.checkPos(pos) && !snake.body.contains(pos)) {
            // Apenas adiciona se a posição não colidir com a parede, maçã ou corpo
            return this.copy(wall = this.wall + pos)
        }
    }
}

/**
 * Verifica se uma dada posição colide com a parede, cobra ou maçã.
 *
 * @param newPos A posição a ser verificada.
 * @return `true` se a posição colidir, `false` caso contrário.
 */
fun Game.checkPos(newPos: Position): Boolean {
    val tailPosition = Position(
        (snake.body.last().x - snake.headDirection.dx + canvasWidth) % canvasWidth,
        (snake.body.last().y - snake.headDirection.dy + canvasHeight) % canvasHeight
    )

    return this.wall.contains(newPos) || newPos == snake.body.first() || newPos == tailPosition || newPos == this.apple
}

/**
 * Atualiza o estado do jogo.
 *
 * Atualiza a posição da cobra, verifica colisões e atualiza a pontuação se a cobra comer a maçã.
 *
 * @return Uma nova instância de `Game` com o estado atualizado.
 */
fun Game.updateGame(): Game {
    val newSnake = this.snake.atualizaSnake(this.apple ?: Position(-5, -5), this.wall)

    // Atualiza a pontuação e gera uma nova maçã se a cobra comeu
    if (this.snakeEatsApple()) {
        return this.copy(
            apple = this.canDrawApple(), // Gera uma nova maçã
            snake = newSnake,     // Cobra cresce
            score = score + 1            // Incrementa a pontuação
        )
    }

    // Verifica colisões com a parede
    if (this.wall.contains(newSnake.body.first())) {
        return this // Cobra bateu, o jogo não avança
    }

    return this.copy(snake = newSnake) // Atualiza a cobra normalmente
}

/**
 * Verifica se a cobra comeu a maçã.
 *
 * @return `true` se a cobra comeu a maçã, `false` caso contrário.
 */
fun Game.snakeEatsApple(): Boolean {
    return this.snake.body.first() == this.apple
}

/**
 * Verifica se mais tijolos podem ser adicionados ao jogo.
 *
 * Itera por todas as posições no canvas e verifica se há uma posição válida para adicionar um tijolo.
 *
 * @return `true` se mais tijolos podem ser adicionados, `false` caso contrário.
 */
fun Game.canAddMoreBricks(): Boolean {
    for (x in 0 until canvasWidth) {
        for (y in 0 until canvasHeight) {
            val pos = Position(x, y)
            if (!this.checkPos(pos)) return true
        }
    }
    return false
}

/**
 * Gera uma nova posição válida para a maçã.
 *
 * Gera posições aleatórias até encontrar uma válida que não colida com a parede ou cobra.
 *
 * @return Uma posição válida para a maçã.
 */
fun Game.canDrawApple(): Position {
    var pos: Position
    do {
        pos = Position((0..canvasWidth - 1).random(), (0..canvasHeight - 2).random())
    } while (this.checkPos(pos) || this.snake.body.contains(pos))
    return pos
}

/**
 * Verifica se o jogo acabou.
 *
 * O jogo acaba se a cobra colidir consigo mesma.
 *
 * @return `true` se o jogo acabou, `false` caso contrário.
 */
fun Game.isGameOver(): Boolean {
    return snake.body.drop(1).any { it == snake.body.first() }
}

/**
 * Reseta o jogo para o estado inicial.
 *
 * Inicializa a cobra, parede, maçã e pontuação para os valores iniciais.
 *
 * @return Uma nova instância de `Game` com o estado inicial.
 */
fun Game.resetGame(): Game {
    val initialBody = mutableListOf(Position(5, 5), Position(6, 5), Position(7, 5)) // Corpo inicial de 3 segmentos
    return this.copy(snake = Snake(initialBody, toGrow = 1), wall = emptyList(), apple = canDrawApple(), score = 0)
}

/**
 * Verifica se a cobra pode se mover.
 *
 * Verifica se há movimentos possíveis sem colidir com a parede ou corpo da cobra.
 *
 * @return `true` se a cobra pode se mover, `false` caso contrário.
 */
fun Game.canMove(): Boolean {
    val head = snake.body.first()
    val possibleMoves = listOf(
        Position(head.x, head.y - 1),  // Cima
        Position(head.x, head.y + 1),  // Baixo
        Position(head.x - 1, head.y),  // Esquerda
        Position(head.x + 1, head.y)   // Direita
    )

    return possibleMoves.any { move ->
        // Verifica se o movimento está dentro do canvas
        move.x in 0 until canvasWidth &&
                move.y in 0 until canvasHeight &&
                // Verifica se não colide com paredes ou corpo
                !wall.contains(move) &&
                !snake.body.contains(move)
    }
}

/**
 * Atualiza a pontuação.
 *
 * Incrementa a pontuação se a cobra comer a maçã.
 *
 * @return A pontuação atualizada.
 */
fun Game.updateScore(): Int {
    return if (snakeEatsApple()) score + 1 else score
}

/**
 * Inicializa o jogo com o estado inicial.
 *
 * Configura a cobra, parede, maçã e pontuação iniciais.
 *
 * @return Uma nova instância de `Game` com o estado inicial.
 */
fun initializeGame(): Game {
    val initialSnakeBody = listOf(
        Position(5, 5),
        Position(6, 5),
        Position(7, 5)
    )

    val initialSnake = Snake(
        body = initialSnakeBody,
        headDirection = HeadDirection.MOVE_LEFT,
        toGrow = 2  // Começa com 5 elementos para crescer
    )

    val game = Game(
        snake = initialSnake,
        wall = inicialBrick,  // Tijolos iniciais definidos em Position.kt
        apple = Position(3, 3),
        score = 0
    )
    return game
}
