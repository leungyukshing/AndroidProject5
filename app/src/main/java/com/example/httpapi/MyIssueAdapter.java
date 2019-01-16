package com.example.httpapi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyIssueAdapter extends RecyclerView.Adapter<MyIssueAdapter.ViewHolder> {
    private List<Issue> list;

    public MyIssueAdapter(List<Issue> list) {
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
    public MyIssueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.issue_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyIssueAdapter.ViewHolder viewHolder, final int i) {
        System.out.println("In Issue Adapter " + i);
        final Issue issue = list.get(i);
        // 渲染每个Item的页面数据
        viewHolder.issueTitle.setText("Title：" + issue.getTitle());
        viewHolder.createTime.setText("创建时间：" + issue.getCreated_at());
        viewHolder.issueStatus.setText("问题状态：" + issue.getState());
        viewHolder.issueDiscription.setText("问题描述：" + issue.getBody());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView issueTitle;
        private TextView createTime;
        private TextView issueStatus;
        private TextView issueDiscription;

        public ViewHolder(View itemView) {
            super(itemView);
            issueTitle = itemView.findViewById(R.id.issueTitle);
            createTime = itemView.findViewById(R.id.createTime);
            issueStatus = itemView.findViewById(R.id.issueStatus);
            issueDiscription = itemView.findViewById(R.id.issueDiscription);
        }
    }
}
