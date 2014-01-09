package com.example.office;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * Stores application public constants such as URLs to update configurations, default check back-in values, etc.
 */
public class Constants {

    /**
     * Endpoint to retrieve list current messages from the inbox. https://10.68.13.124/ = https://EXHB-7841/
     */
    public static final String MAIL_MESSAGES = "https://10.68.13.124/ews/odata/Me/Inbox/Messages";

    /**
     * Endpoint to retrieve content of inbox folder. https://10.68.13.124/ = https://EXHB-7841/
     */
    public static final String INBOX_FOLDER = "https://10.68.13.124/ews/odata/Me/Inbox";

    /**
     * Endpoint to retrieve list current events https://10.68.13.124/ = https://EXHB-7841/
     */
    public static final String EVENTS_LIST = "https://10.68.13.124/ews/odata/Me/Events";

    /**
     * Endpoint Endpoint to retrieve metadata https://10.68.13.124/ = https://EXHB-7841/
     */
    public static final String METADATA = "https://10.68.13.124/ews/odata/$metadata";

    /**
     * TEST Endpoint to retrieve list current messages from the inbox.
     */
    public static final String MAIL_MESSAGES_TEST = "https://sdfpilot.outlook.com/ews/";

    /**
     * TEST Endpoint to retrieve content of inbox folder
     */
    public static final String INBOX_FOLDER_TEST = "https://www.cubby.com/pl/exhb-7841.Me.Inbox.json/_09ed17d2aac94eab8e5c5e709759e0a2";

    /**
     * TEST Endpoint to retrieve list current events
     */
    public static final String EVENTS_LIST_TEST = "https://www.cubby.com/pl/exhb-7841.Me.Events.json/_657a3c8fa2c04ff8996a934147757490";

    /**
     * TEST Endpoint to retrieve metadata
     */
    public static final String METADATA_TEST = "https://www.cubby.com/pl/%24metadata.xml/_4fba2f24242446a4bdf62f31111abc97";



    public static final String SP_SITE_URL = "https://microsoft.sharepoint.com/teams/MSOpenTech-CLA/testsite/";
    // public static final String SP_SITE_URL = "http://sphvm-7052/";
    // public static final String SP_SITE_URL = "https://akvelon.sharepoint.com/sites/Sandbox/";

    /**
     * Base URL to access SharePoint and all related services.
     */
    public static final String SP_BASE_URL = SP_SITE_URL + "_api/";

    /**
     * Endpoint to retrieve metadata. Usually requires adding "$metadata" to the base URL.
     */
    public static final String SP_METADATA = SP_BASE_URL;

    /**
     * Endpoint to retrieve Lists.
     */
    public static final String SP_LISTS_URL = SP_BASE_URL + "web/lists";

    /**
     * NTLM authentication required cookie.
     */
    public static final String COOKIE_FED_AUTH = "FedAuth=77u/PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48U1A+RmFsc2UsMGguZnxtZW1iZXJzaGlwfDEwMDM3ZmZlODc5NDU4NjJAbGl2ZS5jb20sMCMuZnxtZW1iZXJzaGlwfHYtbWF4a29zQG1pY3Jvc29mdC5jb20sMTMwMzM4MDg4OTAwNDA2NTc3LEZhbHNlLGtDSFpDZGVoMExKWmZGWXhBSkxHZFRYTlBsaXlTaThvRDFvbVdRcmlMRVB5emd5QWJGcFBFSWdOV2wxZGdYYk5jN0JqTUFIWGY0Y0w5Lzd5VmZ5aVREdkY0RFQ1ZDlsMTE5eEV0d1liR1pzVEN4bE5RWXREeFZIenl3RjdWa0VhYjlTSlZYOGRKa2VnMVRBeExvNjAvTjIyUytBc3FieGlRUkVKd1lSYXYyTURCM2x4alR5NzB4dEVJMjkzb2ptQm04TzdSempGSkpsZkNvQVh1dW1ZVGw1R3BITEVmK1NNV0FxN2VnZm5QSlUvWXoyMHZmNUljUHFqVTVITWhYa2xhSEFWaFltai9Ka0pJbTlKazVMT0FFS3VzdHJpU0cxN0VtR1VULzh0OTZoYjNCMFhFZHgrTGZBZGxKZ2VKdUZqZ0ExYzBUWUppUjFKb3NHZVFqWmxWZz09LGh0dHBzOi8vbWljcm9zb2Z0LnNoYXJlcG9pbnQuY29tL3RlYW1zL01TT3BlblRlY2gtQ0xBL3Rlc3RzaXRlL1NoYXJlZCBEb2N1bWVudHMvRm9ybXMvQWxsSXRlbXMuYXNweDwvU1A+";

