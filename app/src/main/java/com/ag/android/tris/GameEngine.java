package com.ag.android.tris;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {
    public enum Player {
        X,
        O;

        public Player otherPlayer() {
            if (this == Player.X)
                return Player.O;
            else
                return Player.X;
        }
    }

    public enum GameStatus {
        STILL_PLAYING,
        X_WON,
        O_WON,
        DRAW
    }

    private static final Random rand = new Random();

    private final Player[][] tiles;
    private GameStatus gameStatus;
    private Player toMove;


    public GameEngine() {
        tiles  = new Player[3][3];
        reset();
    }

    public Player[][] getTiles() {
        return tiles;
    }

    public GameStatus getStatus() {
        return gameStatus;
    }

    public Player getToMove() {
        return toMove;
    }


    public void reset() {
        for (int x=0; x < 3; x ++) {
            for (int y=0; y < 3; y ++) {
                tiles[x][y] = null;
            }
        }

        gameStatus = GameStatus.STILL_PLAYING;
        toMove = ( (rand.nextInt(2) == 1) ? Player.O : Player.X );
    }


    public GameStatus move(Player player, int[] xy) {
        return move(player, xy[0], xy[1]);
    }

    public GameStatus move(Player player, int x, int y) {
        if (gameStatus != GameStatus.STILL_PLAYING) {
            //game already finished
            return null;
        }

        if (toMove != player) {
            //not your turn
            return null;
        }

        if (tiles[x][y] != null) {
            //tile already occupied
            return null;
        }

        tiles[x][y] = player;
        if (player == Player.O)
            toMove = Player.X;
        else
            toMove = Player.O;


        updateStatus();
        return gameStatus;
    }


    public int[] proposeMove(Player player) {
        if (gameStatus != GameStatus.STILL_PLAYING)
            return null;

        if (toMove != player) {
            //not my turn
            return null;
        }

        List<int[]> candidates = listEmpties();
        if (candidates.isEmpty())
            return null;

        return candidates.get(rand.nextInt(candidates.size()));
    }


    private void updateStatus() {
        //check cols
        for (int x=0; x < 3; x ++) {
            if (tiles[x][0] != null && tiles[x][0] == tiles[x][1] && tiles[x][1] == tiles[x][2]) {
                gameStatus = playerWon(tiles[x][0]);
                return;
            }
        }

        //check rows
        for (int y=0; y < 3; y ++) {
            if (tiles[0][y] != null && tiles[0][y] == tiles[1][y] && tiles[1][y] == tiles[2][y]) {
                gameStatus = playerWon(tiles[0][y]);
                return;
            }
        }

        //check diags
        if (tiles[0][0] != null && tiles[0][0] == tiles[1][1] && tiles[1][1] == tiles[2][2]) {
            gameStatus = playerWon(tiles[0][0]);
            return;
        }

        if (tiles[0][2] != null && tiles[0][2] == tiles[1][1] && tiles[1][1] == tiles[2][0]) {
            gameStatus = playerWon(tiles[0][2]);
            return;
        }


        //check draw
        if (!hasEmpties())
            gameStatus = GameStatus.DRAW;
    }


    private List<int[]> listEmpties() {
        List<int[]> result = new ArrayList<>();

        for (int x=0; x < 3; x ++) {
            for (int y=0; y < 3; y ++) {
                if (tiles[x][y] == null)
                    result.add(new int[]{x, y});
            }
        }

        return result;
    }

    private boolean hasEmpties() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (tiles[x][y] == null) {
                    return true;
                }
            }
        }

        return false;
    }

    public static GameStatus playerWon(Player p) {
        if (p == Player.O)
            return GameStatus.O_WON;

        if (p == Player.X)
            return GameStatus.X_WON;

        return null;
    }

}
