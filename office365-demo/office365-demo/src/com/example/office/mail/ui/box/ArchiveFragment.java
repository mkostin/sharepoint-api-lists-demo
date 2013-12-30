package com.example.office.mail.ui.box;

import java.util.ArrayList;
import java.util.List;

import com.example.office.Constants;
import com.example.office.Constants.UI;
import com.example.office.logger.Logger;
import com.example.office.mail.actions.MoveToMailBoxMailAction;
import com.example.office.mail.actions.RemindLaterMailAction;
import com.example.office.mail.actions.RemoveMailAction;
import com.example.office.ui.animate.actions.AnimationAction;
import com.example.office.ui.animate.actions.AnimationAction.Direction;

/**
 * 'Archive' fragment containing logic related to managing emails that are considered to be 'done' i.e. no further actions are required on
 * it.
 */
public class ArchiveFragment extends BoxFragment {

    @Override
    protected UI.Screen getBox() {
        return UI.Screen.ARCHIVE;
    }

    @Override
    protected List<AnimationAction> getAnimationActions() {
        try {
            List<AnimationAction> list = new ArrayList<AnimationAction>();
            list.add(new MoveToMailBoxMailAction(mListener, Direction.LEFT, Constants.SCREEN_PART_FOR_DEFAULT_ACTION));
            list.add(new RemindLaterMailAction(mListener, Direction.LEFT, 1 - Constants.SCREEN_PART_FOR_DEFAULT_ACTION, this));
            list.add(new RemoveMailAction(mListener, Direction.RIGHT, 1));
            return list;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "getAnimationActions(): Error.");
        }
        return null;
    }

}
