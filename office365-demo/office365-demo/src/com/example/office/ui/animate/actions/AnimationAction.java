package com.example.office.ui.animate.actions;

import com.example.office.logger.Logger;
import com.example.office.ui.animate.AnimatedListViewTouchListener;

/**
 * Base class for all animation operations that may be invoked on mails
 */
public abstract class AnimationAction {

    /**
     * Direction of operation
     */
    public enum Direction {
        LEFT, RIGHT
    }

    /**
     * Direction where this action will be attached to
     */
    protected Direction direction;

    /**
     * Part of screen where this action will affect
     */
    protected float partition;

    /**
     * An icon id for this action
     */
    protected int iconId;

    /**
     * Background color for this action
     */
    protected int bgColor;

    /**
     * Default constructor used when all assignments implemented in descendants
     */
    protected AnimationAction() {}

    protected AnimationAction(Direction direction, float partition, int iconId, int bgColor) {
        this.direction = direction;
        this.partition = partition;
        this.iconId = iconId;
        this.bgColor = bgColor;
    }

    /**
     * When implemented in descendants performs the action itself
     */
    public abstract void doAction(Object... params) throws IllegalArgumentException;

    /**
     * Notifies related {@link AnimatedListViewTouchListener} that this action has finished and listener can perform actions by animating
     * remaining items
     */
    protected abstract void complete();

    /**
     * Notifies related {@link AnimatedListViewTouchListener} that this action has canceled and listener can show appropriate animation
     */
    protected abstract void cancel();

    /**
     * Called when operation totally finished and you need to perform actions on updating related components like UI. Default implementation
     * updates adapter and footer for current view.
     * 
     * @param params Parameters may be passed for finishing action.
     * @return true if finishing was handled and false otherwise.
     */
    public abstract boolean onFinish(Object... params);

    /**
     * Called after preparation part of ooperation when all needed data for action received. Implementation must return true if action was
     * handled and false otherwise. Default implementation notifies {@link listener} to make it finish animations.
     * 
     * @param params Parameters may be passed to method for processing
     * @return true if this case was handled and false otherwise.
     */
    public boolean onComplete(Object... params) {
        try {
            complete();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onFinish(): Error.");
        }

        return false;
    }

    /**
     * Called when action was cancelled.
     * 
     * @param params Parameters may be passed to cancel action.
     * @return true if cancellation was handled and false otherwise.
     */
    public boolean onCancel(Object... params) {
        try {
            cancel();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCancel(): Error.");
        }

        return false;
    }

    public void setDirection(Direction d) {
        direction = d;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setPartition(float p) {
        partition = p;
    }

    public float getPartition() {
        return partition;
    }

    public void setIconid(int iconId) {
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setBgColor(int color) {
        this.bgColor = color;
    }

    public int getBgColor() {
        return this.bgColor;
    }
}
