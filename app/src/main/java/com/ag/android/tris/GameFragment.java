package com.ag.android.tris;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment {
    private static final long COMPUTER_MOVE_DELAY = 1500;

    private GameEngine mGame;
    private GameEngine.Player mHumanPlayer = GameEngine.Player.O;

    private TextView mMessageView;
    private GridView mGridView;
    private Button mRestartButton;


    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);

        mMessageView = v.findViewById(R.id.message);
        mRestartButton = v.findViewById(R.id.button_restart);
        mRestartButton.setOnClickListener(view -> startGame());

        mGridView = v.findViewById(R.id.gridview);
        mGridView.setAdapter(new GridAdapter(getActivity()));

        mGridView.setOnItemClickListener((parent, view, position, itemId) -> {
            int y = position / 3;
            int x = position - (y*3);

            GameEngine.GameStatus newStatus = mGame.move(mHumanPlayer, x, y);
            if (newStatus != null) {
                updateGridView();

                if (newStatus == GameEngine.GameStatus.STILL_PLAYING) {
                    makeComputerMove();
                }

            }
        });

        startGame();

        return v;
    }

    private void startGame() {
        mGame = new GameEngine();
        updateGridView();

        if (mGame.getToMove() != mHumanPlayer)
            makeComputerMove();
    }

    private void makeComputerMove() {
        mMessageView.setText(R.string.thinking);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            GameEngine.Player computerPlayer = mHumanPlayer.otherPlayer();
            int[] computerMove = mGame.proposeMove(computerPlayer);
            if (computerMove != null) {
                GameEngine.GameStatus newStatus1 = mGame.move(computerPlayer, computerMove);
                if (newStatus1 != null) {
                    updateGridView();
                }
            }
        }, COMPUTER_MOVE_DELAY);
    }

    private void updateGridView() {
        GridAdapter gridAdapter = (GridAdapter)mGridView.getAdapter();
        gridAdapter.setTiles(mGame.getTiles());
        gridAdapter.notifyDataSetChanged();

        if (mGame.getStatus() == GameEngine.playerWon(mHumanPlayer)) {
            mMessageView.setText(R.string.you_won);
        } else if (mGame.getStatus() == GameEngine.playerWon(mHumanPlayer.otherPlayer())) {
            mMessageView.setText(R.string.i_won);
        } else if (mGame.getStatus() == GameEngine.GameStatus.DRAW) {
            mMessageView.setText(R.string.draw);
        } else {
            if (mGame.getToMove() == mHumanPlayer)
                mMessageView.setText(R.string.your_turn);
            else
                mMessageView.setText(R.string.my_turn); //should never happen, "I'm thinking" should be triggered instead
        }
    }


    private final static class GridAdapter extends BaseAdapter {
        private List<GameTileData> mTiles;
        private final LayoutInflater mInflater;

        public GridAdapter(Context context) {
            mInflater = LayoutInflater.from(context);

            mTiles = new ArrayList<>();
            for (int x=0; x < 3; x ++) {
                for (int y=0; y < 3; y ++) {
                    mTiles.add(new GameTileData(0));
                }
            }
        }

        public void setTiles(GameEngine.Player[][] tiles) {
            List<GameTileData> newTiles = new ArrayList<>();

            //notice x and y are inverted: list construction order is opposite to screen coords
            for (int y=0; y < 3; y ++) {
                for (int x=0; x < 3; x ++) {
                    int drawableId;
                    if (tiles[x][y] == GameEngine.Player.O)
                        drawableId = R.drawable.tile_o;
                    else if (tiles[x][y] == GameEngine.Player.X)
                        drawableId = R.drawable.tile_x;
                    else
                        drawableId = 0;

                    newTiles.add(new GameTileData(drawableId));
                }
            }

            this.mTiles = newTiles;
        }


        @Override
        public int getCount() {
            return mTiles.size();
        }

        @Override
        public GameTileData getItem(int i) {
            return mTiles.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mTiles.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;

            if (v == null) {
                v = mInflater.inflate(R.layout.item_game_tile, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView) v.getTag(R.id.picture);

            GameTileData item = getItem(i);

            picture.setImageResource(item.drawableId);

            return v;
        }
    }

    private static class GameTileData {
        public int drawableId;

        GameTileData(int drawableId) {
            this.drawableId = drawableId;
        }
    }
}
