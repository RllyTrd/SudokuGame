package com.example.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class PuzzleView extends View {
    private static final  String TAG = "CPTR320";
    private final Game game;
    private float width;
    private float height;
    private int selX;
    private int selY;
    private static final String SEL_X = "x_selection";
    private static final String SEL_Y = "y_selection";

    private static final String VIEW_STATE = "puzzle_view_state";

    private int ID = 342;

    private final Rect selRect = new Rect();



    public PuzzleView(Context context) {
        super(context);
        this.game = (Game) context;
        setFocusable(true);
        setFocusableInTouchMode(true);
        setId(ID);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w/9f;
        height = h/9f;
        getRect(selX,selY,selRect);
        Log.d(TAG, "onSizedChanged: width " + width + " height " + height);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void getRect(int x, int y, Rect rect) {
        rect.set((int) (x*width), (int) (y*height), (int) (x*width+width), (int) (y*height+height));
    }
    private Rect getAreaOnScreen(int x, int y) {
        Rect rect = new Rect();
        rect.set((int) (x*width), (int) (y*height), (int) (x*width+width), (int) (y*height+height));
        return rect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint background = new Paint();
        background.setColor(getResources().getColor(R.color.puzzle_background, game.getTheme()));
        canvas.drawRect(0,0,getWidth(),getHeight(),background);
        Paint dark = new Paint();
        dark.setColor(getResources().getColor(R.color.puzzle_dark, game.getTheme()));
        Paint hilite = new Paint();
        hilite.setColor(getResources().getColor(R.color.puzzle_hilite, game.getTheme()));
        Paint light = new Paint();
        light.setColor(getResources().getColor(R.color.puzzle_light, game.getTheme()));

        for (int i = 0; i < 9; i++) {
            canvas.drawLine(0,i*height, getWidth(),i*height + 2,dark);
            canvas.drawLine(0,i*height + 2, getWidth(),i*height,hilite);
            canvas.drawLine(i*width,0, i*width, getHeight(),dark);
            canvas.drawLine(i*width+ 2,0, i*width + 2, getHeight(),hilite);
        }
        for(int i = 0; i < 9; i++){
            if (i%3 != 0)
                continue;
            canvas.drawLine(0,i*height, getWidth(),i*height + 2,dark);
            canvas.drawLine(0, i * height + 2,getWidth(),i*height + 2, hilite);
            canvas.drawLine(i*width,0, i*width, getHeight(),dark);
            canvas.drawLine(i*width + 2, 0, i*width + 2, getHeight(), hilite);
        }
        Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
        foreground.setColor(getResources().getColor(R.color.greyish, game.getTheme()));
        foreground.setStyle(Paint.Style.FILL);
        foreground.setTextSize(height*0.75f);
        foreground.setTextScaleX(width/height);
        foreground.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fm = foreground.getFontMetrics();
        float x = width/2;
        float y = height/2 - (fm.ascent+fm.descent)/2;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);
            }
        }

        Paint selected = new Paint();
        selected.setColor(getResources().getColor(R.color.puzzle_selected, game.getTheme()));
        canvas.drawRect(selRect,selected);

        int[] puzzle = game.getCurrentPuzzle();
        for (int i = 0;i < puzzle.length;i++) {
            int yy = i / 9;
            int xx = i % 9;
            if (puzzle[i] != 0){
                Rect rect = getAreaOnScreen(xx, yy);
                Paint mutable = new Paint();
                mutable.setColor(getResources().getColor(R.color.clearseafoam, game.getTheme()));
                canvas.drawRect(rect,mutable);
            }

        }

        if(PrefsActivity.getHints(getContext())){
            Paint hint = new Paint();
            int c[] = { getResources().getColor(R.color.puzzle_hint_0, game.getTheme()),
            getResources().getColor(R.color.puzzle_hint_1, game.getTheme()),
            getResources().getColor(R.color.puzzle_hint_2, game.getTheme())
            };
            Rect r = new Rect();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int movesleft = 9 - game.getUsedTiles(i, j).length;
                    if (movesleft < c.length) {
                        getRect(i, j, r);
                        hint.setColor(c[movesleft]);
                        canvas.drawRect(r,hint);
                    }
                }
            }
        }
    }

    public void setSelectedTile(int tile) {
        if (game.setTileIfValid(selX,selY,tile)) {
            invalidate();
            Log.d(TAG, "setSelectedTile: invalid: " + tile);
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }
        select((int) (event.getX()/width), (int) (event.getY()/height));
        game.showKeypadOrError(selX,selY);
        Log.d(TAG, "onTouchEvent : x " + selX + " , y " + selY);
        return true;
    }
    private void select(int x, int y) {
        postInvalidate(selRect.left, selRect.top, selRect.right, selRect.bottom);
        selX = Math.min(Math.max(x, 0), 8);
        selY = Math.min(Math.max(y, 0), 8);
        getRect(selX,selY,selRect);
        postInvalidate(selRect.left, selRect.top, selRect.right, selRect.bottom);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
                select(selX,selY - 1);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                select(selX,selY + 1);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                select(selX- 1,selY );
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                select(selX + 1,selY);
                break;
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_SPACE:
                setSelectedTile(0);
                break;
            case KeyEvent.KEYCODE_1:
                setSelectedTile(1);
                break;
            case KeyEvent.KEYCODE_2:
                setSelectedTile(2);
                break;
            case KeyEvent.KEYCODE_3:
                setSelectedTile(3);
                break;
            case KeyEvent.KEYCODE_4:
                setSelectedTile(4);
                break;
            case KeyEvent.KEYCODE_5:
                setSelectedTile(5);
                break;
            case KeyEvent.KEYCODE_6:
                setSelectedTile(6);
                break;
            case KeyEvent.KEYCODE_7:
                setSelectedTile(7);
                break;
            case KeyEvent.KEYCODE_8:
                setSelectedTile(8);
                break;
            case KeyEvent.KEYCODE_9:
                setSelectedTile(9);
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                game.showKeypadOrError(selX,selY);
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcel = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putInt(SEL_X, selX);
        bundle.putInt(SEL_Y,selY);
        bundle.putParcelable(VIEW_STATE, parcel);
        Log.d(TAG, "onSave");
        return bundle;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        int x = bundle.getInt(SEL_X);
        int y = bundle.getInt(SEL_Y);
        select(x,y);
        Parcelable parcel = (Parcelable) bundle.getParcelable(VIEW_STATE,PuzzleView.class);
        Log.d(TAG, "onRestore");
        super.onRestoreInstanceState(parcel);

    }
}
