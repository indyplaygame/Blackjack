package indy.blackjack.main;

import indy.blackjack.game.Game;
import indy.blackjack.utils.ASCII;
import indy.blackjack.utils.Colors;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static Game Game = new Game();

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);
        System.out.println(Colors.WHITE + ASCII.BLACKJACK);

        int n = 0;
        try {
            do {
                System.out.print(Colors.WHITE + "Enter number of players (max 6): ");
                n = scanner.nextInt();
            } while(n < 2 || n > 6);
        } catch (InputMismatchException e) {
            System.out.println("Error: expected a number.");
        }
        scanner.nextLine();

        Game.start(n);
    }
}
