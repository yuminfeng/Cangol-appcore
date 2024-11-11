package mobi.cangol.mobile.store;

import android.content.Context;

import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVLogLevel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// mmkv帮助类
public class MMKVHelper {

    private static volatile boolean hasInit = false;
    // mmkv缓存
    private static final Map<String, SharedStore> mmkvCache = new ConcurrentHashMap<>();


    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        if (hasInit) {
            return;
        }
        MMKV.initialize(context, context.getFilesDir().getAbsolutePath() + "mmkv");
        MMKV.setLogLevel(MMKVLogLevel.LevelNone);
        hasInit = true;
    }


    /**
     * 获取mmkv实例
     *
     * @param name
     * @return
     */
    public static SharedStore with(Context context, String name) {
        if (!hasInit) {
            init(context);
        }
        if (mmkvCache.containsKey(name)) {
            return mmkvCache.get(name);
        }
        SharedStore store = new MMKVShareStore(name, MMKV.SINGLE_PROCESS_MODE);
        mmkvCache.put(name, store);
        return store;
    }

    /**
     * 获取mmkv实例
     *
     * @param context
     * @param name
     * @param mode
     * @return
     */
    public static SharedStore with(Context context, String name, int mode) {
        if (!hasInit) {
            init(context);
        }
        if (mmkvCache.containsKey(name)) {
            return mmkvCache.get(name);
        }
        SharedStore store = new MMKVShareStore(name, MMKV.SINGLE_PROCESS_MODE);
        store.migrate(context, name, mode);
        mmkvCache.put(name, store);
        return store;
    }
}
