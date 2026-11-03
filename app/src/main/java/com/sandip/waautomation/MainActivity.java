package com.sandip.waautomation;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.database.Cursor;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;
import android.graphics.Color;

public class MainActivity extends Activity {
    private static final int PICK_CONTACT = 1001;
    private EditText messageInput, countInput;
    private TextView contactLabel, progressLabel;
    private String phoneNumber = "";
    private int total = 0;
    private int prepared = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(36, 42, 36, 30);
        page.setBackgroundColor(Color.rgb(12, 19, 30));

        TextView title = new TextView(this);
        title.setText("Sandip WA Automation");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setTypeface(null, Typeface.BOLD);
        page.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Prepare messages for WhatsApp with manual send confirmation.");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(14);
        subtitle.setPadding(0, 8, 0, 24);
        page.addView(subtitle);

        Button pickButton = button("Select WhatsApp contact");
        page.addView(pickButton);
        contactLabel = label("No contact selected");
        page.addView(contactLabel);
        pickButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        });

        page.addView(label("Message"));
        messageInput = new EditText(this);
        messageInput.setHint("Write your message...");
        messageInput.setTextColor(Color.WHITE);
        messageInput.setHintTextColor(Color.GRAY);
        messageInput.setMinLines(3);
        messageInput.setGravity(Gravity.TOP);
        messageInput.setBackgroundColor(Color.rgb(30, 40, 55));
        messageInput.setPadding(18, 14, 18, 14);
        page.addView(messageInput);

        page.addView(label("Number of copies"));
        countInput = new EditText(this);
        countInput.setSingleLine(true);
        countInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        countInput.setText("1");
        countInput.setTextColor(Color.WHITE);
        countInput.setBackgroundColor(Color.rgb(30, 40, 55));
        countInput.setPadding(18, 10, 18, 10);
        page.addView(countInput);

        Button startButton = button("Start preparing messages");
        page.addView(startButton);
        progressLabel = label("No messages prepared yet.");
        page.addView(progressLabel);

        Button nextButton = button("Open WhatsApp for next message");
        page.addView(nextButton);
        nextButton.setOnClickListener(v -> openNextMessage());

        TextView note = new TextView(this);
        note.setText("Important: This app does not send messages automatically. Each message opens in WhatsApp and you must review and tap Send yourself. Use it respectfully and stop if the recipient does not want messages.");
        note.setTextColor(Color.rgb(190, 198, 210));
        note.setTextSize(12);
        note.setPadding(0, 24, 0, 0);
        page.addView(note);

        startButton.setOnClickListener(v -> begin());
        setContentView(page);
    }

    private TextView label(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(Color.WHITE);
        t.setTextSize(14);
        t.setPadding(0, 18, 0, 8);
        return t;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(20, 145, 105)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 12;
        b.setLayoutParams(lp);
        return b;
    }

    private void begin() {
        String msg = messageInput.getText().toString().trim();
        String countText = countInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please select a contact first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (msg.isEmpty()) {
            Toast.makeText(this, "Please write a message.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            total = Integer.parseInt(countText);
        } catch (Exception e) {
            total = 0;
        }
        if (total < 1 || total > 20) {
            Toast.makeText(this, "Choose a count between 1 and 20.", Toast.LENGTH_LONG).show();
            return;
        }
        prepared = 0;
        progressLabel.setText("Ready: " + total + " message(s). Open WhatsApp one at a time.");
        Toast.makeText(this, "Message sequence prepared.", Toast.LENGTH_SHORT).show();
    }

    private void openNextMessage() {
        if (total == 0 || prepared >= total) {
            Toast.makeText(this, "Prepare a message sequence first, or all are completed.", Toast.LENGTH_SHORT).show();
            return;
        }
        String msg = messageInput.getText().toString().trim();
        String digits = phoneNumber.replaceAll("[^0-9]", "");
        if (digits.startsWith("0")) digits = digits.substring(1);
        // wa.me expects international country code. Add India's code when a local 10-digit number is selected.
        if (digits.length() == 10) digits = "91" + digits;
        String url = "https://wa.me/" + digits + "?text=" + Uri.encode(msg);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            prepared++;
            progressLabel.setText("Opened " + prepared + " of " + total +
                ". Review the message and tap Send in WhatsApp. Return here for the next one.");
        } catch (Exception e) {
            Toast.makeText(this, "Could not open WhatsApp. Check that it is installed.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                             ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    phoneNumber = cursor.getString(0);
                    String name = cursor.getString(1);
                    contactLabel.setText("Selected: " + name + " (" + phoneNumber + ")");
                }
                cursor.close();
            }
        }
    }
}
