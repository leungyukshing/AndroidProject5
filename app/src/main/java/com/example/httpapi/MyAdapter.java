package com.example.httpapi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<RecylerObj> list;

    public MyAdapter(ArrayList<RecylerObj> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.ViewHolder viewHolder, int i) {
        //System.out.println("In Adapter");
        final RecylerObj recylerObj = list.get(i);
        // 渲染每个Item的页面数据
        viewHolder.play.setText("播放：" + recylerObj.getData().getPlay());
        viewHolder.video_review.setText("评论：" + recylerObj.getData().getVideo_review());
        viewHolder.duration.setText("时长" + recylerObj.getData().getDuration());
        viewHolder.create.setText("创建时间：" + recylerObj.getData().getCreate());
        viewHolder.title.setText(recylerObj.getData().getTitle());
        viewHolder.content.setText(recylerObj.getData().getContent());
        viewHolder.img.setImageBitmap(recylerObj.getData().getBitmap());
        viewHolder.progressBar.setVisibility(((recylerObj.getData().isVisible()) == true) ? View.VISIBLE : View.INVISIBLE);
        if (recylerObj.getPreviewImageArrayList() != null) {
            int size = recylerObj.getPreviewImageArrayList().size();
            viewHolder.seekBar.setMax(recylerObj.getPreviewImageArrayList().get(size - 1).getIndex());
            viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    for (int j = 0; j < recylerObj.getPreviewImageArrayList().size(); j++) {
                        if (i == recylerObj.getPreviewImageArrayList().get(j).getIndex()) {
                            //System.out.println("Index: " + i + ", j: " + j + ", time: " + recylerObj.getPreviewImageArrayList().get(j).getIndex());
                            viewHolder.img.setImageBitmap(recylerObj.getPreviewImageArrayList().get(j).getBitmap());
                            break;
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // 点击前显示cover图片
                    viewHolder.img.setImageBitmap(recylerObj.getData().getBitmap());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // 点击后显示cover图片
                    viewHolder.img.setImageBitmap(recylerObj.getData().getBitmap());
                }
            });
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private ImageView img;
        private TextView play;
        private TextView video_review;
        private TextView duration;
        private TextView create;
        private TextView title;
        private SeekBar seekBar;
        private TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            img = itemView.findViewById(R.id.img);
            play = itemView.findViewById(R.id.play);
            video_review = itemView.findViewById(R.id.video_review);
            duration = itemView.findViewById(R.id.duration);
            create = itemView.findViewById(R.id.create);
            title = itemView.findViewById(R.id.title);
            seekBar = itemView.findViewById(R.id.seekBar);
            content = itemView.findViewById(R.id.content);
        }
    }
}
