package com.example.office.mail.network;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.example.office.Constants.UI;
import com.example.office.mail.data.BoxedMailItem;
import com.example.office.mail.data.EmailParser;

public class MailRequestHttpOperation extends HttpOperation {

	private String url;
	private ArrayList<BoxedMailItem> mailItems;

	public MailRequestHttpOperation(OnOperaionExecutionListener listener, String url, Context context) {
		super(listener, context);
		this.url = url;
	}

	@Override
	protected String getServerUrl() {
		return this.url;
	}

    protected boolean handleServerResponse(String response) {
        try {
            EmailParser parser = new EmailParser(response);
            mailItems = new ArrayList<BoxedMailItem>();

            for (int i = 0, l = parser.getEmailsCount(); i < l; ++i) {                
                mailItems.add(new BoxedMailItem(parser.getEmail(i), UI.Screen.MAILBOX));
            }
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    public List<BoxedMailItem> getResult() {
        return mailItems;
    }
}
