package com.example.office.ui.animate;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.office.Constants;
import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.ui.ListViewTouchListener;
import com.example.office.ui.animate.actions.AnimationAction;
import com.example.office.ui.animate.actions.AnimationAction.Direction;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * Provides logic for animating items in related ListView
 */
public class AnimatedListViewTouchListener extends ListViewTouchListener {

    /**
     * Position of item which user is currently interacting
     */
    private int currentItemPosition;

    /**
     * List of actions may be performed when item scrolled to left
     */
    private ArrayList<AnimationAction> leftActions;

    /**
     * List of actions may be performed when item scrolled to right
     */
    private ArrayList<AnimationAction> rightActions;

    /**
     * Duration of swipe animation (ms)
     */
    private final int animationDuration = 500;

    /**
     * Currently running action.
     */
    private AnimationAction currentAction = null;

    /**
     * Basic constructor
     * 
     * @param context Context where this {@link AnimatedListViewTouchListener} instance is created
     * @param view ListView which this {@link AnimatedListViewTouchListener} is applied to
     */
    public AnimatedListViewTouchListener(Context context, ListView view) {
        super(context, view);
        this.leftActions = new ArrayList<AnimationAction>();
        this.rightActions = new ArrayList<AnimationAction>();
    }

    /**
     * Creates and returns animator on for given view with specified properties.
     * 
     * @param v View which animator will be applied.
     * @param property Animating property name.
     * @param from Start value for animation. May be null if does not matter.
     * @param to End value for animation. CAN NOT be null.
     * @param duration Animation duration in milliseconds.
     * @param interpolator Animation interpolator. Default is LinearInterpolator.
     * @param updateListener Listener to be invoked on each animation frame. May be null.
     * @param listener Listener to be invoked on animation start/end/repeat/cancel. May be null.
     * @return ObjectAnimator with properties set or null if something went wrong.
     * @throws IllegalArgumentException Thrown when any of <b>v</b>, <b>property</b> or <b>to</b> parameters is null.
     */
    private ObjectAnimator getAnimator(View v, String property, Integer from, Integer to, int duration, Interpolator interpolator,
            AnimatorUpdateListener updateListener, AnimatorListenerAdapter listener) throws IllegalArgumentException {
        try {
            if (v == null) {
                throw new IllegalArgumentException("v");
            }
            if (property == null) {
                throw new IllegalArgumentException("property");
            }
            if (to == null) {
                throw new IllegalArgumentException("to");
            }
            if (interpolator == null) {
                interpolator = new LinearInterpolator();
            }

            ObjectAnimator animator;
            if (from != null) {
                animator = ObjectAnimator.ofFloat(v, property, from, to);
            } else {
                animator = ObjectAnimator.ofFloat(v, property, to);
            }

            animator.setInterpolator(interpolator);
            animator.setDuration(duration);
            if (updateListener != null) {
                animator.addUpdateListener(updateListener);
            }
            if (listener != null) {
                animator.addListener(listener);
            }

            return animator;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getAnimator(): Error.");
        }

        return null;
    }

