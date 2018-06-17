package nlcsjeju.signin;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by mjkwak on 2/13/2018.
 */

public class Util {
    private static final Util ourInstance = new Util();

    public static Util getInstance() {
        return ourInstance;
    }

    private Util() {
    }

    public void log(String tag, String msg){
        if(BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public void log(String msg){
        if(BuildConfig.DEBUG){
            Log.i("Test", msg);
        }
    }
}
