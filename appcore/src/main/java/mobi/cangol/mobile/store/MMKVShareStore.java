package mobi.cangol.mobile.store;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.tencent.mmkv.MMKV;

import java.util.Map;
import java.util.Set;

import mobi.cangol.mobile.logging.Log;

public class MMKVShareStore implements SharedStore {

    private static final String TAG = "MMKVShareStore";
    // 迁移标志
    private static final String KEY_MIGRATE = "key_migrate";

    private final MMKV mmkvInstance;

    public MMKVShareStore(String id, int mode) {
        mmkvInstance = MMKV.mmkvWithID(id, mode);
    }

    @Override
    public Map<String, ?> getAll() {
        return mmkvInstance.getAll();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return mmkvInstance.getString(key, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return mmkvInstance.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mmkvInstance.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mmkvInstance.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mmkvInstance.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mmkvInstance.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return mmkvInstance.contains(key);
    }

    @Override
    public Editor edit() {
        return mmkvInstance.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mmkvInstance.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mmkvInstance.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void migrate(Context context, String key, int mode) {
        boolean migrate = this.getBoolean(KEY_MIGRATE, false);
        if (migrate) {
            Log.d(TAG, "MMKV has migrate sp.");
            return;
        }
        Log.d(TAG, "MMKV start migrating sp.");
        SharedPreferences sp = context.getSharedPreferences(key, mode);
        mmkvInstance.importFromSharedPreferences(sp);
        sp.edit().clear().apply();
        this.putBoolean(KEY_MIGRATE, true);
        Log.d(TAG, "MMKV migrate finished.");
    }

    @Override
    public Editor putString(String key, @Nullable String value) {
        return mmkvInstance.putString(key, value);
    }

    @Override
    public Editor putStringSet(String key, @Nullable Set<String> values) {
        return mmkvInstance.putStringSet(key, values);
    }

    @Override
    public Editor putInt(String key, int value) {
        return mmkvInstance.putInt(key, value);
    }

    @Override
    public Editor putLong(String key, long value) {
        return mmkvInstance.putLong(key, value);
    }

    @Override
    public Editor putFloat(String key, float value) {
        return mmkvInstance.putFloat(key, value);
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        return mmkvInstance;
    }

    @Override
    public Editor remove(String key) {
        return mmkvInstance.clear();
    }

    @Override
    public Editor clear() {
        return mmkvInstance.clear();
    }

    @Override
    public boolean commit() {
        return mmkvInstance.commit();
    }

    @Override
    public void apply() {
        mmkvInstance.mmapID();
    }
}
