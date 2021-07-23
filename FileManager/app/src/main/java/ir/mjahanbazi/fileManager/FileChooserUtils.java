package ir.mjahanbazi.fileManager;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.widget.RelativeLayout;

import java.io.File;

public class FileChooserUtils {
    public static final File DIRECTORY_DCIM;
    public static final File DIRECTORY_DOCUMENTS;
    public static final File DIRECTORY_DOWNLOADS;
    public static final File DIRECTORY_MOVIES;
    public static final File DIRECTORY_MUSIC;
    public static final File DIRECTORY_PICTURES;
    public static final File ExternalStorage;
    public static Activity activity;
    public static RelativeLayout root;
    private static float density = -1;

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            DIRECTORY_DOCUMENTS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        } else {
            DIRECTORY_DOCUMENTS = null;
        }
        DIRECTORY_DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        DIRECTORY_DOWNLOADS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        DIRECTORY_MOVIES = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        DIRECTORY_MUSIC = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        DIRECTORY_PICTURES = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        ExternalStorage = Environment.getExternalStorageDirectory();
    }

    public static int dpi2Pixel(int dpi) {
        return (int) (dpi * getDensity());
    }

    private static float getDensity() {
        if (density == -1) {
            density = FileChooserUtils.activity.getResources().getDisplayMetrics().density;
        }
        return density;
    }
}
