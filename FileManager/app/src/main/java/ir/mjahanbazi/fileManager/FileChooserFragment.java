/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.mjahanbazi.fileManager;

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
