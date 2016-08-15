package com.bellman.bible.android.control.speak;

import com.bellman.bible.android.view.activity.base.CurrentActivityHolder;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class NumPagesToSpeakDefinition {
    private int numPages;
    private int resourceId;
    private boolean isPlural;
    private int radioButtonId;

    public NumPagesToSpeakDefinition(int numPages, int resourceId, boolean isPlural, int radioButtonId) {
        super();
        this.numPages = numPages;
        this.resourceId = resourceId;
        this.isPlural = isPlural;
        this.radioButtonId = radioButtonId;
    }

    public String getPrompt() {
        String prompt = null;
        if (isPlural) {
            prompt = CurrentActivityHolder.getInstance().getApplication().getResources().getQuantityString(resourceId, numPages, numPages);
        } else {
            prompt = CurrentActivityHolder.getInstance().getApplication().getResources().getString(resourceId);
        }
        return prompt;
    }

    public int getRadioButtonId() {
        return radioButtonId;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }
}
