package test;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    int n;
    int probBound;
    double[][] board;
    Position ghostPosition;

    public Game(int boardSize) {
        this.n = boardSize;
        this.probBound = 80;
        this.board = new double[boardSize][boardSize];
        this.initiateBoard();
    }

    public void initiateBoard() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = 1.0/(n*n);
            }
        }

        Random random = new Random();
        ghostPosition = new Position(random.nextInt(n), random.nextInt(n));
    }

    public void resetBoard() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = 0;
            }
        }
    }

    public void printBoard() {
        System.out.println("============================");

        double count = 0;
        for (double[] doubles : board) {
            for (int j = 0; j < board.length; j++) {
                System.out.format("%.4f", doubles[j]);
                System.out.print("  ");
                count += doubles[j];
            }
            System.out.println();
        }
        System.out.println("totalProb: " + count);
        System.out.println("Ghost is at " + ghostPosition.toString());
        System.out.println("============================");
    }

    public double[][] getBoard() {
        return board;
    }

    public int getProbBound() {
        return probBound;
    }

    public void setProbBound(int probBound) {
        this.probBound = probBound;
    }

    public void advanceTime() {
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
                    value += board[sideMove.x][sideMove.y]*sideProb;
                }
                for (Position diagMove : diagMoves) {
                    value += board[diagMove.x][diagMove.y]*diagProb;
                }
                value += board[i][j]*stayProb;

                temp[i][j] = value;

                /*System.out.print("(" + i + "," + j + ") ");
                System.out.println("SideProb: " + sideProb + " DiagProb: " + diagProb + " StayProb: " + stayProb + " totalProb: " + (sideProb*sideMoves.size()+diagProb*diagMoves.size()+stayProb) + " sideMoveSize: " + sideMoves.size() + " diagMoveSize: " + diagMoves.size());
                System.out.println(value);*/
            }
        }

        for (int i = 0; i < n; i++) {
            System.arraycopy(temp[i], 0, board[i], 0, n);
        }

        System.out.println("advanced Time");
        printBoard();
        makeGhostMove();
    }

    public void createMoveList(int i, int j, ArrayList<Position> sideMoves, ArrayList<Position> diagMoves) {
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

    public void makeGhostMove() {
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


    public int getDistance(int i, int j) {
        return Math.abs(ghostPosition.x-i)+Math.abs(ghostPosition.y-j);
    }

    public char getSenseColor(int distance) {
        //int distanceBound;
        if(distance <= 2) return 'r';
        else if (distance <= 5) return 'y';
        return 'g';
    }

    public void sense(int x, int y) {
        int distance = getDistance(x, y);
        char color = getSenseColor(distance);
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

                temp[i][j] = board[i][j]*p;
            }
        }

        for (int i = 0; i < n; i++) {
            System.arraycopy(temp[i], 0, board[i], 0, n);
        }
        System.out.println("sensed:");
        printBoard();
    }
}
