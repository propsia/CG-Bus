package com.nadeem.cgcitybus;

import android.content.Intent;
import android.os.Bundle;

public class SendEmail extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CG City BUS: Hey there Developer!");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ahmed.nadeem016@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Email Client Chooser"));
        finish();
    }
    }

