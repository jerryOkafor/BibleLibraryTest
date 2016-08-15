package com.bellman.bible.android.control.email;

public interface Emailer {

    void send(String emailDialogTitle, String subject, String text);

    void send(String emailDialogTitle, String recipient, String subject, String text);
}