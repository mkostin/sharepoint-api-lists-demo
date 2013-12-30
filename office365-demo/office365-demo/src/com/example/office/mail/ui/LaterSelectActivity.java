package com.example.office.mail.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.example.office.R;
import com.example.office.mail.actions.RemindLaterMailAction;

public class LaterSelectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_later_select_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_later_select, menu);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        // close an activity on click and return RESULT_OK
        // TODO provide correct list choice logic
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent result = new Intent();
            result.putExtra(RemindLaterMailAction.EMAIL_ID_EXTRA, getIntent().getExtras().getString(RemindLaterMailAction.EMAIL_ID_EXTRA));
            if (isTouchInsideDialog(event)) {
                setResult(RESULT_OK, result);
            } else {
                setResult(RESULT_CANCELED);
            }
            
            finish();
        } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            setResult(RESULT_CANCELED);
            finish();
        }

        return super.onTouchEvent(event);
    }
    
    private boolean isTouchInsideDialog(MotionEvent e) {
        float x = e.getRawX(), y = e.getRawY();
        View dialog = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
                
        int[] position = new int[2];
        dialog.getLocationOnScreen(position);
        
        return x - position[0] >= 0 &&
               x - position[0] < dialog.getWidth() &&
               y - position[1] >= 0 &&
               y - position[1] < dialog.getHeight();
    }
}
