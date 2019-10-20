package spaceapps.team42.nasadata;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application {

    private static App instance;
    private static Context context;

    public App() {
        if (context == null)
            context = this;
    }

    private static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public static Context getContext() {
        if (context == null)
            getInstance();
        return context;
    }

    public static synchronized void saveState(String id, boolean bool) {
        SharedPreferences preferences = getContext().getSharedPreferences("TEAM42", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(id, bool);
        editor.apply();
    }

    public static synchronized boolean getState(String id) {
        SharedPreferences preferences = getContext().getSharedPreferences("TEAM42", MODE_PRIVATE);
        return preferences.getBoolean(id, false);
    }

}
