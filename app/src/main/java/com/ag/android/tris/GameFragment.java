package com.ag.android.tris;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment {
    private static final long COMPUTER_MOVE_DELAY = 3000;

    private GameEngine mGame;
    private GameEngine.Player humanPlayer;

    private GridView mGridView;

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGame = new GameEngine();
        humanPlayer = mGame.getToMove();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);

        mGridView = v.findViewById(R.id.gridview);
        mGridView.setAdapter(new GridAdapter(getActivity()));

        mGridView.setOnItemClickListener((parent, view, position, itemId) -> {
            int y = position / 3;
            int x = position - (y*3);

            GameEngine.GameStatus newStatus = mGame.move(humanPlayer, x, y);
            if (newStatus != null) {
                updateGridView();

                if (newStatus == GameEngine.GameStatus.STILL_PLAYING) {
                    Toast.makeText(getActivity(), "I'm thinking...", Toast.LENGTH_SHORT).show();

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        GameEngine.Player computerPlayer = humanPlayer.otherPlayer();
                        int[] computerMove = mGame.proposeMove(computerPlayer);
                        if (computerMove != null) {
                            GameEngine.GameStatus newStatus1 = mGame.move(computerPlayer, computerMove);
                            if (newStatus1 != null)
                                updateGridView();
                        }
                    }, COMPUTER_MOVE_DELAY);
                }

            }
        });
        return v;
    }

    private void updateGridView() {
        GridAdapter gridAdapter = (GridAdapter)mGridView.getAdapter();
        gridAdapter.setTiles(mGame.getTiles());
        gridAdapter.notifyDataSetChanged();

        //TODO: check other statuses and give visual feedback
        if (mGame.getStatus() == GameEngine.playerWon(humanPlayer))
            Toast.makeText(getActivity(), "You won!", Toast.LENGTH_LONG).show();

        if (mGame.getStatus() == GameEngine.playerWon(humanPlayer.otherPlayer()))
            Toast.makeText(getActivity(), "I won!!!", Toast.LENGTH_LONG).show();
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
