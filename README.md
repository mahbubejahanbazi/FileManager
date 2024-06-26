# Custom File Manager
A file manager that shows important folders and the internal storage on the home page.

- The user is able to navigate through the folders by clicking
- There is back button to return to the previous page or the home page

## Tech Stack

- Java
- second

<p align="center">Request permission for accessing external storage</p>
<p align="center">
  <img src="https://github.com/peymanjahanbazi/FileManager/images/request_permission.jpg" />
</p>

<p align="center">Accept or deny permission</p>
<p align="center">
  <img src="https://github.com/peymanjahanbazi/FileManager/blob/main/images/grant_permission.jpg" />
</p>

<p align="center">application home page</p>
<p align="center">
  <img src="https://github.com/peymanjahanbazi/FileManager/blob/main/images/home_page.jpg" />
</p>

<p align="center">List of files or folders</p>
<p align="center">
  <img src="https://github.com/peymanjahanbazi/FileManager/blob/main/images/files.jpg" />
</p>

## Source code

#### Dependencies that are used in this application
```java
dependencies {
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.android.material:material:1.3.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'de.hdodenhof:circleimageview:2.2.0'
    implementation group: 'commons-io', name: 'commons-io', version: '2.5'
    implementation group: 'com.nostra13.universalimageloader', name: 'universal-image-loader', version: '1.9.5'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
}
```
FileChooserUtils.java
```java
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
```
#### Use cache for imges of different type of files
ImageCache.java
```java
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.io.File;

import static ir.mjahanbazi.fileManager.FileChooserUtils.activity;

public class ImageCache {

    private static final DisplayImageOptions displayImageOptionsCacheImage = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .delayBeforeLoading(50)
            .showImageForEmptyUri(R.drawable.ic_picture)
            .showImageOnFail(R.drawable.ic_picture)
            .showImageOnLoading(R.drawable.ic_picture)
            .build();
    private static final DisplayImageOptions displayImageOptionsCacheVideo = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .delayBeforeLoading(100)
            .imageScaleType(ImageScaleType.EXACTLY)
            .showImageForEmptyUri(R.drawable.ic_video)
            .showImageOnFail(R.drawable.ic_video)
            .showImageOnLoading(R.drawable.ic_video)
            .build();
    private static final ImageLoaderConfiguration config = new
            ImageLoaderConfiguration.Builder(activity)
            .defaultDisplayImageOptions(displayImageOptionsCacheImage)
            .denyCacheImageMultipleSizesInMemory()
            .diskCacheExtraOptions(200, 200, new BitmapProcessor() {
                public Bitmap process(Bitmap bitmap) {
                    return bitmap;
                }
            })
            .diskCache(new LimitedAgeDiskCache
                    (new File(Environment.getExternalStorageDirectory(), "." + "FileChooserCache"),
                            24 * 60 * 60 * 1000))
            .diskCacheFileCount(2000)
            .diskCacheFileNameGenerator(new Md5FileNameGenerator())
            .tasksProcessingOrder(QueueProcessingType.LIFO)
            .build();
    private static ImageLoader imageLoader;

    public synchronized static ImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
        }
        return imageLoader;
    }


    public static void image(File file, ImageView imageView) {
        getImageLoader().displayImage("file://" + file.getAbsolutePath(), imageView, displayImageOptionsCacheImage);
    }

    public static void video(File file, ImageView imageView) {
        getImageLoader().displayImage("file://" + file.getAbsolutePath(), imageView, displayImageOptionsCacheVideo);
    }

}
```

