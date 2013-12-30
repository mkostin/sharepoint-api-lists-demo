package com.example.office.mail.ui.box;

import java.util.ArrayList;
import java.util.List;

import com.example.office.Constants;
import com.example.office.Constants.UI;
import com.example.office.logger.Logger;
import com.example.office.mail.actions.MoveToArchiveMailAction;
import com.example.office.mail.actions.MoveToListsMailAction;
import com.example.office.mail.actions.MoveToMailBoxMailAction;
import com.example.office.mail.actions.RemindLaterMailAction;
import com.example.office.ui.animate.actions.AnimationAction;
import com.example.office.ui.animate.actions.AnimationAction.Direction;

/**
 * 'Later' fragment containing logic related to managing Later/TODO emails.
 */
public class LaterFragment extends BoxFragment {

    @Override
    protected UI.Screen getBox() {
        return UI.Screen.LATER;
    }

    @Override
    protected List<AnimationAction> getAnimationActions() {
        try {
            List<AnimationAction> list = new ArrayList<AnimationAction>();
            list.add(new RemindLaterMailAction(mListener, Direction.LEFT, Constants.SCREEN_PART_FOR_DEFAULT_ACTION, this));
            list.add(new MoveToListsMailAction(mListener, Direction.LEFT, 1 - Constants.SCREEN_PART_FOR_DEFAULT_ACTION, this));
            list.add(new MoveToMailBoxMailAction(mListener, Direction.RIGHT, Constants.SCREEN_PART_FOR_DEFAULT_ACTION));
            list.add(new MoveToArchiveMailAction(mListener, Direction.RIGHT, 1 - Constants.SCREEN_PART_FOR_DEFAULT_ACTION));
            return list;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "getAnimationActions(): Error.");
        }
        return null;
    }

}
