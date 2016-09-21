package com.bellman.bible.android.common.resource;


import com.bellman.bible.android.Bible;

public class AndroidResourceProvider implements ResourceProvider {

    @Override
    public String getString(int resourceId) {
        return Bible.getInstance().getApplication().getString(resourceId);
    }

}