#### Check for permission for accessing external storage
MainActivity.java
```java
  public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileChooserUtils.activity = MainActivity.this;
        FileChooserUtils.root = findViewById(R.id.activity_main_root);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (hasPermission()) {
            ft.replace(R.id.your_placeholder, new FileChooserFragment());
            ft.commit();
        } else {
            ft.replace(R.id.your_placeholder, new RequestPermissionFragment());
            ft.commit();

        }
    }

    private boolean hasPermission() {
        boolean has = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                has = false;
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                has = false;
            }
        }
        return has;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE_SOME_FEATURES_PERMISSIONS == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                int grantResult = grantResults[i];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.your_placeholder, new FileChooserFragment());
                    ft.commit();
                }
            }
        }
    }

}
```
#### Request permission for accessing external storage
RequestPermissionFragment.java
```java
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.List;

public class RequestPermissionFragment extends Fragment {
    private Context context;
    private Button requestPermission;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            requestPermission();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.request_permission, container, false);
        context = getActivity();
        requestPermission = rootView.findViewById(R.id.request_permission_request_permission);
        requestPermission.setOnClickListener(onClickListener);
        return rootView;
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<String>();
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!permissions.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(),permissions.toArray(new String[permissions.size()]),
                        MainActivity.REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
            }
        }
    }
}
```
FileChooserFragment.java
```java
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ir.mjahanbazi.fileManager.FileChooserUtils.DIRECTORY_DCIM;
import static ir.mjahanbazi.fileManager.FileChooserUtils.DIRECTORY_DOCUMENTS;
import static ir.mjahanbazi.fileManager.FileChooserUtils.DIRECTORY_DOWNLOADS;
import static ir.mjahanbazi.fileManager.FileChooserUtils.DIRECTORY_MOVIES;
import static ir.mjahanbazi.fileManager.FileChooserUtils.DIRECTORY_MUSIC;
import static ir.mjahanbazi.fileManager.FileChooserUtils.DIRECTORY_PICTURES;
import static ir.mjahanbazi.fileManager.FileChooserUtils.ExternalStorage;
import static ir.mjahanbazi.fileManager.FileChooserUtils.activity;

public class FileChooserFragment extends Fragment {
    public File root;
    private ListView list;
    private File rootFolder;
    private FileChooserAdapter adapter;
    private TextView filePath;
    private ViewSwitcher switcher;
    private ImageView camera;
    private ImageView documents;
    private ImageView downloads;
    private ImageView movies;
    private ImageView musics;
    private ImageView pictures;
    private ImageView internalStorage;
    private TextView caption;
    private Context context;
    private ImageButton up;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setMyAdaptor(view.getId());
        }
    };

    public FileChooserFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.file_manager, container, false);
        context = getActivity();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootFolder = Environment.getExternalStorageDirectory();
        up = view.findViewById(R.id.file_manager_up);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up();
            }
        });
        filePath = (TextView) view.findViewById(R.id.file_manager_filePath);
        list = (ListView) view.findViewById(R.id.file_manager_grid);
        if (Environment.getExternalStorageDirectory() == null) {
            Toast.makeText(context, "there is no files", Toast.LENGTH_SHORT).show();
        }
        adapter = new FileChooserAdapter(new ArrayList<File>(), this);
        list.setAdapter(adapter);
        switcher = (ViewSwitcher) view.findViewById(R.id.file_manager_switcher);
        camera = (ImageView) view.findViewById(R.id.file_manager_camera_icon);
        documents = (ImageView) view.findViewById(R.id.file_manager_document_icon);
        downloads = (ImageView) view.findViewById(R.id.file_manager_download_icon);
        movies = (ImageView) view.findViewById(R.id.file_manager_movie_icon);
        musics = (ImageView) view.findViewById(R.id.file_manager_music_icon);
        pictures = (ImageView) view.findViewById(R.id.file_manager_pictures_icon);
        internalStorage = (ImageView) view.findViewById(R.id.file_manager_internal_storage_icon);
        TextView camera_txt = (TextView) view.findViewById(R.id.file_manager_camera_txt);
        TextView documents_txt = (TextView) view.findViewById(R.id.file_manager_document_txt);
        TextView downloads_txt = (TextView) view.findViewById(R.id.file_manager_download_txt);
        TextView movies_txt = (TextView) view.findViewById(R.id.file_manager_movie_txt);
        TextView musics_txt = (TextView) view.findViewById(R.id.file_manager_music_txt);
        TextView pictures_txt = (TextView) view.findViewById(R.id.file_manager_pictures_txt);
        TextView internalStorage_txt = (TextView) view.findViewById(R.id.file_manager_internal_storage_txt);
        caption = (TextView) view.findViewById(R.id.file_manager_caption);
        camera.setOnClickListener(onClickListener);
        documents.setOnClickListener(onClickListener);
        downloads.setOnClickListener(onClickListener);
        movies.setOnClickListener(onClickListener);
        musics.setOnClickListener(onClickListener);
        pictures.setOnClickListener(onClickListener);
        internalStorage.setOnClickListener(onClickListener);
        if (!DIRECTORY_DCIM.isDirectory()) {
            camera_txt.setTextColor(activity.getResources().getColor(R.color.gray_filechooser));
        }
        if (DIRECTORY_DOCUMENTS == null || !DIRECTORY_DOCUMENTS.isDirectory()) {
            documents_txt.setTextColor(activity.getResources().getColor(R.color.gray_filechooser));
        }
        if (!DIRECTORY_DOWNLOADS.isDirectory()) {
            downloads_txt.setTextColor(activity.getResources().getColor(R.color.gray_filechooser));
        }
        if (!DIRECTORY_MOVIES.isDirectory()) {
            movies_txt.setTextColor(activity.getResources().getColor(R.color.gray_filechooser));
        }
        if (!DIRECTORY_MUSIC.isDirectory()) {
            musics_txt.setTextColor(activity.getResources().getColor(R.color.gray_filechooser));
        }
        if (!DIRECTORY_PICTURES.isDirectory()) {
            pictures_txt.setTextColor(activity.getResources().getColor(R.color.gray_filechooser));
        }
        if (!ExternalStorage.isDirectory()) {
            internalStorage_txt.setTextColor(activity.getResources().getColor(R.color.gray_filechooser));
        }
    }


    private void up() {
        List<File> list = adapter.getList();
        list.clear();
        List<File> newFiles;
        newFiles = new ArrayList<File>();
        if (root == null) {
            activity.finishAffinity();
            return;
        } else if (root != null && rootFolder.getAbsolutePath().equals(root.getAbsolutePath())) {
            switcher.showNext();
            caption.setVisibility(VISIBLE);
            updatepath(null);
            root = null;
        } else {
            rootFolder = rootFolder.getParentFile();
            newFiles = getFileList();
            updatepath(rootFolder);
        }
        list.addAll(newFiles);
        adapter.notifyDataSetChanged();
    }

    public void ItemClicked(File file, View view, int position) {
        if (file.isDirectory()) {
            if (root == null) {
                root = file;
            }
            List<File> list = adapter.getList();
            list.clear();
            rootFolder = file;
            list.addAll(getFileList());
            adapter.notifyDataSetChanged();
            updatepath(file);
        }
    }

    public void updatepath(File file) {
        if (file == null) {
            filePath.setText("");
            return;
        }
        filePath.setText(file.getAbsolutePath());
    }

    private List<File> getFileList() {
        File[] fileList = rootFolder.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        });

        if (fileList == null) {
            return Arrays.asList(new File[0]);
        }
        Arrays.sort(fileList, new Comparator<File>() {
            public int compare(File left, File right) {
                if (left.isDirectory() && right.isFile()) {
                    return -1;
                }
                if (right.isDirectory() && left.isFile()) {
                    return 1;
                }
                return left.getName().compareTo(right.getName());
            }
        });
        return Arrays.asList(fileList);
    }


    private void setMyAdaptor(int viewId) {
        caption.setVisibility(GONE);
        File[] files = null;
        File file = null;
        ArrayList<File> result = new ArrayList<File>();
        if (viewId == R.id.file_manager_camera_icon) {
            if (DIRECTORY_DCIM.isDirectory()) {
                files = DIRECTORY_DCIM.listFiles();
                file = DIRECTORY_DCIM;
            } else {
                camera.setEnabled(false);
            }
        } else if (viewId == R.id.file_manager_document_icon) {
            if (DIRECTORY_DOCUMENTS != null && DIRECTORY_DOCUMENTS.isDirectory()) {
                files = DIRECTORY_DOCUMENTS.listFiles();
                file = DIRECTORY_DOCUMENTS;
            } else {
                documents.setEnabled(false);
            }
        } else if (viewId == R.id.file_manager_download_icon) {
            if (DIRECTORY_DOWNLOADS.isDirectory()) {
                files = DIRECTORY_DOWNLOADS.listFiles();
                file = DIRECTORY_DOWNLOADS;
            } else {
                downloads.setEnabled(false);
            }
        } else if (viewId == R.id.file_manager_movie_icon) {
            if (DIRECTORY_MOVIES.isDirectory()) {
                files = DIRECTORY_MOVIES.listFiles();
                file = DIRECTORY_MOVIES;
            } else {
                movies.setEnabled(false);

            }
        } else if (viewId == R.id.file_manager_music_icon) {
            if (DIRECTORY_MUSIC.isDirectory()) {
                files = DIRECTORY_MUSIC.listFiles();
                file = DIRECTORY_MUSIC;
            } else {
                musics.setEnabled(false);
            }
        } else if (viewId == R.id.file_manager_pictures_icon) {
            if (DIRECTORY_PICTURES.isDirectory()) {
                files = DIRECTORY_PICTURES.listFiles();
                file = DIRECTORY_PICTURES;
            } else {
                pictures.setEnabled(false);
            }
        } else if (viewId == R.id.file_manager_internal_storage_icon) {
            if (ExternalStorage.isDirectory()) {
                files = ExternalStorage.listFiles();
                file = ExternalStorage;
            } else {
                internalStorage.setEnabled(false);
            }
        }
        if (files != null) {
            result.addAll(Arrays.asList(files));
            List<File> list = adapter.getList();
            list.clear();
            rootFolder = file;
            root = file;
            list.addAll(result);
            adapter.notifyDataSetChanged();
            updatepath(file);
            switcher.showNext();
        }

    }
}
```
MyListAdapter.java
```java
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MyListAdapter<T> extends BaseAdapter {

    protected final List<T> list;

    public MyListAdapter() {
        this(new ArrayList<T>());
    }

    public MyListAdapter(List<T> list) {
        this.list = list;
    }

    public int getCount() {
        return list.size();
    }

    public T getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public List<T> getList() {
        return list;
    }

    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void add(int location, T object) {
        list.add(location, object);
        notifyDataSetChanged();
    }

    public void add(T object) {
        list.add(object);
        notifyDataSetChanged();
    }

    public void addAll(int location, Collection<T> collection) {
        list.addAll(location, collection);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> collection) {
        list.addAll(collection);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void remove(int location) {
        list.remove(location);
        notifyDataSetChanged();
    }

    public void remove(T object) {
        list.remove(object);
        notifyDataSetChanged();
    }

    public void removeAll(Collection<T> collection) {
        list.removeAll(collection);
        notifyDataSetChanged();
    }

}
```
FileChooserAdapter.java
```java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.List;


import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static ir.mjahanbazi.fileManager.FileChooserUtils.activity;
import static ir.mjahanbazi.fileManager.FileChooserUtils.dpi2Pixel;

public class FileChooserAdapter extends MyListAdapter<File> {

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            TagData tag = (TagData) v.getTag();
            final FileChooserAdapter adapter = tag.adapter;
            File file = tag.file;
            if (v.getId() != R.id.file_manger_item_root) {
                adapter.fm.ItemClicked(file, tag.view, tag.position);
            } else {

            }
        }
    };
    private final FileChooserFragment fm;

    public FileChooserAdapter(List<File> files, FileChooserFragment fm) {
        super(files);
        this.fm = fm;
    }

    class TagData {
        ImageView fileImage;
        TextView fileName;
        File file;
        FileChooserAdapter adapter;
        int position;
        View view;
    }

    private static String[] extensions = {"jpg", "jpeg", "bmp", "png"};

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        File file = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_manager_item, parent, false);
            convertView.setOnClickListener(onClickListener);
            final ImageView fileImageXml = (ImageView) convertView.findViewById(R.id.file_manager_item_icon);
            fileImageXml.setOnClickListener(onClickListener);
            final TextView fileNameXml = (TextView) convertView.findViewById(R.id.file_manager_item_name);
            fileNameXml.setOnClickListener(onClickListener);
            TagData tag = new TagData() {
                {
                    fileImage = fileImageXml;
                    fileName = fileNameXml;
                    adapter = FileChooserAdapter.this;
                }
            };
            tag.fileImage.setTag(tag);
            tag.fileName.setTag(tag);
            convertView.setTag(tag);
        }
        TagData tag = (TagData) convertView.getTag();
        if (file.isFile()) {
            if (FilenameUtils.isExtension(file.getName().toLowerCase(), extensions)) {
                ImageCache.image(file, tag.fileImage);
                tag.fileImage.setBackground(activity.getResources().getDrawable(R.drawable.bg_default));
                tag.fileImage.setScaleType(CENTER_CROP);
                tag.fileImage.setPadding(0, 0, 0, 0);
            } else if (FilenameUtils.isExtension(file.getName().toLowerCase(), new String[]{"mp3"})) {
                tag.fileImage.setImageResource(R.drawable.ic_music);
                tag.fileImage.setBackground(activity.getResources().getDrawable(R.drawable.bg_default_icon_music));
                tag.fileImage.setPadding(dpi2Pixel(7), dpi2Pixel(7), dpi2Pixel(7), dpi2Pixel(7));
            } else if (FilenameUtils.isExtension(file.getName().toLowerCase(), new String[]{"mp4"})) {
                ImageCache.video(file, tag.fileImage);
                tag.fileImage.setBackground(activity.getResources().getDrawable(R.drawable.bg_default));
                tag.fileImage.setScaleType(CENTER_CROP);
                tag.fileImage.setPadding(0, 0, 0, 0);
            } else {
                tag.fileImage.setImageResource(R.drawable.ic_filechooser_file_icon_default);
                tag.fileImage.setBackground(activity.getResources().getDrawable(R.drawable.bg_default_icon_file));
                tag.fileImage.setPadding(dpi2Pixel(7), dpi2Pixel(7), dpi2Pixel(7), dpi2Pixel(7));
            }
        } else if (file.isDirectory()) {
            tag.fileImage.setImageResource(R.drawable.ic_folder);
            tag.fileImage.setBackground(activity.getResources().getDrawable(R.drawable.bg_default_icon_folder));
            tag.fileImage.setPadding(dpi2Pixel(7), dpi2Pixel(7), dpi2Pixel(7), dpi2Pixel(7));
        }
        tag.fileName.setText(file.getName());
        tag.file = file;
        tag.position = position;
        tag.view = convertView;
        return convertView;
    }

}
```
activity_main.xml
```xml 
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/activity_main_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

    <FrameLayout
        android:id="@+id/your_placeholder"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></FrameLayout>

</RelativeLayout>
```
file_manager.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/file_manager_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <View
        android:id="@+id/file_manager_toolbar"
        android:layout_width="match_parent"
        android:layout_height="@dimen/toolbar_height"
        android:layout_alignParentTop="true"
        android:background="@color/toolbar_color" />

    <ImageButton
        android:id="@+id/file_manager_up"
        android:layout_width="@dimen/toolbar_height"
        android:layout_height="@dimen/toolbar_height"
        android:layout_alignParentStart="true"
        android:layout_alignParentTop="true"
        android:background="@color/toolbar_color"
        android:scaleType="fitCenter"
        android:src="@drawable/ic_back" />

    <TextView
        android:id="@+id/file_manager_caption"
        android:layout_width="wrap_content"
        android:layout_height="@dimen/toolbar_height"
        android:layout_toEndOf="@+id/file_manager_up"
        android:background="@color/toolbar_color"
        android:paddingStart="@dimen/padding_left"
        android:paddingTop="@dimen/padding_top"
        android:text="@string/filechooser_caption"
        android:textColor="@color/white"
        android:textSize="@dimen/caption_text_size" />

    <TextView
        android:id="@+id/file_manager_filePath"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:layout_alignParentTop="true"
        android:layout_toEndOf="@+id/file_manager_up"
        android:paddingStart="5dp"
        android:paddingTop="5dp"
        android:paddingEnd="5dp"
        android:textColor="@color/white" />

    <ViewSwitcher
        android:id="@+id/file_manager_switcher"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@+id/file_manager_toolbar"
        android:background="@color/white">

        <ScrollView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content">

            <TableLayout
                android:id="@+id/file_manager_child1"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:paddingTop="10dp"
                android:paddingBottom="10dp"
                android:shrinkColumns="*"
                android:stretchColumns="*">

                <TableRow
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="20dp">

                    <RelativeLayout android:id="@+id/file_manager_camera">

                        <ImageView
                            android:id="@+id/file_manager_camera_icon"
                            android:layout_width="@dimen/filechosser_image_height"
                            android:layout_height="@dimen/filechosser_image_height"
                            android:layout_alignParentTop="true"
                            android:layout_centerHorizontal="true"
                            android:background="@drawable/bg_file_chooser_camera"
                            android:padding="@dimen/file_chooser_image_padding"
                            android:scaleType="fitCenter"
                            android:src="@drawable/ic_file_chooser_camera" />

                        <TextView
                            android:id="@+id/file_manager_camera_txt"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_below="@+id/file_manager_camera_icon"
                            android:layout_centerHorizontal="true"
                            android:text="@string/filechooser_camera"
                            android:textColor="@color/black" />
                    </RelativeLayout>

                    <RelativeLayout android:id="@+id/file_manager_document">

                        <ImageView
                            android:id="@+id/file_manager_document_icon"
                            android:layout_width="@dimen/filechosser_image_height"
                            android:layout_height="@dimen/filechosser_image_height"
                            android:layout_alignParentTop="true"
                            android:layout_centerHorizontal="true"
                            android:background="@drawable/bg_file_chooser_document"
                            android:padding="@dimen/file_chooser_image_padding"
                            android:scaleType="fitCenter"
                            android:src="@drawable/ic_file_chooser_document" />

                        <TextView
                            android:id="@+id/file_manager_document_txt"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_below="@+id/file_manager_document_icon"
                            android:layout_centerHorizontal="true"
                            android:text="@string/filechooser_documents"
                            android:textColor="@color/black" />
                    </RelativeLayout>
                </TableRow>

                <TableRow
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:paddingTop="10dp">

                    <RelativeLayout android:id="@+id/file_manager_download">

                        <ImageView
                            android:id="@+id/file_manager_download_icon"
                            android:layout_width="@dimen/filechosser_image_height"
                            android:layout_height="@dimen/filechosser_image_height"
                            android:layout_alignParentTop="true"
                            android:layout_centerHorizontal="true"
                            android:background="@drawable/bg_file_chooser_downloads"
                            android:padding="@dimen/file_chooser_image_padding"
                            android:scaleType="fitCenter"
                            android:src="@drawable/ic_file_chooser_downloads" />

                        <TextView
                            android:id="@+id/file_manager_download_txt"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_below="@+id/file_manager_download_icon"
                            android:layout_centerHorizontal="true"
                            android:text="@string/filechooser_downloads"
                            android:textColor="@color/black" />
                    </RelativeLayout>

                    <RelativeLayout android:id="@+id/file_manager_movie">

                        <ImageView
                            android:id="@+id/file_manager_movie_icon"
                            android:layout_width="@dimen/filechosser_image_height"
                            android:layout_height="@dimen/filechosser_image_height"
                            android:layout_alignParentTop="true"
                            android:layout_centerHorizontal="true"
                            android:background="@drawable/bg_file_chooser_movies"
                            android:padding="@dimen/file_chooser_image_padding"
                            android:scaleType="fitCenter"
                            android:src="@drawable/ic_file_chooser_movies" />

                        <TextView
                            android:id="@+id/file_manager_movie_txt"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_below="@+id/file_manager_movie_icon"
                            android:layout_centerHorizontal="true"
                            android:text="@string/filechooser_movies"
                            android:textColor="@color/black" />
                    </RelativeLayout>
                </TableRow>

                <TableRow
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:paddingTop="10dp">

                    <RelativeLayout android:id="@+id/file_manager_music">

                        <ImageView
                            android:id="@+id/file_manager_music_icon"
                            android:layout_width="@dimen/filechosser_image_height"
                            android:layout_height="@dimen/filechosser_image_height"
                            android:layout_alignParentTop="true"
                            android:layout_centerHorizontal="true"
                            android:background="@drawable/bg_file_chooser_musics"
                            android:padding="@dimen/file_chooser_image_padding"
                            android:scaleType="fitCenter"
                            android:src="@drawable/ic_file_chooser_musics" />

                        <TextView
                            android:id="@+id/file_manager_music_txt"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_below="@+id/file_manager_music_icon"
                            android:layout_centerHorizontal="true"
                            android:text="@string/filechooser_musics"
                            android:textColor="@color/black" />
                    </RelativeLayout>

                    <RelativeLayout android:id="@+id/file_manager_pictures">

                        <ImageView
                            android:id="@+id/file_manager_pictures_icon"
                            android:layout_width="@dimen/filechosser_image_height"
                            android:layout_height="@dimen/filechosser_image_height"
                            android:layout_alignParentTop="true"
                            android:layout_centerHorizontal="true"
                            android:background="@drawable/bg_file_chooser_pictures"
                            android:padding="@dimen/file_chooser_image_padding"
                            android:scaleType="fitCenter"
                            android:src="@drawable/ic_file_chooser_pictures" />

                        <TextView
                            android:id="@+id/file_manager_pictures_txt"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_below="@+id/file_manager_pictures_icon"
                            android:layout_centerHorizontal="true"
                            android:text="@string/filechooser_pictures"
                            android:textColor="@color/black" />
                    </RelativeLayout>
                </TableRow>

                <TableRow
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:paddingTop="20dp">

                    <RelativeLayout
                        android:id="@+id/file_manager_internal_storage"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:layout_weight="1">

                        <ImageView
                            android:id="@+id/file_manager_internal_storage_icon"
                            android:layout_width="90dp"
                            android:layout_height="90dp"
                            android:layout_alignParentTop="true"
                            android:layout_centerHorizontal="true"
                            android:background="@drawable/bg_file_chooser_internal_storage"
                            android:padding="@dimen/file_chooser_image_padding"
                            android:scaleType="fitCenter"
                            android:src="@drawable/ic_file_chooser_internal_storage" />

                        <TextView
                            android:id="@+id/file_manager_internal_storage_txt"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_below="@+id/file_manager_internal_storage_icon"
                            android:layout_centerHorizontal="true"
                            android:text="@string/filechooser_internal_storage"
                            android:textColor="@color/black" />
                    </RelativeLayout>
                </TableRow>
            </TableLayout>
        </ScrollView>

        <RelativeLayout
            android:id="@+id/file_manager_child2"
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <ListView
                android:id="@+id/file_manager_grid"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@color/white"
                android:divider="@null"
                android:dividerHeight="0dp" />

        </RelativeLayout>
    </ViewSwitcher>
</RelativeLayout>
```
file_manager_item.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/file_manger_item_root"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <de.hdodenhof.circleimageview.CircleImageView
        android:id="@+id/file_manager_item_icon"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_alignParentStart="true"
        android:layout_alignParentTop="true"
        android:layout_centerHorizontal="true"
        android:layout_marginStart="15dp"
        android:layout_marginTop="10dp" />

    <TextView
        android:id="@+id/file_manager_item_name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerVertical="true"
        android:layout_marginStart="10dp"
        android:layout_marginEnd="10dp"
        android:layout_toEndOf="@+id/file_manager_item_icon"
        android:textColor="#57575E"
        android:ellipsize="end"
        android:maxLines="1"
        android:textAlignment="viewStart" />

    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_alignParentBottom="true"
        android:layout_marginStart="60dp"
        android:layout_marginEnd="10dp"
        android:layout_marginTop="10dp"
        android:layout_marginBottom="10dp"
        android:background="#F6F4F4" />
</RelativeLayout>
```
request_permission.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <Button
        android:id="@+id/request_permission_request_permission"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_button"
        android:padding="20dp"
        android:text="Srequest permission"
        android:textColor="@color/white"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```
## Contact

mjahanbazi@protonmail.com
