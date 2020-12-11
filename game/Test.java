package game;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        int n = 9;
        Scanner scn = new Scanner(System.in);
        System.out.print("Enter N: ");
        n = scn.nextInt();

        Game game = new Game(n);
        game.printBoard();

        while(true) {
            System.out.println("1.AdvanceTime\t2.Sense\t3.Bust");
            System.out.print("Enter: ");
            int op = scn.nextInt();
            if(op == 1) {
                game.advanceTime();
            }
            else if(op == 2) {
                System.out.println("Enter a co ordinate to sense: ");
                int x = scn.nextInt();
                int y = scn.nextInt();
                char c = game.sense(x,y);
                System.out.println("found color: " + c);
            }
            else {
                System.out.println("Enter a co ordinate to check ghost: ");
                int x = scn.nextInt();
                int y = scn.nextInt();
                boolean check = game.checkGhost(x, y);
                if(check) {
                    System.out.println("ghost busted");
                    break;
                }
                else {
                    System.out.println("missed the ghost");
                    char c = game.sense(x,y);
                    System.out.println("found color: " + c);
                }
            }
        }

        /*Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 35; i++) {
            game.advanceTime();
            // game.sense(random.nextInt(n), random.nextInt(n));
            // System.out.println("Sense: ");
            // int x = scn.nextInt();
            // int y = scn.nextInt();
            // game.sense(x,y);
        }*/
    }
}
