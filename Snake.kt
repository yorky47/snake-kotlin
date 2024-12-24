/**
 * Define a classe `Snake`, que representa a cobra no jogo,
 * juntamente com suas funções associadas
 * para atualização de direção e posição.
 * Este ficheiro também contém a enum `Direction` que define
 * as direções possíveis da cobra.
 */

data class Snake(val body: List<Position>, val headDirection: HeadDirection = HeadDirection.MOVE_LEFT, val toGrow: Int)

/**
 * A enumeração `HeadDirection` define as direções possíveis para a cabeça da cobra se mover em um plano bidimensional.
 *
 * @property dx Representa a alteração na coordenada x ao mover-se nesta direção.
 * @property dy Representa a alteração na coordenada y ao mover-se nesta direção.
 *
 * MOVE_UP: Move-se para cima, incrementando a coordenada y.
 * MOVE_DOWN: Move-se para baixo, decrementando a coordenada y.
 * MOVE_LEFT: Move-se para a esquerda, decrementando a coordenada x.
 * MOVE_RIGHT: Move-se para a direita, incrementando a coordenada x.
 */
enum class HeadDirection(val dx: Int = -1, val dy: Int = 0) {
    MOVE_UP(0, 1),
    MOVE_DOWN(0, -1),
    MOVE_LEFT(-1, 0),
    MOVE_RIGHT(1, 0);

    /**
     * Verifica se a direção atual é oposta a outra direção.
     *
     * @param other A outra direção a ser comparada.
     * @return `true` se as direções são opostas, `false` caso contrário.
     */
    fun isOpposite(other: HeadDirection): Boolean {
        return this.dx + other.dx == 0 && this.dy + other.dy == 0
    }
}

enum class CornerDirection { INVALID, LEFT_UP, RIGHT_UP, LEFT_DOWN, RIGHT_DOWN, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT }

/**
 * Atualiza a posição da cobra no tabuleiro do jogo com base na sua direção atual.
 *
 * A nova posição da cobra é calculada somando os valores de dx e dy da direção atual
 * às coordenadas x e y da posição atual. As novas coordenadas são ajustadas para garantir
 * que a cobra reapareça no lado oposto do tabuleiro se ultrapassar os limites.
 *
 * @param apple A posição da maçã no tabuleiro.
 * @param wall A lista de posições ocupadas por tijolos no tabuleiro.
 * @return Uma nova instância de `Snake` com a posição atualizada.
 */
fun Snake.atualizaSnake(apple: Position, wall: List<Position>): Snake {
    // Calcula a nova posição da cabeça
    val newHead = Position(
        (body.first().x + headDirection.dx + canvasWidth) % canvasWidth,
        (body.first().y + headDirection.dy + canvasHeight) % canvasHeight
    )

    // Verifica colisão com o próprio corpo ou com a parede
    if (body.contains(newHead) || wall.contains(newHead)) {
        return this
    }

    // Atualiza o contador toGrow ao comer a maçã
    val updatedToGrow = if (ateApple(apple)) toGrow + 5 else toGrow

    // Verifica se há elementos para crescer e se o caminho está livre
    return if (updatedToGrow > 0) {
        // Cresce adicionando a nova cabeça ao corpo
        this.copy(
            body = listOf(newHead) + body,
            toGrow = updatedToGrow - 1
        )
    } else {
        // Movimento normal (sem crescer)
        this.copy(body = listOf(newHead) + body.dropLast(1))
    }
}

/**
 * Atualiza a direção da cobra no jogo.
 *
 * A nova direção será aplicada apenas se não for a direção oposta à atual
 * e se a nova posição da cobra não estiver ocupada por uma barreira (tijolo).
 *
 * @param newHeadDirection A nova direção desejada para a cobra.
 * @param positions A lista de posições ocupadas por barreiras no jogo.
 * @return A instância atualizada de `Snake` com a nova direção, se válida; caso contrário, a instância atual.
 */
fun Snake.updateDir(newHeadDirection: HeadDirection, positions: List<Position>): Snake {
    // Calcula a nova posição da cabeça com base na direção
    val newX = (body.first().x + newHeadDirection.dx + canvasWidth) % canvasWidth
    val newY = (body.first().y + newHeadDirection.dy + canvasHeight) % canvasHeight
    val newPosition = Position(newX, newY)

    // Verifica se o novo movimento não é para a posição oposta à direção atual
    if (this.headDirection.isOpposite(newHeadDirection)) {
        return this // Impede o movimento para a direção oposta
    }

    // Verifica se o próximo espaço está livre, ignorando o corpo, mas respeitando os limites do canvas e a parede
    if (!positions.contains(newPosition) && !this.body.contains(newPosition)) {
        // Se o espaço à frente estiver livre, atualiza a direção
        return this.copy(headDirection = newHeadDirection)
    }

    // Caso contrário, continua com a direção atual
    return this
}

/**
 * Verifica se a cobra comeu a maçã.
 *
 * @param apple A posição da maçã no tabuleiro.
 * @return `true` se a cobra comeu a maçã, `false` caso contrário.
 */
fun Snake.ateApple(apple: Position?): Boolean {
    // Verifica se a cabeça da cobra está na mesma posição que a maçã
    return this.body.first() == apple
}

