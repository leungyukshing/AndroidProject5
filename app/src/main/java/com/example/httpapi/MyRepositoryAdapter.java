package com.example.httpapi;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyRepositoryAdapter extends RecyclerView.Adapter<MyRepositoryAdapter.ViewHolder> {
    private List<Repository> list;

    public MyRepositoryAdapter(List<Repository> list) {
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
    public MyRepositoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.repo_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRepositoryAdapter.ViewHolder viewHolder, final int i) {
        System.out.println("In Repository Adapter");
        final Repository repository = list.get(i);
        // 渲染每个Item的页面数据
        viewHolder.repoTitle.setText("项目名：" + repository.getName());
        viewHolder.repoID.setText("项目id：" + repository.getId() + "");
        viewHolder.repoDiscription.setText("项目描述：" + repository.getDescription());
        if (repository.getHas_issues()) {
            viewHolder.problem.setText("存在问题：" + repository.getOpen_issues()+"");
        }
        else {
            viewHolder.problem.setText("存在问题：0");
        }

        // 点击事件
        viewHolder.itemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(i);
            }
        });
    }

    public interface onItemClickListener {
        void onItemClick(int i);
    }
    private onItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView itemCardView;
        private TextView repoTitle;
        private TextView repoID;
        private TextView problem;
        private TextView repoDiscription;

        public ViewHolder(View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            repoTitle = itemView.findViewById(R.id.repoTitle);
            repoID = itemView.findViewById(R.id.repoID);
            problem = itemView.findViewById(R.id.problem);
            repoDiscription = itemView.findViewById(R.id.repoDiscription);
        }
    }
}