package com.example.httpapi;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class RecylerObj {
    private boolean status;
    private Data data;
    private ArrayList<PreviewImage> previewImageArrayList;

    public static class PreviewImage {
        private Bitmap bitmap;
        private int index;
        PreviewImage() {
            bitmap = null;
            index = -1;
        }
        PreviewImage(Bitmap bitmap, int index) {
            this.bitmap = bitmap;
            this.index = index;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public void setPreviewImageArrayList(ArrayList<PreviewImage> previewImageArrayList) { this.previewImageArrayList = previewImageArrayList; }

    public ArrayList<PreviewImage> getPreviewImageArrayList() { return previewImageArrayList; }

    public Data getData() { return data; }

    public boolean isStatus() { return status; }

    public static class Data {
        private int aid;
        private int state;
        private String cover;
        private String title;
        private String content;
        private int play;
        private String duration;
        private int video_review;
        private String create;
        private String rec;
        private int count;
        private Bitmap bitmap;
        private boolean visible; // 表示进度条是否显示的变量


        public int getAid() { return aid; }

        public int getState() { return state; }

        public String getCover() { return cover; }

        public String getTitle() { return title; }

        public int getPlay() { return play; }

        public int getVideo_review() { return video_review; }

        public String getDuration() { return duration; }

        public String getCreate() { return create; }

        public String getContent() { return content; }

        public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

        public Bitmap getBitmap() { return bitmap; }

        public boolean isVisible() { return visible; }

        public void setVisible(boolean visible) { this.visible = visible; }
    }
}
