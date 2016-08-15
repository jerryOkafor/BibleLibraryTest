package com.bellman.bible.android.control.backup;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bellman.bible.android.activity.R;
import com.bellman.bible.android.view.activity.base.Callback;
import com.bellman.bible.android.view.activity.base.Dialogs;
import com.bellman.bible.service.common.FileManager;
import com.bellman.bible.service.db.CommonDatabaseHelper;
import com.bellman.bible.util.SharedConstants;

import java.io.File;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class BackupControl {

    // this is now unused because Embedded Bible databases are held on the SD card to facilitate easier backup by file copy
    private static final File internalDbDir = new File(Environment.getDataDirectory(), "/data/" + SharedConstants.PACKAGE_NAME + "/databases/");

    private static final String TAG = "BackupControl";

    public void updateOptionsMenu(Menu menu) {
        MenuItem restoreMenuItem = menu.findItem(R.id.restore);
        if (restoreMenuItem != null) {
            restoreMenuItem.setEnabled(isBackupFile());
        }
    }

    /**
     * backup database to sd card
     */
    public void backupDatabase() {
        boolean ok = FileManager.copyFile(CommonDatabaseHelper.DATABASE_NAME, internalDbDir, SharedConstants.BACKUP_DIR);

        if (ok) {
            Log.d(TAG, "Copied database to SD card successfully");
            Dialogs.getInstance().showMsg(R.string.backup_success, SharedConstants.BACKUP_DIR.getName());
        } else {
            Log.e(TAG, "Error copying database to SD card");
            Dialogs.getInstance().showErrorMsg(R.string.error_occurred);
        }
    }

    /**
     * restore database from sd card
     */
    public void restoreDatabase() {
        Dialogs.getInstance().showMsg(R.string.restore_confirmation, true, new Callback() {
            @Override
            public void okay() {
                boolean ok = FileManager.copyFile(CommonDatabaseHelper.DATABASE_NAME, SharedConstants.BACKUP_DIR, internalDbDir);

                if (ok) {
                    Log.d(TAG, "Copied database from SD card successfully");
                    Dialogs.getInstance().showMsg(R.string.restore_success, SharedConstants.BACKUP_DIR.getName());
                } else {
                    Log.e(TAG, "Error copying database from SD card");
                    Dialogs.getInstance().showErrorMsg(R.string.error_occurred);
                }
            }
        });
    }

    /**
     * return true if a backup has been done and the file is on the sd card
     */
    private boolean isBackupFile() {
        return new File(SharedConstants.BACKUP_DIR, CommonDatabaseHelper.DATABASE_NAME).exists();
    }
}
