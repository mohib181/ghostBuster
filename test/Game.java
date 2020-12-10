package test;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    int n;
    double probBound;
    double[][] board;
    Position ghostPosition;

    public Game(int boardSize) {
        this.n = boardSize;
        this.probBound = 0.8;
        this.board = new double[boardSize][boardSize];
        this.initiateBoard();
    }

    public void initiateBoard() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = 1.0/(n*n);
            }
        }

        Random random = new Random(System.currentTimeMillis());
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

        double sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.format("%.4f", board[i][j]);
                System.out.print("  ");
                sum += board[i][j];
            }
            System.out.println();
        }

        System.out.println("totalProb: " + sum);
        System.out.println("Ghost is at " + ghostPosition.toString());
        System.out.println("============================");
    }

    public double[][] getBoard() {
        return board;
    }

    public double getProbBound() {
        return probBound;
    }

    public void setProbBound(double probBound) {
        this.probBound = probBound;
    }

    public ArrayList<Position> getSideNeighbors(int i, int j) {
        ArrayList<Position> sideNeighbors = new ArrayList<>();

        if(i-1 >= 0) sideNeighbors.add(new Position(i - 1, j));
        if(i+1 < n) sideNeighbors.add(new Position(i + 1, j));
        if(j-1 >= 0) sideNeighbors.add(new Position(i, j-1));
        if(j+1 < n) sideNeighbors.add(new Position(i, j+1));

        return sideNeighbors;
    }

    public ArrayList<Position> getDiagNeighbors(int i, int j) {
        ArrayList<Position> diagNeighbors = new ArrayList<>();

        if(i-1 >= 0) {
            if(j-1 >= 0) diagNeighbors.add(new Position(i-1, j-1));
            if(j+1 < n) diagNeighbors.add(new Position(i-1, j+1));
        }
        if(i+1 < n) {
            if(j-1 >= 0) diagNeighbors.add(new Position(i+1, j-1));
            if(j+1 < n) diagNeighbors.add(new Position(i+1, j+1));
        }

        return diagNeighbors;
    }

    public void makeGhostMove() {
        ArrayList<Position> sideNeighbors = getSideNeighbors(ghostPosition.x, ghostPosition.y);
        ArrayList<Position> diagNeighbors = getDiagNeighbors(ghostPosition.x, ghostPosition.y);

        Random random = new Random(System.currentTimeMillis());
        int k = random.nextInt(100);
        if(k < probBound*100) {
            //choose from sideMove
            ghostPosition = sideNeighbors.get(random.nextInt(sideNeighbors.size()));
            //System.out.println("k: "+ k + " side: " + ghostPosition.toString());
        }
        else {
            int t = random.nextInt(diagNeighbors.size()+1);
            if (t < diagNeighbors.size()) {
                //choosing diag move
                ghostPosition = diagNeighbors.get(t);
                //System.out.println("k: "+ k + " t: "+ t + " diag: " + ghostPosition.toString());
            }
            else {
                //stay
                //System.out.println("k: "+ k + " t: "+ t + " stay: " + ghostPosition.toString());
            }
        }

        //System.out.println("Ghost moved to " + ghostPosition.toString());
    }

    public void advanceTime() {
        //calculate all possible transition
        //update the original grid
        //make the ghost move
        double sum = 0;
        double[][] temp = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                ArrayList<Position> sideNeighbors = getSideNeighbors(i, j);
                ArrayList<Position> diagNeighbors = getDiagNeighbors(i, j);

                //System.out.println("(" + i + "," + j + ")");
                double value = 0;
                for (Position sideNeighbor : sideNeighbors) {
                    double sideProb = probBound/getSideNeighbors(sideNeighbor.x, sideNeighbor.y).size();
                    value += board[sideNeighbor.x][sideNeighbor.y]*sideProb;
                    //System.out.println("side: " + sideNeighbor.toString() + " sideProb: " + sideProb + "neighbor: " + board[sideNeighbor.x][sideNeighbor.y]);
                }
                for (Position diagNeighbor : diagNeighbors) {
                    double diagProb = (1-probBound)/getDiagNeighbors(diagNeighbor.x, diagNeighbor.y).size();
                    value += board[diagNeighbor.x][diagNeighbor.y]*diagProb;
                    //System.out.println("diag: " + diagNeighbor.toString() + " diagProb: " + diagProb + "neighbor: " + board[diagNeighbor.x][diagNeighbor.y]);
                }
                value += board[i][j]*((1-probBound)/(diagNeighbors.size()+1));

                temp[i][j] = value;
                sum += value;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = (1.0/sum)*temp[i][j];
            }
        }

        System.out.println("advanced Time");
        makeGhostMove();
        printBoard();
    }

    public int getDistance(int i, int j) {
        return Math.abs(ghostPosition.x-i)+Math.abs(ghostPosition.y-j);
    }

    public char getSenseColor(int distance) {
        if(distance <= 2) return 'r';
        else if (distance <= 5) return 'y';
        return 'g';
    }

    public void sense(int x, int y) {
        int distance = getDistance(x, y);
        char color = getSenseColor(distance);

        double sum = 0;
        double[][] temp = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int d = Math.abs(x-i)+Math.abs(y-j);

                int p;
                if (color == getSenseColor(d)) p = 1;
                else p = 0;

                temp[i][j] = board[i][j]*p;
                sum += temp[i][j];
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = (1.0/sum)*temp[i][j];
            }
        }
        System.out.println("sensed: (" + x + "," + y + ") distance: " + distance + " color: " + color);
        printBoard();
    }
}
