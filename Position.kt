/**
 * Define a classe `Position`, que representa a posição
 * no tabuleiro do jogo, juntamente com
 * funções auxiliares para criação de novas
 * posições aleatórias no tabuleiro.
 */
data class Position(val x: Int, val y: Int)

fun newpos( ): Position {

    val x = (0..canvasWidth-1).random()
    val y = (0..canvasHeight-1).random()
    return Position(x, y)
}



val inicialBrick=listOf(Position(0,0),Position(1,0),Position(2,0)  //canto superior izquerdo
                   ,Position(0,1),Position(0,2),Position(0,3),

                    Position(19,0),Position(18,0),Position(17,0)  //canto superior dereito
                    ,Position(19,1),Position(19,2),Position(19,3),

                    Position(0,12),Position(0,13),Position(0,14)  //canto inferior izquerdo
                    ,Position(0,15),Position(1,15),Position(2,15)
                    ,Position(0,16),Position(1,16),Position(2,16),

                    Position(19,12),Position(19,13),Position(19,14)  //canto inferior dereito
                    ,Position(19,15),Position(18,15),Position(17,15)
                    ,Position(19,16),Position(18,16),Position(17,16))

