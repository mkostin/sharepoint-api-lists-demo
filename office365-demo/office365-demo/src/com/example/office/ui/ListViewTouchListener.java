package com.example.office.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

import com.example.office.logger.Logger;

/**
 * Touch listener for ListView
 */
public class ListViewTouchListener implements OnTouchListener {

    /**
     * Gesture detector
     */
    private final GestureDetector gestureDetector;

    /**
     * View that instance of this class is attached to
     */
    protected final ListView view;

    /**
     * Gesture listener
     */
    private final GestureListener gestureListener;

    public ListViewTouchListener(Context context, ListView view) {
        this.gestureListener = new GestureListener();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.view = view;
    }

    public ListView getView() {
        return view;
    }

    /**
     * Gets a view matches given position
     * 
     * @param position number of item in ListView
     * @return subject or null on failure
     */
    public View getActiveView(int position) {
        // translate index of all elements to index of visible elements
        int firstPosition = view.getFirstVisiblePosition();
        int visiblePosition = position - firstPosition;
        if (visiblePosition >= 0 && visiblePosition < view.getChildCount()) {
            return view.getChildAt(visiblePosition);
        }
        return null;
    }

    /**
     * We need to listen to onUp event but SimpleOnGestureListener does not provide such event support
     */
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        if (gestureDetector.onTouchEvent(e)) return true;
        if (e.getAction() == MotionEvent.ACTION_UP) return gestureListener.onUp(e);
        return false;
    }

    /**
     * Gesture listener
     */
    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;

        private static final int SCROLL_THRESHOLD = 0;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onUp(MotionEvent e) {
            try {
                int position = view.pointToPosition((int) e.getX(), (int) e.getY());
                View v = getActiveView(position);
                if (v == null) return false;

                return onTouchEnd(position);
            } catch (Exception e2) {
                Logger.logApplicationException(e2, getClass().getSimpleName() + "onUp(): Error.");
                return false;
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            try {
                int position = view.pointToPosition((int) e2.getX(), (int) e2.getY());

                // make sure we are on the same item
                if (view.pointToPosition((int) e1.getX(), (int) e1.getY()) == position) {
                    float diffX = e2.getX() - e1.getX();
                    float diffY = e2.getY() - e1.getY();
                    if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SCROLL_THRESHOLD) {
                        if (diffX < 0) {
                            onScrollLeft(position, (int) diffX);
                        } else {
                            onScrollRight(position, (int) diffX);
                        }

                        return true;
                    }
                }
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + "onScroll(): Error.");
            }

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                int position = view.pointToPosition((int) e2.getX(), (int) e2.getY());

                // make sure we are on the same item
                if (view.pointToPosition((int) e1.getX(), (int) e1.getY()) == position) {
                    float diff = e2.getX() - e1.getX();
                    if (Math.abs(diff) > SWIPE_THRESHOLD) {
                        if (diff < 0) {
                            onSwipeLeft(position, (int) diff);
                        } else {
                            onSwipeRight(position, (int) diff);
                        }
                    } else {
                        // notify item that scroll ended and it can move back to
                        // source position
                        // provide direction anyway because users can implement
                        // theirs own animation logic for this case
                        if (diff < 0) {
                            onScrollLeft(position, 0);
                        } else {
                            onScrollRight(position, 0);
                        }
                    }

                    return true;
                }
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + "onFling(): Error.");
            }

            return false;
        }

    }

    public void onScrollRight(int position, int delta) {}

    public void onScrollLeft(int position, int delta) {}

    public void onSwipeRight(int position, int delta) {}

    public void onSwipeLeft(int position, int delta) {}

    public boolean onTouchEnd(int position) {return false;}
}