    /**
     * NTLM authentication required cookie.
     */
    public static final String COOKIE_RT_FA = "rtFa=btHSSMLPPaqSjpN1ZN8U91B/V2iaHSc3ubUJtmn8E00D2zONyhkloc9dyL0wIPDbn+OkAsENZfTSseRlQ5eRfV2c53qjNPrHOOf9v63fOcLYYRgAcutqGjhPG3OzBqWI4vFbhT/CmR2bGSEPEuo79oDQRJzwLPgE7Zfiyla0tyYp3riPudFeDYwdedWvlsUsU9wCnR2UQsOPgr/jhDta/qxuJ+0uDdIln+DyX58GSrckMPpi97qkmTvuYAGhtt3CxCZFEXBb5bfIeCMNDh3iTq8xHT54UMZfw4Ea8Ohm29PDAe9ywinLpoLj+6oWYLNkQ6UEGRDTLsdSBTNaky6Q8nMlwgx30SjhX8JH8/unQIvjge/KLPypMVxEDom4CUe9IAAAAA==";

    /**
     * Login for authorization on mails endpoint.
     */
    public static final String USERNAME = "odata1@CTSTest.ccsctp.net";

    /**
     * Password for authorization on mails endpoint.
     */
    public static final String PASSWORD = "07Apples";

    /**
     * Application logging TAG.
     */
    public static final String APP_TAG = "Office365Demo";

    /**
     * Distance for scroll or swipe to make action on mail item
     */
    public static final int SWIPE_THRESHOLD = 50;

    /**
     * Message will not be removed if it swiped smaller than this value
     */
    public static final float SCREEN_PART_FOR_DEFAULT_ACTION = 2f / 3;

    /**
     * Id of list choose request when appropriate dialog is shown
     */
    public static final int CHOOSE_LIST_REQUEST = 0;

    /**
     * Id of remind time request when appropriate dialog is shown
     */
    public static final int CHOOSE_REMIND_TIME_REQUEST = 1;

    /**
     * Distance which view will offset on bounce animation
     */
    public static final int BOUNCE_BASE_OFFSET = 50;

    /**
     * SIM number used as a stub when: <br/>
     * 1. real sim is not present (e.g. on the emulator) <b>AND</b> <br/>
     * 2. {@link Configuration#EMULATE_SIM_PRESENT} is set to <code>true</code>.
     */
    public static final String MOCK_SIM = "12345678910";

    /**
     * Holds enumerations and constants related to application UI
     */
    public static class UI {

        /**
         * Unifies a number of screen references using some grouping criteria. Defines a common set of methods to make group operable.
         */
        public static interface IScreenGroup {
            /**
             * Retrieves members of the group.
             *
             * @return List of group members.
             */
            public EnumSet<Screen> getMembers();

            /**
             * Adds a member to the group
             *
             * @param member Screen to be added to the group.
             */
            public void addMember(Screen member);
        }

        /**
         * Splits all application screens in defined groups based on it's functionality.
         */
        public static enum ScreenGroup implements IScreenGroup {
            MAIL, // 'Box screens' that can contain email messages
            DRAWER, // References to screens that can be accessed via left sliding drawer.
            LISTS, FILES;

            /**
             * List of members of this group. It is initialized statically.
             */
            private List<Screen> members = new LinkedList<Screen>();

