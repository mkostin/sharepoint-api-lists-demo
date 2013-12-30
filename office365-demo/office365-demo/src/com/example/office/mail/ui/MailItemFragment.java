package com.example.office.mail.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.mail.data.BoxedMailItem;
import com.example.office.mail.data.MailConfig;
import com.example.office.mail.data.odata.Importance;
import com.example.office.mail.data.odata.ItemBody;
import com.example.office.mail.storage.MailConfigPreferences;
import com.example.office.ui.BaseFragment;
import com.example.office.utils.DateTimeUtils;

/**
 * Email details fragment.
 */
public class MailItemFragment extends BaseFragment {
    
    /**
     * Currently displayed email
     */
    protected BoxedMailItem mail;

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.mail_item_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        try {
            ActionBarActivity activity = (ActionBarActivity) getActivity();
            activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            
            Intent intent = getActivity().getIntent();
            mail = (BoxedMailItem) intent.getExtras().get(getActivity().getString(R.string.intent_mail_key));
            displayMail(rootView);
            ((ActionBarActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCreateView(): Error.");
        }

        return rootView;
    }

    /**
     * Fills fragment content with email fields
     * @param root Root view for current fragment
     */
    private void displayMail(View root) {
        try {
            TextView subjectView = (TextView) root.findViewById(R.id.mail_fragment_subject);
            subjectView.setText(mail.getSubject());

            String sender = getActivity().getString(R.string.unknown_sender_text_stub);
            if (mail.getSender() != null) {
                if (!TextUtils.isEmpty(mail.getSender().getAddress())) {
                    sender = mail.getSender().getAddress();
                }
            }
            TextView participantsView = (TextView) root.findViewById(R.id.mail_fragment_participants);
            // TODO: add full list of ToRecipients, CcRecipients, BccRecipients
            participantsView.setText(R.string.me_and_somebody_text_stub + sender);

            ImageView importanceIcon = (ImageView) root.findViewById(R.id.mail_fragment_icon_mark_as_important);
            if (mail.getImportance() == Importance.HIGH) {
                importanceIcon.setImageResource(android.R.drawable.star_on);
            } else {
                importanceIcon.setImageResource(android.R.drawable.star_off);
            }
            importanceIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Importance importance = mail.getImportance();
                    if (importance == Importance.HIGH) {
                        importance = Importance.NORMAL;
                    } else {
                        importance = Importance.HIGH;
                    }

                    setEmailImportance(importance);

                    if (importance == Importance.HIGH) {
                        ((ImageView) v).setImageResource(android.R.drawable.star_on);
                    } else {
                        ((ImageView) v).setImageResource(android.R.drawable.star_off);
                    }
                }
            });

            TextView senderView = (TextView) root.findViewById(R.id.mail_fragment_sender);
            senderView.setText(sender);
            // TODO: if sender is me, display "Me"

            TextView dateView = (TextView) root.findViewById(R.id.mail_fragment_date);
            dateView.setText(DateTimeUtils.getDefaultFormatter(mail.getDateTimeReceived()).format(mail.getDateTimeReceived()));

            WebView webview = (WebView) root.findViewById(R.id.mail_fragment_content);
            if (mail.getBody().getContentType() == ItemBody.BodyType.HTML) {
                webview.loadData(mail.getBody().getContent(), getActivity().getString(R.string.mime_type_text_html), "utf8"); // TODO utf8?
            } else {
                webview.loadData(mail.getBody().getContent(), getActivity().getString(R.string.mime_type_text_plain), "utf8");
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".displayMail(): Error.");
        }
    }

    /**
     * Sets and saves current mail importance
     * 
     * @param importance Indicates new importance.
     */
    private void setEmailImportance(Importance importance) {
        try {
            mail.setImportance(importance);
            MailConfig config = MailConfigPreferences.loadConfig();
            config.updateMailById(mail.getId(), mail);
            MailConfigPreferences.saveConfiguration(config);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".setEmailImportance(): Error.");
        }
    }
}