    /**
     * Notifies AnimatedListViewTouchListener that performed action has been canceled
     */
    public void notifyActionCanceled() {
        try {
            View dismissView = getActiveView(currentItemPosition);
            View background = dismissView.findViewById(R.id.mail_item_background);
            View foreground = dismissView.findViewById(R.id.mail_item_foreground);
            ImageView leftIcon = (ImageView) background.findViewById(R.id.mail_item_left), rightIcon = (ImageView) background
                    .findViewById(R.id.mail_item_right);
            boolean toRight = ViewHelper.getTranslationX(foreground) < 0;
            int iconWidth = iconWithMarginSize(dismissView, !toRight);

            BounceInterpolator interpolator = new BounceInterpolator();
            if (toRight) {
                getAnimator(rightIcon, "translationX", null, iconWidth, animationDuration, interpolator, null, null).start();
            } else {
                getAnimator(leftIcon, "translationX", null, -iconWidth, animationDuration, interpolator, null, null).start();
            }

            getAnimator(foreground, "translationX", null, 0, animationDuration, interpolator, null, null).start();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".notifyActionCanceled(): Error.");
        }
    }

    /**
     * Animates items below current to bubble them up and removes current.
     */
    private void animateNextItems() {
        try {
            final View dismissView = getActiveView(currentItemPosition);
            final View background = dismissView.findViewById(R.id.mail_item_background);
            final View foreground = dismissView.findViewById(R.id.mail_item_foreground);
            final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) foreground.getLayoutParams();
            final int originalHeight = dismissView.getHeight();

            ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(animationDuration);

            AnimatorUpdateListener updateListener = new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator a) {
                    FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(background.getWidth(), (Integer) a.getAnimatedValue());
                    lp.height = p.height;
                    background.setLayoutParams(p);
                    foreground.setLayoutParams(lp);
                }
            };

            animator.addUpdateListener(updateListener);

            AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        // restore View properties
                        ValueAnimator va = ValueAnimator.ofInt(originalHeight).setDuration(0);
                        va.addUpdateListener(new AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator a) {
                                FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(background.getWidth(), originalHeight);
                                background.setLayoutParams(p);
                                foreground.setLayoutParams(p);
                                getAnimator(foreground, "translationX", null, 0, 0, null, null, null).start();
                                a.removeAllListeners();
                            }
                        });

                        va.start();

                        // save current scroll
                        int index = getView().getFirstVisiblePosition();
                        View v = getView().getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        currentAction.onFinish();

                        // restore scroll (do we really need explicitly do it?)
                        getView().setSelectionFromTop(index, top);

                    } catch (Exception e) {
                        Logger.logApplicationException(e, getClass().getSimpleName() + "onAnimationEnd(): Error.");
                    }
                }
            };

            animator.addListener(listener);

            animator.start();
        } catch (Exception ex) {
            Logger.logApplicationException(ex, getClass().getSimpleName() + ".animateNextItems(): Error.");
        }
    }

    /**
     * Notifies {@link AnimatedListViewTouchListener} that performed action has finished
     */
    public void notifyActionCompleted() {
        try {
            animateNextItems();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".notifyActionCompleted(): Error.");
        }
    }

    /**
     * Determines should scroll on given item in list be ignored
     * 
     * @param delta Offset.
     * @param v ListView item.
     * @return true if offset is too small, false otherwise
     */
    private boolean shouldScrollBeIgnored(int delta, View v) {
        try {
            return Math.abs(delta) < iconWithMarginSize(v, delta < 0);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".shouldScrollBeIgnored(): Error.");
        }
        return false;
    }

    /**
     * Calculates size of icon in given ListView item with its margins
     * 
     * @param v View containing icon
     * @param left Indicates should we consider left or right icon
     * @return width of icon and its left and right margins
     */
    private int iconWithMarginSize(View v, boolean left) {
        try {
            View background = v.findViewById(R.id.mail_item_background);
            ImageView image = (ImageView) (left ? background.findViewById(R.id.mail_item_left) : background.findViewById(R.id.mail_item_right));
            RelativeLayout.LayoutParams params = (LayoutParams) image.getLayoutParams();
            return params.leftMargin + params.rightMargin + image.getWidth();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".iconWithMarginSize(): Error.");
        }

        return 0;
    }

    /**
     * Determines new color of background by given delta
     * 
     * @param background View to change background color
     * @param delta Offset of foreground
     */
    private void changeBackgroundColor(View background, int delta) {
        try {
            AnimationAction action = getAction(delta);
            if (action == null) {
                Log.e(Constants.APP_TAG, getClass().getName() + " is not filled correctly");
                return;
            }
            background.setBackgroundColor(action.getBgColor());
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".changeBackgroundColor(): Error.");
        }
    }

    /**
     * Shows appropriate icon on background and hides other
     * 
     * @param background Background view
     * @param delta Current offset
     */
    private void showAppropriateIcon(View background, int delta) {
        try {
            ImageView leftIcon = (ImageView) background.findViewById(R.id.mail_item_left), rightIcon = (ImageView) background
                    .findViewById(R.id.mail_item_right);

            AnimationAction action = getAction(delta);
            if (action == null) {
                Log.e(Constants.APP_TAG, getClass().getName() + " is not filled correctly");
                return;
            }
            if (delta < 0) {
                rightIcon.setImageResource(action.getIconId());
                rightIcon.setVisibility(View.VISIBLE);
                leftIcon.setVisibility(View.INVISIBLE);
            } else {
                leftIcon.setImageResource(action.getIconId());
                leftIcon.setVisibility(View.VISIBLE);
                rightIcon.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".showAppropriateIcon(): Error.");
        }
    }

    /**
     * Slides ListView item for given offset
     * 
     * @param position Index of item in ListView.
     * @param delta Offset.
     */
    private void moveCurrentItem(int position, final int delta) {
        try {
            FrameLayout fl = (FrameLayout) getActiveView(position);
            if (fl == null) {
                return;
            }

            View background = fl.findViewById(R.id.mail_item_background);
            final View foreground = fl.findViewById(R.id.mail_item_foreground);

            ImageView leftIcon = (ImageView) background.findViewById(R.id.mail_item_left), rightIcon = (ImageView) background
                    .findViewById(R.id.mail_item_right);

            int iconOffset = delta - (int) Math.signum(delta) * iconWithMarginSize(fl, delta < 0);

            if (delta != 0) {
                changeBackgroundColor(background, delta);
                showAppropriateIcon(background, delta);

                // if we set layout params each time there are performance issues so set first time only
                if (background.getHeight() != foreground.getHeight()) {
                    background.setLayoutParams(new FrameLayout.LayoutParams(background.getWidth(), foreground.getHeight()));
                }

                if (shouldScrollBeIgnored(delta, fl)) {
                    background.setBackgroundColor(Color.GRAY);
                    iconOffset = 0;
                }

                getAnimator(delta > 0 ? leftIcon : rightIcon, "translationX", null, iconOffset, 0, null, null, null).start();
            }

            getAnimator(foreground, "translationX", null, delta, 0, null, null, null).start();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".moveCurrentItem(): Error.");
        }
    }

    public void onScrollLeft(int position, int delta) {
        try {
            // disable scroll on footer
            // we need to animate and perform action for all items in current box for this case
            if (getView().getAdapter().getItemViewType(position) == AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                return;
            }
            this.currentItemPosition = position;
            moveCurrentItem(position, delta);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onScrollLeft(): Error.");
        }
    }

    public void onScrollRight(int position, int delta) {
        try {
            // disable scroll on footer
            // we need to animate and perform action for all items in current box for this case
            if (getView().getAdapter().getItemViewType(position) == AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                return;
            }
            this.currentItemPosition = position;
            moveCurrentItem(position, delta);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onScrollRight(): Error.");
        }
    }

    /**
     * Determines action to execute by given foreground offset
     * 
     * @param offset Foreground offset
     * @return Appropriate mail action
     */
    private AnimationAction getAction(int offset) {
        try {
            if (offset < 0) {
                float partition = (float) -offset / getView().getWidth();
                float skippedPart = 0;
                for (AnimationAction a : leftActions) {
                    float p = a.getPartition();
                    if (skippedPart + p > partition) {
                        return a;
                    } else {
                        skippedPart += p;
                    }
                }
            } else {
                float partition = (float) offset / getView().getWidth();
                float skippedPart = 0;
                for (AnimationAction a : rightActions) {
                    float p = a.getPartition();
                    if (skippedPart + p > partition) {
                        return a;
                    } else {
                        skippedPart += p;
                    }
                }
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getAction(): Error.");
        }

        // this case takes place when leftActions or rightActions filled incorrectly
        return null;
    }

    /**
     * Shows animation for removing current item and performs action related
     * 
     * @param position Index of current item in ListView
     * @param toRight Animation direction
     */
    private void animateGivenItem(final int position, final boolean toRight) {
        try {
            View v = getActiveView(position);
            if (v == null) {
                return;
            }

            final View fg = v.findViewById(R.id.mail_item_foreground);
            final View bg = v.findViewById(R.id.mail_item_background);
            final int startOffset = (int) ViewHelper.getTranslationX(fg);
            currentAction = getAction(toRight ? (int) Math.abs(startOffset) : -(int) Math.abs(startOffset));
            if (currentAction == null) {
                Log.e(Constants.APP_TAG, getClass().getName() + " is not filled correctly");
                return;
            }

            AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    try {
                        // pass current item position to action
                        currentAction.doAction(position);
                        a.removeAllListeners();
                    } catch (Exception e) {
                        Logger.logApplicationException(e, getClass().getSimpleName() + "onAnimationEnd(): Error.");
                    }
                }
            };

            int width = fg.getWidth();

            ObjectAnimator animator = getAnimator(fg, "translationX", startOffset,
                    toRight ? v.getWidth() + iconWithMarginSize(v, !toRight) + Math.abs(startOffset) : -v.getWidth()
                            - iconWithMarginSize(v, !toRight) - Math.abs(startOffset),
                    Math.round(animationDuration * (width - Math.abs(startOffset)) / width), null, null, listener);

            ObjectAnimator iconAnimator;
            if (toRight) {
                ImageView icon = (ImageView) bg.findViewById(R.id.mail_item_left);
                iconAnimator = getAnimator(icon, "translationX", null, v.getWidth(),
                        Math.round(animationDuration * (width - Math.abs(startOffset)) / width), null, null, null);
            } else {
                ImageView icon = (ImageView) bg.findViewById(R.id.mail_item_right);
                iconAnimator = getAnimator(icon, "translationX", null, -v.getWidth(),
                        Math.round(animationDuration * (width - Math.abs(startOffset)) / width), null, null, null);
            }

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator, iconAnimator);
            animatorSet.start();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".animateGivenItem(): Error.");
        }
    }

    @Override
    public void onSwipeLeft(int position, int delta) {
        try {
            // disable scroll on footer
            // we need to animate and perform action for all items in current box for this case
            if (getView().getAdapter().getItemViewType(position) == AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                return;
            }
            this.currentItemPosition = position;
            animateGivenItem(position, false);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onSwipeLeft(): Error.");
        }
    }

    @Override
    public void onSwipeRight(int position, int delta) {
        try {
            // disable scroll on footer
            // we need to animate and perform action for all items in current box for this case
            if (getView().getAdapter().getItemViewType(position) == AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                return;
            }
            this.currentItemPosition = position;
            animateGivenItem(position, true);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onSwipeRight(): Error.");
        }
    }

    /**
     * Invoked when touch ended on ListView cell
     * 
     * @param position Number of cell.
     * @return <b>true</b> if event was handled, <b>false</b> otherwise
     */
    @Override
    public boolean onTouchEnd(int position) {
        try {
            // disable scroll on footer
            // we need to animate and perform action for all items in current box for this case
            if (getView().getAdapter().getItemViewType(position) == AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                return false;
            }
            FrameLayout fl = (FrameLayout) getActiveView(position);
            if (fl == null) {
                return false;
            }

            View foreground = fl.findViewById(R.id.mail_item_foreground);

            // get current scroll position to determine should we act or reset view
            int delta = (int) ViewHelper.getTranslationX(foreground);
            if (shouldScrollBeIgnored(delta, fl)) {
                if (delta == 0) {
                    // this is a click and will be handled by onItemClickListener
                    return false;
                }
                if (delta < 0) {
                    onScrollRight(position, 0);
                } else {
                    onScrollLeft(position, 0);
                }
            } else {
                if (delta < 0) {
                    onSwipeLeft(position, delta);
                } else {
                    onSwipeRight(position, delta);
                }
            }
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onTouchEnd(): Error.");
            return false;
        }
    }

    /**
     * Adds given action to this listener. Actions must be added in theirs appearance order, i. e. LTR for right actions, RTL for left. Sum
     * of partitions must be equal to 1 for both left and right actions when all items have been added
     * 
     * @param action Action to be added
     */
    public void addAnimationAction(AnimationAction action) {
        try {
            if (action.getDirection() == Direction.LEFT) {
                leftActions.add(action);
            } else {
                rightActions.add(action);
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".addAnimationAction(): Error.");
        }
    }

    /**
     * Retrieves all animation actions currently attached to this listener or <code>null</code> in case of failure.
     * 
     * @return Animation actions list.
     */
    public List<AnimationAction> getAnimationActions() {
        try {
            List<AnimationAction> actions = new ArrayList<AnimationAction>(leftActions);
            actions.addAll(rightActions);
            return actions;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getAnimationActions(): Error.");
        }
        return null;
    }
}