            static {
                // Forcing dependent enumeration initiation.
                try {
                    Class.forName(Screen.class.getName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Constants.ScreenGroup.static(): Class 'Screen' not found", e);
                }
            }

            @Override
            public EnumSet<Screen> getMembers() {
                return EnumSet.copyOf(members);
            }

            /**
             * Returns list of member names.
             *
             * @param context Application context.
             *
             * @return list of member names.
             */
            public List<String> getMemberNames(Context context) {
                List<String> names = new ArrayList<String>(members.size());
                for (Screen screen : members) {
                    names.add(screen.getName(context));
                }
                return names;
            }

            @Override
            public void addMember(Screen member) {
                members.add(member);
            }
        }

        /**
         * Enumerates application screens
         */
        public enum Screen {
            LATER   (R.string.screens_mail_later, R.drawable.later, ScreenGroup.MAIL, ScreenGroup.DRAWER),
            MAILBOX (R.string.screens_mailbox, R.drawable.mailbox, ScreenGroup.MAIL, ScreenGroup.DRAWER),
            ARCHIVE (R.string.screens_mail_archive, R.drawable.archive, ScreenGroup.MAIL, ScreenGroup.DRAWER),
            LIST    (R.string.screens_mail_lists, R.drawable.lists, ScreenGroup.MAIL, ScreenGroup.DRAWER),
            TOREAD  (R.string.screens_mail_to_read, R.drawable.empty, ScreenGroup.MAIL, ScreenGroup.DRAWER),
            TRASH   (R.string.screens_mail_trash, R.drawable.trash, ScreenGroup.MAIL, ScreenGroup.DRAWER),
            SENT    (R.string.screens_mail_sent, R.drawable.sent, ScreenGroup.MAIL, ScreenGroup.DRAWER),
            LISTS   (R.string.screens_lists, R.drawable.lists, ScreenGroup.LISTS, ScreenGroup.DRAWER),
            FILES   (R.string.screens_files, R.drawable.files, ScreenGroup.FILES, ScreenGroup.DRAWER),
            SETINGS (R.string.screens_settings, R.drawable.settings, ScreenGroup.DRAWER),
            HELP    (R.string.screens_help, R.drawable.help, ScreenGroup.DRAWER);

            /**
             * Resource id holding the name for this screen.
             */
            private int titleId = -1;

            /**
             * Resource if holding the icon for this screen.
             */
            private int iconId = -1;

            /**
             * Internal constructor that is statically invoked by grouping class.
             *
             * @param screenGroupList Grouping class holding a list of its members.
             */
            private Screen(int titleId, int iconId, IScreenGroup... screenGroupList) {
                this.titleId = titleId;
                this.iconId = iconId;
                for (IScreenGroup group : screenGroupList) {
                    group.addMember(this);
                }
            }

            /**
             * Returns Screen name from resources.
             *
             * @param context Application context.
             *
             * @return Screen name from resources.
             */
            public String getName(Context context) {
                return context.getResources().getString(titleId);
            }

            /**
             * Returns icon from resources for current Screen.
             *
             * @param context Application context.
             *
             * @return Screen icon from resources.
             */
            public Drawable getIcon(Context context) {
                return context.getResources().getDrawable(iconId);
            }

            public int getIconId() {
                return iconId;
            }

            /**
             * Returns screen that has the same name as provided tag.
             *
             * @param tag Name of the screen we're looking for.
             * @param context Application context.
             *
             * @return Screen with specified name or <code>null</code> if tag is <code>null</code> or empty ot context is <code>null</code>
             *         or if no screen is found.
             */
            public static Screen getByTag(String tag, Context context) {
                if(TextUtils.isEmpty(tag) || context == null) return null;

                for (Screen screen : Screen.values()) {
                    if (screen.getName(context).equals(tag)) {
                        return screen;
                    }
                }

                return null;
            }

            /**
             * Tells if this screen belongs to the provided group.
             *
             * @param group Group to check if this dcreen belongs to it.
             *
             * @return <code>true</code> if screen belongs to the group. <code>false</code> otherwise.
             */
            public boolean in(IScreenGroup group) {
                for (Screen screen : group.getMembers()) {
                    if (screen.equals(this)) return true;
                }
                return false;
            }
        }
    }
}
