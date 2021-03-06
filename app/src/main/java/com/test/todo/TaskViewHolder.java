package com.test.todo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    public TextView taskDesc;
    public TextView createdByView;
    public ImageView taskImage;
    public TextView typeTag;
    public Switch taskStatus;
    public CardView taskCard;
    public Context mContext;
    public TaskViewHolder(View taskLayout, Context mContext) {
        super(taskLayout);
        taskDesc = taskLayout.findViewById(R.id.taskDesc);
        createdByView = taskLayout.findViewById(R.id.createdByView);
        taskImage = taskLayout.findViewById(R.id.taskImage);
        typeTag = taskLayout.findViewById(R.id.typeTag);
        taskStatus = taskLayout.findViewById(R.id.taskStatus);
        taskCard = taskLayout.findViewById(R.id.taskCard);
        this.mContext = mContext;
    }

    public void bindData(Task task) {
        taskDesc.setText(task.getTask());
        createdByView.setText("Created By: " + task.getCreatedBy());
        typeTag.setText(task.getType());
        if(task.isStatus() == 0){
            taskStatus.setChecked(false);
        }else{
            taskStatus.setChecked(true);
        }
        taskCard.setTag(task.get_id());
        if(task.getImage() != null) {
            taskImage.setVisibility(View.VISIBLE);
            Picasso.get().load(task.getImage())
                    .centerCrop()
                    .resize(200, 200)
                    .into(taskImage);
        }



    }
}