package game;

import java.util.ArrayList;

public class Player {
    char playerColor;
    String playerName;

    public Player(char playerColor, String playerName) {
        this.playerColor = playerColor;
        this.playerName = playerName;
    }

    public char getPlayerColor() {
        return playerColor;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int calculateMoveAhead(char[][] board, int row, int col, String dir) {
        int moveAhead = 0;
        switch (dir) {
            case "up":
            case "down":
                for (int i = 0; i < board.length; i++) {
                    if (board[i][col] != '-') moveAhead++;
                }
                break;
            case "left":
            case "right":
                for (int j = 0; j < board.length; j++) {
                    if (board[row][j] != '-') moveAhead++;
                }
                break;
            case "upper-left":
            case "lower-right":
                int min = Math.min(row, col);
                for (int i = row - min, j = col - min; i < board.length && j < board.length; i++, j++) {
                    if (board[i][j] != '-') moveAhead++;
                }
                break;
            case "lower-left":
            case "upper-right":
                for (int i = row, j = col; i < board.length && j >= 0; i++, j--) {
                    if (board[i][j] != '-') moveAhead++;
                }
                for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) {
                    if (board[i][j] != '-') moveAhead++;
                }
                break;
        }
        return moveAhead;
    }

    public Position getValidMove(char[][] board, int row, int col, int moveAhead, String dir) {
        char color = board[row][col];
        char opponentColor = (color == 'w') ? 'b' : 'w';

        if ("up".equals(dir)) {
            if (row - moveAhead < 0) return null;
            if (board[row - moveAhead][col] == color) return null;
            for (int i = 1; i < moveAhead; i++) {
                if (board[row - i][col] == opponentColor) return null;
            }
            return new Position(row - moveAhead, col);
        } else if ("down".equals(dir)) {
            if (row + moveAhead >= board.length) return null;
            if (board[row + moveAhead][col] == color) return null;
            for (int i = 1; i < moveAhead; i++) {
                if (board[row + i][col] == opponentColor) return null;
            }
            return new Position(row + moveAhead, col);
        } else if ("right".equals(dir)) {
            if (col + moveAhead >= board.length) return null;
            if (board[row][col + moveAhead] == color) return null;
            for (int i = 1; i < moveAhead; i++) {
                if (board[row][col + i] == opponentColor) return null;
            }
            return new Position(row, col + moveAhead);
        } else if ("left".equals(dir)) {
            if (col - moveAhead < 0) return null;
            if (board[row][col - moveAhead] == color) return null;
            for (int i = 1; i < moveAhead; i++) {
                if (board[row][col - i] == opponentColor) return null;
            }
            return new Position(row, col - moveAhead);
        } else if ("upper-right".equals(dir)) {
            if (row - moveAhead < 0 || col + moveAhead >= board.length) return null;
            if (board[row - moveAhead][col + moveAhead] == color) return null;
            for (int i = 1; i < moveAhead; i++) {
                if (board[row - i][col + i] == opponentColor) return null;
            }
            return new Position(row - moveAhead, col + moveAhead);
        } else if ("upper-left".equals(dir)) {
            if (row - moveAhead < 0 || col - moveAhead < 0) return null;
            if (board[row - moveAhead][col - moveAhead] == color) return null;
            for (int i = 1; i < moveAhead; i++) {
                if (board[row - i][col - i] == opponentColor) return null;
            }
            return new Position(row - moveAhead, col - moveAhead);
        } else if ("lower-right".equals(dir)) {
            if (row + moveAhead >= board.length || col + moveAhead >= board.length) return null;
            if (board[row + moveAhead][col + moveAhead] == color) return null;
            for (int i = 1; i < moveAhead; i++) {
                if (board[row + i][col + i] == opponentColor) return null;
            }
            return new Position(row + moveAhead, col + moveAhead);
        } else if ("lower-left".equals(dir)) {
            if (row + moveAhead >= board.length || col - moveAhead < 0) return null;
            if (board[row + moveAhead][col - moveAhead] == color) return null;
            for (int i = 1; i < moveAhead; i++) {
                if (board[row + i][col - i] == opponentColor) return null;
            }
            return new Position(row + moveAhead, col - moveAhead);
        }
        return null;
    }

    public ArrayList<Position> generateMoves(char[][] board, int row, int col) {
        ArrayList<Position> moves = new ArrayList<>();
        String[] dirs = {"up", "down", "left", "right", "upper-left", "lower-right", "lower-left", "upper-right"};
        for (String dir: dirs) {
            int moveAhead = calculateMoveAhead(board, row, col, dir);

            Position pos = getValidMove(board, row, col, moveAhead, dir);
            if (pos != null) moves.add(pos);
        }
        return moves;
    }

    public char makeMove(char[][] board, int row, int col, int moveAheadRow, int moveAheadCol) {
        char value = board[moveAheadRow][moveAheadCol];
        board[moveAheadRow][moveAheadCol] = board[row][col];
        board[row][col] = '-';

        return value;
    }

    public void undoMove(char[][] board, int row, int col, int moveAheadRow, int moveAheadCol, char prevValue) {
        board[row][col] = board[moveAheadRow][moveAheadCol];
        board[moveAheadRow][moveAheadCol] = prevValue;
    }
}
