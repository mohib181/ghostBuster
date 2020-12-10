package game;

import java.util.ArrayList;

public class PlayerComputer extends Player {
    private final static int MAXVALUE = 100000;
    private final static int MINVALUE = -100000;

    private int scheme;
    private final char opponentColor;
    private final ArrayList<Position> pieces;
    private final ArrayList<Position> opponentPieces;

    public PlayerComputer(char playerColor, String playerName) {
        super(playerColor, playerName);
        scheme = 1;
        opponentColor = (playerColor == 'w') ? 'b' : 'w';
        pieces = new ArrayList<>();
        opponentPieces = new ArrayList<>();
    }

    public void setScheme(int scheme) {
        this.scheme = scheme;
    }

    private int evaluateBoardPieceTable(char[][] board) {
        int[][] pieceSquareTable = {
                {-80, -25, -20, -20, -20, -20, -25, -80},
                {-25, 10, 10, 10, 10, 10, 10, -25},
                {-20, 10, 25, 25, 25, 25, 10, -20},
                {-20, 10, 25, 50, 50, 25, 10, -20},
                {-20, 10, 25, 50, 50, 25, 10, -20},
                {-20, 10, 25, 25, 25, 25, 10, -20},
                {-25, 10, 10, 10, 10, 10, 10, -25},
                {-80, -25, -20, -20, -20, -20, -25, -80}
        };

        int[][] pieceSquareTable6 = {
                {-80, -20, -20, -20, -20, -80},
                {-20, 25, 25, 25, 25, -20},
                {-20, 25, 50, 50, 25, -20},
                {-20, 25, 50, 50, 25, -20},
                {-20, 25, 25, 25, 25, -20},
                {-80, -20, -20, -20, -20, -80}
        };

        int[][] scoreTable = new int[0][];

        if (board.length == 8) scoreTable = pieceSquareTable;
        else if (board.length == 6) scoreTable = pieceSquareTable6;

        int myScore = 0;
        int opponentScore = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == playerColor) myScore += scoreTable[i][j];
                else if (board[i][j] == opponentColor) opponentScore += scoreTable[i][j];
            }
        }

        return myScore-opponentScore;
    }

    private double getDensity(char[][] board, char color) {
        int xMass = 0, yMass = 0, xCount = 0, yCount = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != '-') {
                    xCount++;
                    yCount++;
                    xMass += i;
                    yMass += j;
                }
            }
        }
        double centreX = (double) xMass/xCount;
        double centreY = (double) yMass/yCount;

        double distanceX, distanceY, distance = 0;

        ArrayList<Position> positionArrayList;
        if (color == playerColor)  positionArrayList = pieces;
        else positionArrayList = opponentPieces;

        for (Position piece: positionArrayList) {
            distanceX = Math.abs(centreX - piece.x);
            distanceY = Math.abs(centreY - piece.y);
            distance += distanceX*distanceX + distanceY*distanceY;
        }
        distance = distance/positionArrayList.size();
        distance = Math.sqrt(distance);
        return distance;
    }
    private double evaluateBoardDensity(char[][] board) {
        double myDensity = getDensity(board, playerColor);
        double opponentDensity = getDensity(board, opponentColor);

        return opponentDensity-myDensity;
    }

    private int getArea(char[][] board, char color) {
        int i, j = 0;
        int row = 0, col = 0;
        boolean found = false;
        for (i = 0; i < board.length && !found; i++) {
            for (j = 0; j < board.length; j++) {
                if (board[i][j] == color) {
                    row = i;
                    col = j;
                    found = true;
                    break;
                }
            }
        }
        found = false;
        for (i = board.length-1; i >= 0 && !found; i--) {
            for (j = board.length-1; j >= 0; j--) {
                if (board[i][j] == color) {
                    found = true;
                    break;
                }
            }
        }

        return Math.abs(i-row)*Math.abs(j-col);
    }
    private int evaluateBoardArea(char[][] board) {
        int myArea = getArea(board, playerColor);
        int opponentArea = getArea(board, opponentColor);

        return opponentArea-myArea;
    }

    private int getQuadCount(char[][] board, char color) {
        int count, quadCount = 0;
        for (int i = 0; i < board.length-1; i++) {
            count = 0;
            for (int j = 0; j < board.length-1; j++) {
                if (board[i][j] == color) count++;
                if (board[i][j+1] == color) count++;
                if (board[i+1][j] == color) count++;
                if (board[i+1][j+1] == color) count++;
                if (count >= 3) quadCount++;
            }
        }
        return quadCount;
    }
    private int evaluateBoardQuad(char[][] board) {
        int quadCount = getQuadCount(board, playerColor);
        int opponentQuadCount = getQuadCount(board, opponentColor);

        return quadCount-opponentQuadCount;
    }

    private double evaluateBoard(char[][] board) {
        if (scheme == 1)
            return ( evaluateBoardPieceTable(board) * 0.4 +
                     evaluateBoardDensity(board) * 0.4 +
                     evaluateBoardArea(board) * 0.2

            );

        return ( evaluateBoardPieceTable(board) * 0.4 +
                 evaluateBoardDensity(board) * 0.4 +
                 evaluateBoardQuad(board) * 0.2

        );
    }

    private double minimax(char[][] board, int depth, double alpha, double beta, boolean isMax) {
        double value;
        char prevValue;
        Position prev;
        ArrayList<Position> moves;

        if (depth == 0) {
            return evaluateBoard(board);
        }

        if (isMax) {
            value = MINVALUE;

            for (Position piece: pieces) {
                moves = generateMoves(board, piece.x, piece.y);

                for (Position pos: moves) {
                    prev = new Position(piece.x, piece.y);
                    prevValue = makeMove(board, piece.x, piece.y, pos.x, pos.y);
                    piece.x = pos.x;
                    piece.y = pos.y;

                    value = Math.max(value, minimax(board, depth-1, alpha, beta, false));

                    piece.x = prev.x;
                    piece.y = prev.y;
                    undoMove(board, piece.x, piece.y, pos.x, pos.y, prevValue);

                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) break;
                }
            }
        }
        else {
            value = MAXVALUE;

            for (Position piece: opponentPieces) {
                moves = generateMoves(board, piece.x, piece.y);

                for (Position pos: moves) {
                    prev = new Position(piece.x, piece.y);
                    prevValue = makeMove(board, piece.x, piece.y, pos.x, pos.y);
                    piece.x = pos.x;
                    piece.y = pos.y;

                    value = Math.min(value, minimax(board, depth-1, alpha, beta, true));

                    piece.x = prev.x;
                    piece.y = prev.y;
                    undoMove(board, piece.x, piece.y, pos.x, pos.y, prevValue);

                    beta = Math.min(beta, value);
                    if (beta <= alpha) break;
                }
            }
        }

        return value;
    }

    public ArrayList<Position> findBestMove(char[][] board, int depth) {
        char prevValue;
        double currMoveVal;
        double bestMoveVal = MINVALUE;
        int row = -1, col = -1;
        int bestRow = -1, bestCol = -1;

        pieces.clear();
        opponentPieces.clear();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == playerColor) {
                    pieces.add(new Position(i, j));
                }
                else if (board[i][j] == opponentColor) {
                    opponentPieces.add(new Position(i, j));
                }
            }
        }

        Position prev;
        boolean hasMove = false;
        ArrayList<Position> moves = new ArrayList<>();
        for (Position piece: pieces) {
            moves = generateMoves(board, piece.x, piece.y);
            //System.out.println(moves);

            for (Position pos : moves) {
                prev = new Position(piece.x, piece.y);
                prevValue = makeMove(board, piece.x, piece.y, pos.x, pos.y);
                piece.x = pos.x;
                piece.y = pos.y;

                currMoveVal = minimax(board, depth, MINVALUE, MAXVALUE, true);
                //System.out.println("Move: " + prev + "-->" + pos + " Score: " + currMoveVal);

                piece.x = prev.x;
                piece.y = prev.y;
                undoMove(board, piece.x, piece.y, pos.x, pos.y, prevValue);

                if (currMoveVal > bestMoveVal) {
                    hasMove = true;
                    bestMoveVal = currMoveVal;

                    row = piece.x;
                    col = piece.y;
                    bestRow = pos.x;
                    bestCol = pos.y;
                }
            }
        }

        moves.clear();
        if (hasMove) {
            moves.add(new Position(row, col));
            moves.add(new Position(bestRow, bestCol));
        }

        return moves;
    }
}
