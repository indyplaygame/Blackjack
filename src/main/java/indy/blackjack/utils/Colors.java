package indy.blackjack.utils;

public class Colors {
    public static String WHITE = "\u001B[37m";
    public static String RED = "\u001B[31m";
    public static String GREEN = "\u001B[32m";

    public static void set(String color) {
        System.out.print(color);
    }
}
