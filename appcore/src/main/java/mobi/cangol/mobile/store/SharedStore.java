package mobi.cangol.mobile.store;

import android.content.Context;
import android.content.SharedPreferences;

public interface SharedStore extends SharedPreferences, SharedPreferences.Editor {
    // 迁移sp
    void migrate(Context context, String key, int mode);
}
