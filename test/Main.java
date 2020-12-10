package test;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static int probBound = 96;
    static int n;
    static char color;
    static double[][] grid;
    static Position ghostPosition;

    public static int getDistance(int i, int j) {
        return Math.abs(ghostPosition.x-i)+Math.abs(ghostPosition.y-j);
    }

    public static char getSenseColor(int distance) {
        //int distanceBound;
        if(distance <= 2) return 'r';
        else if (distance <= 5) return 'y';
        return 'g';
    }

    public static void makeGhostMove() {
        ArrayList<Position> sideMoves = new ArrayList<>();
        ArrayList<Position> diagMoves = new ArrayList<>();

        createMoveList(ghostPosition.x, ghostPosition.y, sideMoves, diagMoves);

        Random random = new Random();
        int k = random.nextInt(100);
        if(k < probBound) {
            //choose from sideMove
            ghostPosition = sideMoves.get(random.nextInt(sideMoves.size()));
            //System.out.println("side: " + ghostPosition.toString());
        }
        else {
            int t = random.nextInt(diagMoves.size()+1);
            if (t < diagMoves.size()) {
                //choosing diag move
                ghostPosition = diagMoves.get(t);
                //System.out.println("diag: " + ghostPosition.toString());
            }
            else {
                //stay
                //System.out.println("stay: " + ghostPosition.toString());
            }
        }

        System.out.println("Ghost is at " + ghostPosition.toString());
    }

    public static void advanceTime() {
        //calculate all possible transition
        //update the original grid
        //make the ghost move
        double[][] temp = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                ArrayList<Position> sideMoves = new ArrayList<>();
                ArrayList<Position> diagMoves = new ArrayList<>();
                createMoveList(i, j, sideMoves, diagMoves);

                double sideProb = probBound/(sideMoves.size()*100.0);
                double diagProb = (100-probBound)/((diagMoves.size()+1)*100.0);
                double stayProb = diagProb;
                double value = 0;
                
                for (Position sideMove : sideMoves) {
                    value += grid[sideMove.x][sideMove.y]*sideProb;
                }
                for (Position diagMove : diagMoves) {
                    value += grid[diagMove.x][diagMove.y]*diagProb;
                }
                value += grid[i][j]*stayProb;

                temp[i][j] = value;
                
                /*System.out.print("(" + i + "," + j + ") ");
                System.out.println("SideProb: " + sideProb + " DiagProb: " + diagProb + " StayProb: " + stayProb + " totalProb: " + (sideProb*sideMoves.size()+diagProb*diagMoves.size()+stayProb) + " sideMoveSize: " + sideMoves.size() + " diagMoveSize: " + diagMoves.size());
                System.out.println(value);*/
            }
        }

        for (int i = 0; i < n; i++) {
            System.arraycopy(temp[i], 0, grid[i], 0, n);
        }

        System.out.println("advanced Time");
        printGrid();
        makeGhostMove();
    }

    public static void createMoveList(int i, int j, ArrayList<Position> sideMoves, ArrayList<Position> diagMoves) {
        if(i-1 >= 0) {
            sideMoves.add(new Position(i-1, j));
            if(j-1 >= 0) diagMoves.add(new Position(i-1, j-1));
            if(j+1 < n) diagMoves.add(new Position(i-1, j+1));
        }
        if(i+1 < n) {
            sideMoves.add(new Position(i+1, j));
            if(j-1 >= 0) diagMoves.add(new Position(i+1, j-1));
            if(j+1 < n) diagMoves.add(new Position(i+1, j+1));
        }
        if(j-1 >= 0) sideMoves.add(new Position(i, j-1));
        if(j+1 < n) sideMoves.add(new Position(i, j+1));
    }

    public static void sense(int x, int y) {
        int distance = getDistance(x, y);
        color = getSenseColor(distance);
        System.out.println("manhattan distance: " + distance + " color: " + color);

        double[][] temp = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int d = Math.abs(x-i)+Math.abs(y-j);

                int p;
                /*if(d <= 2 && color == 'r') {
                    p = 1;
                }
                else if (d <= 5 && color == 'y') {
                    p = 1;
                }
                else if (d <= 2*n && color == 'g') {
                    p = 1;
                }
                else {
                    p = 0;
                }*/

                if (color == getSenseColor(d)) p = 1;
                else p = 0;

                temp[i][j] = grid[i][j]*p;
            }
        }

        for (int i = 0; i < n; i++) {
            System.arraycopy(temp[i], 0, grid[i], 0, n);
        }
        System.out.println("sensed:");
        printGrid();
    }
    
    public static void ghostMove() {
        int m;
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            m = random.nextInt(100);
            System.out.print(m + " ");
            if(m < probBound) System.out.println("up"); //makeMove();
            else if (m < probBound*2) System.out.println("down");
            else if (m < probBound*3) System.out.println("left");
            else if (m < probBound*4) System.out.println("up");
            else if (m < (probBound*4 + 100)/2) System.out.println("still");
            else {
                if(m < 25) System.out.println("upper-left");
                else if (m < 50) System.out.println("upper-right");
                else if (m < 75) System.out.println("lower-left");
                else System.out.println("lower-right");
            }
        }
        printGrid();
    }

    public static void printGrid() {
        System.out.println("==========Grid==========");
        double count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //System.out.print(String.format("%.3g%n", grid[i][j]) + " ");
                System.out.format("%.4f", grid[i][j]);
                System.out.print("  ");
                count += grid[i][j];
            }
            System.out.println();
        }
        System.out.println("totalProb: " + count + "\n");
    }

    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        //System.out.println("konnichiwa");
        //System.out.print("Enter N: ");
        //n = scn.nextInt();

        n = 5;
        grid = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = 1.0/(n*n);
            }
        }

        printGrid();

        Random random = new Random();
        ghostPosition = new Position(random.nextInt(n), random.nextInt(n));
        System.out.println("Ghost is at " + ghostPosition.toString());
        

        for (int i = 0; i < 5; i++) {
            advanceTime();
            System.out.println("Sense: ");
            int x = scn.nextInt();
            int y = scn.nextInt();
            sense(x,y);
        }

        /*ArrayList<Position> sideMoves = new ArrayList<>();
        ArrayList<Position> diagMoves = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            sideMoves.clear();
            diagMoves.clear();
            createMoveList(ghostPosition.x, ghostPosition.y, sideMoves, diagMoves);

            int k = random.nextInt(100);
            if(k < probBound) {
                //choose from sideMove
                ghostPosition = sideMoves.get(random.nextInt(sideMoves.size()));
                System.out.println("side: " + ghostPosition.toString());
            }
            else {
                int t = random.nextInt(diagMoves.size()+1);
                if (t < diagMoves.size()) {
                    //choosing diag move
                    ghostPosition = diagMoves.get(t);
                    System.out.println("diag: " + ghostPosition.toString());
                }
                else {
                    //stay
                    System.out.println("stay: " + ghostPosition.toString());
                }
            }

            System.out.println("sideProb: " + (double)probBound/sideMoves.size() + " sideSize: " + sideMoves.size());
            System.out.println("diagProb: " + (double)(100-probBound)/(diagMoves.size()+1) + " diagSize " + diagMoves.size());
            System.out.println("stayProb: " + (double)(100-probBound)/(diagMoves.size()+1));
            System.out.println("----------");
        }
        */

        //ghostMove();
    }
}
