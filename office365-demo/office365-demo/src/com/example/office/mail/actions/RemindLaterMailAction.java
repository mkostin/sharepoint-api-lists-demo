package com.example.office.mail.actions;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.office.Constants;
import com.example.office.OfficeApplication;
import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.mail.data.BoxedMailItem;
import com.example.office.mail.data.MailConfig;
import com.example.office.mail.storage.MailConfigPreferences;
import com.example.office.mail.ui.LaterSelectActivity;
import com.example.office.ui.animate.AnimatedListViewTouchListener;
import com.example.office.ui.animate.actions.ListItemAnimationAction;

/**
 * Provides functional to allow user to set reminder for given email
 */
public class RemindLaterMailAction extends ListItemAnimationAction<BoxedMailItem> {

    /**
     * Identifier for extracting email position from intent
     */
    public static final String EMAIL_ID_EXTRA = "EMAIL_ID";

    /**
     * Fragment that owns this item.
     */
    protected Fragment fragment;

    /**
     * Creates new {@link MoveToListsMailAction} instance.
     * 
     * @param listener {@link AnimatedListViewTouchListener} which related to this action.
     * @param direction Swipe direction.
     * @param partition Cell partition for this action.
     * @param fragment Fragment that owns this item.
     */
    public RemindLaterMailAction(AnimatedListViewTouchListener listener, Direction direction, float partition, Fragment fragment) {
        super(listener, direction, partition, android.R.drawable.ic_popup_reminder, OfficeApplication.getContext().getResources()
                .getColor(R.color.background_remind_later));
        this.fragment = fragment;
    }

    @Override
    public void doAction(Object... params) throws IllegalArgumentException {
        try {
            // TODO change ListChooseDialog to reminder choose dialog
            Intent intent = new Intent(OfficeApplication.getContext(), LaterSelectActivity.class);
            int position = (Integer) params[0];
            BoxedMailItem mail = getItemByListViewPosition(position);
            currentItem = mail;
            intent.putExtra(EMAIL_ID_EXTRA, mail.getId());
            fragment.startActivityForResult(intent, Constants.CHOOSE_REMIND_TIME_REQUEST);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "doAction(): Error.");
        }
    }

    @Override
    public boolean onComplete(Object... params) {
        try {
            if (params == null || !(params[0] instanceof Integer) || !(params[1] instanceof Intent)) return false;

            if (Constants.CHOOSE_REMIND_TIME_REQUEST == (Integer) (params[0])) {
                complete();
                Intent data = (Intent) params[1];
                String mailId = data.getExtras().getString(RemindLaterMailAction.EMAIL_ID_EXTRA);

                // TODO: set reminder

                MailConfig config = MailConfigPreferences.loadConfig();
                if (config != null) {
                    BoxedMailItem mail = config.getMailById(mailId);
                    if (mail != null) {
                        config.updateMailById(mailId, mail.setBox(Constants.UI.Screen.LATER));
                        MailConfigPreferences.saveConfiguration(config);
                    }
                }

                return true;
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onFinish(): Error.");
        }

        return false;
    }

    @Override
    public boolean onCancel(Object... params) {
        try {
            cancel();
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCancel(): Error.");
        }

        return super.onCancel(params);
    }
}
