package com.example.office.mail.actions;

import com.example.office.Constants;
import com.example.office.OfficeApplication;
import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.mail.data.BoxedMailItem;
import com.example.office.mail.data.MailConfig;
import com.example.office.mail.storage.MailConfigPreferences;
import com.example.office.ui.animate.AnimatedListViewTouchListener;
import com.example.office.ui.animate.actions.ListItemAnimationAction;

/**
 * Provides functional to allow user to move email to archive
 */
public class MoveToArchiveMailAction extends ListItemAnimationAction<BoxedMailItem> {

    /**
     * Creates new {@link MoveToListsMailAction} instance.
     * 
     * @param listener {@link AnimatedListViewTouchListener} which related to this action.
     * @param direction Swipe direction.
     * @param partition Cell partition for this action.
     */
    public MoveToArchiveMailAction(AnimatedListViewTouchListener listener, Direction direction, float partition) {
        super(listener, direction, partition, R.drawable.abc_ic_cab_done_holo_light, OfficeApplication.getContext().getResources()
                .getColor(R.color.background_move_to_archive));
    }

    @Override
    public void doAction(Object... params) throws IllegalArgumentException {
        try {
            int position = (Integer) params[0];
            MailConfig config = MailConfigPreferences.loadConfig();
            BoxedMailItem mail = getItemByListViewPosition(position);
            currentItem = mail;
            complete();
            mail.setBox(Constants.UI.Screen.ARCHIVE);
            config.updateMailById(mail.getId(), mail);
            MailConfigPreferences.saveConfiguration(config);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doAction(): Error.");
        }
    }

}
