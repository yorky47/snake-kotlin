import CornerDirection.*
import pt.isel.canvas.Canvas
import pt.isel.canvas.GREEN
import pt.isel.canvas.RED
import pt.isel.canvas.WHITE

const val CELL_SIZE = 32
const val HEIGHT = 544 // 512 // 16 cells
const val WIDTH = 640 // 20 cells
const val canvasWidth = WIDTH / CELL_SIZE
const val canvasHeight = HEIGHT / CELL_SIZE

/**
 * Desenha o estado atual do jogo no canvas.
 *
 * - Apaga o canvas.
 * - Desenha as paredes (tijolos).
 * - Desenha a cobra e a maçã.
 * - Desenha a barra com a pontuação e o tamanho da cobra.
 * - Verifica se o jogo terminou e desenha a mensagem de vitória ou derrota.
 *
 * @param canvas O canvas onde o jogo será desenhado.
 */
fun Game.drawGame(canvas: Canvas) {
    canvas.erase()

    // Desenha as paredes (tijolos)
    this.wall.forEach { p ->
        canvas.drawImage("bricks.png", p.x * CELL_SIZE, p.y * CELL_SIZE, 32, 32)
    }

    // Desenha a cobra e a maçã
    this.drawSnake(canvas)
    this.drawApple(canvas)

    // Desenha a barra com a pontuação e o tamanho da cobra
    this.drawBar(canvas)

    // Verifica se o jogo terminou
    if (!this.canMove()) {
        // Se o jogo terminou, desenha a mensagem de vitória ou derrota
        if (this.snake.body.size >= 60) {
            drawWinMessage(canvas)  // Chama a função para desenhar a mensagem de vitória
        } else {
            drawLoseMessage(canvas)  // Chama a função para desenhar a mensagem de derrota
        }
    }
}

/**
 * Desenha a mensagem de vitória no canvas.
 *
 * @param canvas O canvas onde a mensagem será desenhada.
 */
fun drawWinMessage(canvas: Canvas) {
    canvas.drawText(500, 540, "You Win", GREEN, 25)
}

/**
 * Desenha a mensagem de derrota no canvas.
 *
 * @param canvas O canvas onde a mensagem será desenhada.
 */
fun drawLoseMessage(canvas: Canvas) {
    canvas.drawText(500, 540, "You Lose", RED, 25)
}

/**
 * Desenha a maçã no canvas, se estiver presente.
 *
 * @param canvas O canvas onde a maçã será desenhada.
 */
fun Game.drawApple(canvas: Canvas) {
    this.apple?.let {
        canvas.drawImage("snake.png|0,192,64,64", it.x * CELL_SIZE, it.y * CELL_SIZE, 32, 32)
    }
}

/**
 * Desenha a barra de pontuação na parte inferior do canvas.
 *
 * @param canvas O canvas onde a barra será desenhada.
 */
fun Game.drawBar(canvas: Canvas) {
    canvas.drawRect(0, 512, WIDTH, 80, 0x2c2c2c) // Fundo da barra
    canvas.drawText(150, 540, "Score: ${this.score}", WHITE, 25)
    canvas.drawText(10, 540, "Size: ${this.snake.body.size}", WHITE, 25)
}

/**
 * Desenha a cobra no canvas com base na sua direção e posição.
 *
 * @param canvas O canvas onde a cobra será desenhada.
 */
