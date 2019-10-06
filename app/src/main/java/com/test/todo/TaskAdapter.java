package com.test.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter {

    private Context mContext;

    List<Task> tasks;
    public static final String TAG = "TagAdapter";

    public TaskAdapter(List<Task> tasks) { this.tasks = tasks;}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View taskLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taskcard,
                viewGroup, false);
        return new TaskViewHolder(taskLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Task task = tasks.get(i);
        bindData((TaskViewHolder) viewHolder, task);
    }

    private void bindData(TaskViewHolder viewHolder, Task task) {
        viewHolder.taskDesc.setText(task.getTask());
        viewHolder.createdByView.setText(task.getCreatedBy());
        viewHolder.typeTag.setText(task.getType());
        if(task.isStatus() == 0){
            viewHolder.taskStatus.setChecked(false);
        }else{
            viewHolder.taskStatus.setChecked(true);
        }
        viewHolder.taskCard.setTag(task.get_id());
        if(task.getImage() != null) {
            viewHolder.taskImage.setVisibility(View.VISIBLE);
            Picasso.get().load(task.getImage())
                    .centerCrop()
                    .resize(200, 200)
                    .into(viewHolder.taskImage);
        }


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }



}
