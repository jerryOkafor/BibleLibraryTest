package com.bellman.bible.android.view.util.buttongrid;

import com.bellman.bible.android.view.util.buttongrid.ButtonGrid.ButtonInfo;

/**
 * Interface to allow call back when a Grid button is pressed
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public interface OnButtonGridActionListener {
    void buttonPressed(ButtonInfo buttonInfo);
}
