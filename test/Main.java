package test;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int n;
        Scanner scn = new Scanner(System.in);
        //System.out.print("Enter N: ");
        //n = scn.nextInt();

        n = 5;
        Game game = new Game(n);
        game.printBoard();

        for (int i = 0; i < 5; i++) {
            game.advanceTime();
            System.out.println("Sense: ");
            int x = scn.nextInt();
            int y = scn.nextInt();
            game.sense(x,y);
        }
    }


}
