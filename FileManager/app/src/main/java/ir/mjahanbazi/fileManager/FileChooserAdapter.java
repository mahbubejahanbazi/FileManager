/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.mjahanbazi.fileManager;

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
