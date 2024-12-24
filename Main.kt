/**
 * Ponto de entrada principal do aplicativo. Este ficheiro
 * é responsável por inicializar o jogo,
 * definir a lógica de tempo e de eventos do teclado,
 * e gerir as interações do usuário com o jogo.
 */
import pt.isel.canvas.*

/**
 * A função principal `main` configura e inicia o jogo da cobra.
 *
 * - Inicializa o canvas e o jogo.
 * - Adiciona tijolos em posições aleatórias no tabuleiro em intervalos de tempo.
 * - Atualiza a posição da cobra em intervalos de tempo.
 * - Responde às teclas pressionadas para mudar a direção da cobra.
 * - Verifica se o jogo terminou com base nas posições ocupadas.
 * - Desenha a cobra e os tijolos no canvas.
 */
fun main() {
    onStart {
        val arena = Canvas(WIDTH,HEIGHT,BLACK)
//        val list= inicialBrick
        var game = initializeGame()


        arena.onTimeProgress(5000) {

       game=game.gameWithNewPosition()
            //para os tijolos
            game.drawGame(arena)


        }
        arena.onTimeProgress(250) {
            game = game.updateGame()
            game.drawGame(arena)

            }

        arena.onKeyPressed { k->
            val headDirection=when(k.code){
                pt.isel.canvas.LEFT_CODE -> HeadDirection.MOVE_LEFT
                pt.isel.canvas.RIGHT_CODE -> HeadDirection.MOVE_RIGHT
                pt.isel.canvas.UP_CODE -> HeadDirection.MOVE_DOWN
                else -> HeadDirection.MOVE_UP
            }
            game = game.copy(snake = game.snake.updateDir(headDirection,game.wall))

        }

    }
    onFinish {}
}


