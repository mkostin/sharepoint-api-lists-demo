package com.example.office.mail.actions;

import com.example.office.OfficeApplication;
import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.mail.data.BoxedMailItem;
import com.example.office.mail.data.MailConfig;
import com.example.office.mail.storage.MailConfigPreferences;
import com.example.office.ui.animate.AnimatedListViewTouchListener;
import com.example.office.ui.animate.actions.ListItemAnimationAction;

/**
 * Provides functional to allow user to remove email
 */
public class RemoveMailAction extends ListItemAnimationAction<BoxedMailItem> {

    /**
     * Creates new {@link MoveToListsMailAction} instance.
     * 
     * @param listener {@link AnimatedListViewTouchListener} which related to this action.
     * @param direction Swipe direction.
     * @param partition Cell partition for this action.
     */
    public RemoveMailAction(AnimatedListViewTouchListener listener, Direction direction, float partition) {
        super(listener, direction, partition, R.drawable.abc_ic_clear_normal, OfficeApplication.getContext().getResources()
                .getColor(R.color.background_remove_email));
    }

    @Override
    public void doAction(Object... params) throws IllegalArgumentException {
        try {
            int position = (Integer) params[0];
            MailConfig config = MailConfigPreferences.loadConfig();
            BoxedMailItem mail = getItemByListViewPosition(position);
            currentItem = mail;
            complete();
            config.removeMailById(mail.getId());
            MailConfigPreferences.saveConfiguration(config);
            // TODO perform action on server side
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "doAction(): Error.");
        }
    }

}
