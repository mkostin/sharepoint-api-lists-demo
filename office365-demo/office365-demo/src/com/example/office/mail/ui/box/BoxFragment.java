package com.example.office.mail.ui.box;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.office.Constants;
import com.example.office.Constants.UI;
import com.example.office.OfficeApplication;
import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.mail.adapters.MailItemAdapter;
import com.example.office.mail.data.BoxedMailItem;
import com.example.office.mail.data.MailConfig;
import com.example.office.mail.storage.MailConfigPreferences;
import com.example.office.mail.ui.MailItemActivity;
import com.example.office.ui.animate.AnimatedListFragment;
import com.example.office.ui.animate.actions.AnimationAction;
import com.example.office.utils.Utils;

/**
 * Base Box fragment containing logic related to managing box items.
 */
public abstract class BoxFragment extends AnimatedListFragment<BoxedMailItem, MailItemAdapter> {

    /**
     * View used as a footer of the list;
     */
    protected View mListFooterView;

    /**
     * Tells if Activity that has been started for result has returned it and it has been handled.
     */
    protected boolean mIsResumeEventHandled;

    /**
     * Default constructor.
     */
    public BoxFragment() {
        super();
        try {
            mIsResumeEventHandled = false;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".constructor(): Error.");
        }
    }

    protected int getListItemLayoutId() {
        return R.layout.mail_list_item;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.mail_list_fragment;
    }

    @Override
    protected int getListViewId() {
        return R.id.mail_list;
    }

    @Override
    protected int getProgressViewId() {
        return R.id.mail_list_progress;
    }

    @Override
    protected int getContentContainerId() {
        return R.id.mail_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        try {
            final ActionBarActivity activity = (ActionBarActivity) getActivity();

            final ListView mailListView = (ListView) rootView.findViewById(getListViewId());
            mailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        BoxedMailItem mail = getListAdapterInstance().getItem(position);
                        mail.setIsRead(true);
                        MailConfig config = MailConfigPreferences.loadConfig();
                        config.updateMailById(mail.getId(), mail);
                        MailConfigPreferences.saveConfiguration(config);

                        Intent intent = new Intent(OfficeApplication.getContext(), MailItemActivity.class);
                        intent.putExtra(getActivity().getString(R.string.intent_mail_key), mail);
                        activity.startActivity(intent);

                    } catch (Exception e) {
                        Logger.logApplicationException(e, getClass().getSimpleName() + ".listView.onItemClick(): Error.");
                    }
                }
            });

        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCreateView(): Error.");
        }
        return rootView;
    }

    /**
     * Returns {@link Constants.UI.Screen} tht this fragment is describing.
     *
     * @return Box for this fragment, or <code>null</code> in case of error.
     */
    protected abstract UI.Screen getBox();

    @Override
    protected List<BoxedMailItem> getListData() {
        try {
            MailConfig config = MailConfigPreferences.loadConfig();
            boolean isValidList = false;
            if (config != null) {
                List<BoxedMailItem> mails = config.getMails();
                isValidList = mails != null && !mails.isEmpty();
                if (isValidList) {
                    return Utils.boxMail(mails, getBox());
                }
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getListData(): Error.");
        }
        return null;
    }

    @Override
    protected View getListFooterViewInstance() {
        try {
            if (mListFooterView == null) {
                mListFooterView = mInflater.inflate(R.layout.mail_list_footer, null);
                ((TextView) mListFooterView.findViewById(R.id.footer_mail_count)).setText(String.valueOf(getListAdapterInstance().getCount()));
            }
            return mListFooterView;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getListFooterView(): Error.");
        }
        return null;
    }

    @Override
    public MailItemAdapter getListAdapterInstance(List<BoxedMailItem> data) {
        try {
            if (mAdapter == null) {
                mAdapter = new MailItemAdapter(getActivity(), getListItemLayoutId(), data != null ? data : getListData());
            }
            return mAdapter;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getListAdapter(): Error.");
        }
        return null;
    }

    @Override
    public void onResume() {
        try {
            if (!mIsResumeEventHandled) {
                initList();
            }

            mIsResumeEventHandled = false;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onResume(): Error.");
        } finally {
            super.onResume();
        }
    }

    @Override
    protected void initList() {
        try {
            List<BoxedMailItem> mails = getListData();
            if (mails != null && !mails.isEmpty()) {
                updateList(mails);
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "initList(): Error.");
        }
    }

    /**
     * Updates list with new data.
     *
     * @param items Items to be displayed in the list.
     */
    protected void updateList(List<BoxedMailItem> items) {
        try {
            getListAdapterInstance().update(Utils.boxMail(items, getBox()));

            View rootView = getView();
            if (rootView != null) {
                ListView mailListView = (ListView) rootView.findViewById(getListViewId());
                if (mailListView != null) {
                    mailListView.setAdapter(getListAdapterInstance());
                }

                if (mListFooterView != null) {
                    if (getListAdapterInstance().getCount() <= 0) {
                        mailListView.removeFooterView(mListFooterView);
                    } else {
                        if (mailListView.getFooterViewsCount() == 0) {
                            mailListView.addFooterView(mListFooterView);
                        }
                        ((TextView) mListFooterView.findViewById(R.id.footer_mail_count))
                                .setText(String.valueOf(getListAdapterInstance().getCount()));
                    }
                }
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".updateList(): Error.");
        }
    }

    /**
     * Tracks if event has been handled so that fragment content wouldn't be initialized on next resume. Goes through all
     * {@link AnimationAction}s attached to animation listener and asks them to handle result with
     * {@link AnimationAction#onComplete(Object...)} or {@link AnimationAction#onCancel(Object...)}. Returns as soon as at least one of the
     * actions is able to handle it.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            mIsResumeEventHandled = false;

            for (AnimationAction action : mListener.getAnimationActions()) {
                if (action != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        mIsResumeEventHandled = action.onComplete(requestCode, data);
                    } else {
                        mIsResumeEventHandled = action.onCancel(requestCode, data);
                    }
                    if (mIsResumeEventHandled) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "onActivityResult(): Error.");
        } finally {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