fun Game.drawSnake(canvas: Canvas) {
    // Desenha a cabeça da cobra com base na direção
    val headImage = when (snake.headDirection) {
        HeadDirection.MOVE_LEFT -> "snake.png|192,64,64,64"
        HeadDirection.MOVE_UP -> "snake.png|256,64,64,64"
        HeadDirection.MOVE_RIGHT -> "snake.png|256,0,64,64"
        HeadDirection.MOVE_DOWN -> "snake.png|192,0,64,64"
    }

    canvas.drawImage(headImage, this.snake.body.first().x * CELL_SIZE, this.snake.body.first().y * CELL_SIZE, 32, 32)

    // Desenha os segmentos do corpo
    for (i in 1 until snake.body.size - 1) {
        val current = snake.body[i]
        val previous = snake.body[i - 1]

        // Imagem padrão do corpo
        val bodyImage = when {
            current.x > previous.x -> "snake.png|64,0,64,64"  // Movendo para a direita
            current.x < previous.x -> "snake.png|64,0,64,64"  // Movendo para a esquerda
            current.y > previous.y -> "snake.png|128,64,64,64" // Movendo para baixo
            else -> "snake.png|128,64,64,64"  // Movendo para cima
        }

        // Verificar curvas
        if (i < snake.body.size - 1) {
            val next = snake.body[i + 1]
            val cornerImage = getCornerImage(previous, current, next)
            if (cornerImage != null) {
                canvas.drawImage(cornerImage, current.x * CELL_SIZE, current.y * CELL_SIZE, 32, 32)
                continue  // Não desenha o corpo aqui, pois a curva já foi desenhada
            }
        }

        // Desenha o corpo normal
        canvas.drawImage(bodyImage, current.x * CELL_SIZE, current.y * CELL_SIZE, 32, 32)
    }

    // Desenha a cauda
    if (snake.body.size > 1) {
        val tail = snake.body.last()
        val previousToTail = snake.body[snake.body.size - 2]
        val tailImage = when {
            tail.x < previousToTail.x -> "snake.png|256,128,64,64" // Movendo para a direita
            tail.x > previousToTail.x -> "snake.png|192,192,64,64" // Movendo para a esquerda
            tail.y > previousToTail.y -> "snake.png|192,128,64,64" // Movendo para baixo
            else -> "snake.png|256,192,64,64"  // Movendo para cima
        }

        canvas.drawImage(tailImage, tail.x * CELL_SIZE, tail.y * CELL_SIZE, 32, 32)
    }
}

/**
 * Determina a imagem correta para uma curva com base nas posições dos segmentos anterior, atual e próximo.
 *
 * @param previous A posição do segmento anterior.
 * @param current A posição do segmento atual.
 * @param next A posição do próximo segmento.
 * @return A imagem correspondente à curva, ou `null` se não for uma curva.
 */
fun getCornerImage(previous: Position, current: Position, next: Position): String? {
    val cornerDir = getLastDirection(previous, current, next)
    return when (cornerDir) {
        DOWN_RIGHT -> "snake.png|0,64,64,64" // De baixo para direita
        DOWN_LEFT -> "snake.png|0,0,64,64"  // De baixo para esquerda
        UP_RIGHT -> "snake.png|128,128,64,64" // De cima para direita
        UP_LEFT -> "snake.png|128,0,64,64"  // De cima para esquerda
        RIGHT_UP -> "snake.png|128,128,64,64" // De direita para cima
        RIGHT_DOWN -> "snake.png|128,0,64,64"  // De direita para baixo
        LEFT_UP -> "snake.png|0,64,64,64" // De esquerda para cima
        LEFT_DOWN -> "snake.png|0,0,64,64" // De esquerda para baixo
        else -> null
    }
}

/**
 * Determina a direção da curva com base nas posições dos segmentos anterior, atual e próximo.
 *
 * @param previous A posição do segmento anterior.
 * @param current A posição do segmento atual.
 * @param next A posição do próximo segmento.
 * @return A direção da curva.
 */
fun getLastDirection(previous: Position, current: Position, next: Position): CornerDirection {
    val dx1 = current.x - previous.x
    val dy1 = current.y - previous.y
    val dx2 = next.x - current.x
    val dy2 = next.y - current.y

    return when {
        dx1 > 0 && dy2 < 0 -> CornerDirection.RIGHT_UP // Movendo para a direita e virando para cima
        dx1 > 0 && dy2 > 0 -> CornerDirection.RIGHT_DOWN // Movendo para a direita e virando para baixo
        dx1 < 0 && dy2 < 0 -> CornerDirection.LEFT_UP // Movendo para a esquerda e virando para cima
        dx1 < 0 && dy2 > 0 -> CornerDirection.LEFT_DOWN // Movendo para a esquerda e virando para baixo
        dy1 < 0 && dx2 > 0 -> CornerDirection.UP_RIGHT // Movendo para cima e virando para direita
        dy1 < 0 && dx2 < 0 -> CornerDirection.UP_LEFT // Movendo para cima e virando para esquerda
        dy1 > 0 && dx2 > 0 -> CornerDirection.DOWN_RIGHT // Movendo para baixo e virando para direita
        dy1 > 0 && dx2 < 0 -> CornerDirection.DOWN_LEFT // Movendo para baixo e virando para esquerda
        else -> CornerDirection.INVALID
    }
}

