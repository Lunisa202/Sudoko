package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        final var positions = Stream.of(args)
                .collect(toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ));
        var option = -1;
        while (true){
            System.out.println("Selecione uma de las opciones:");
            System.out.println("1 - Iniciar un nuevo juego");
            System.out.println("2 - Colocar un nuevo nuemero");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar el juego actual");
            System.out.println("5 - Verificar estado del juego");
            System.out.println("6 - Limpiar el juego");
            System.out.println("7 - Finalizar el juego");
            System.out.println("8 - Salir");

            option = scanner.nextInt();

            switch (option){
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione una de las opciones del menú");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)){
            System.out.println("El juego ya fue iniciado");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("El juego está por comenzar");
    }


    private static void inputNumber() {
        if (isNull(board)){
            System.out.println("El juego aun no ha iniciado");
            return;
        }

        System.out.println("Ingrese la columna en donde desea insertar el numero: ");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Ingrese la fila en donde desea insertar el numero");
        var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Indique el numeor en el que va a ingresar [%s,%s]\n", col, row);
        var value = runUntilGetValidNumber(1, 9);
        if (!board.changeValue(col, row, value)){
            System.out.printf("A posición [%s,%s] tiene un valor fijo \n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)){
            System.out.println("El juego aun no ha sido iniciado");
            return;
        }

        System.out.println("Ingrese el numero de la columna");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Ingrese el numero de la fila");
        var row = runUntilGetValidNumber(0, 8);
        if (!board.clearValue(col, row)){
            System.out.printf("La posición [%s,%s] tiene un valor fijo \n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)){
            System.out.println("El juego aun no fue iniciado");
            return;
        }

        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col: board.getSpaces()){
                args[argPos ++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }
        System.out.println("Su juego se ve así hasta el momento");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)){
            System.out.println("Su juego aun no ha sido iniciado");
            return;
        }

        System.out.printf("El juego está en el estado: %s\n", board.getStatus().getLabel());
        if(board.hasErrors()){
            System.out.println("El juego tiene errores");
        } else {
            System.out.println("El juego no tiene errores");
        }
    }

    private static void clearGame() {
        if (isNull(board)){
            System.out.println("Su juego aun no ha sido iniciado");
            return;
        }

        System.out.println("Esta seguro de limpiar todo su tablero con el juego avanzado?");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("si") && !confirm.equalsIgnoreCase("no")){
            System.out.println("Responda 'si' o 'no'");
            confirm = scanner.next();
        }

        if(confirm.equalsIgnoreCase("si")){
            board.reset();
        }
    }

    private static void finishGame() {
        if (isNull(board)){
            System.out.println("Su juego aun no ha sido iniciado");
            return;
        }

        if (board.gameIsFinished()){
            System.out.println("Felicitaciones, El juego ha finalizado");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Su juego tiene errores, verifique su tablero");
        } else {
            System.out.println("Usted aun necesita llenar algunos espacios");
        }
    }


    private static int runUntilGetValidNumber(final int min, final int max){
        var current = scanner.nextInt();
        while (current < min || current > max){
            System.out.printf("Informe um número entre %s e %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }

}