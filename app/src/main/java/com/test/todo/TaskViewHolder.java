package com.test.todo;

import android.content.Context;
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

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    public TextView taskDesc;
    public TextView createdByView;
    public ImageView taskImage;
    public TextView typeTag;
    public Switch taskStatus;
    public CardView taskCard;
    public String con;
    public String eMail;

    public TaskViewHolder(View taskLayout) {
        super(taskLayout);
        taskDesc = taskLayout.findViewById(R.id.taskDesc);
        createdByView = taskLayout.findViewById(R.id.createdByView);
        taskImage = taskLayout.findViewById(R.id.taskImage);
        typeTag = taskLayout.findViewById(R.id.typeTag);
        taskStatus = taskLayout.findViewById(R.id.taskStatus);
        taskCard = taskLayout.findViewById(R.id.taskCard);
        taskStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Email from = new Email("digitaltakeoffmailer@gmail.com");
                Email to = new Email(eMail);
                Mail mail;

                if(b){
                    Content content = new Content("text/plain", con+"done");
                    mail = new Mail(from, "Task Done Update Notification",to, content);
                }else{
                    Content content = new Content("text/plain", con+"pending");
                    mail = new Mail(from, "Task Pending Update Notification",to, content);
                }
                SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
                Request request = new Request();
                try {
                    request.setMethod(Method.POST);
                    request.setEndpoint("mail/send");
                    request.setBody(mail.build());
                    Response response = sg.api(request);
                    System.out.println(response.getStatusCode());
                    System.out.println(response.getBody());
                    System.out.println(response.getHeaders());
                } catch (IOException ex) {
                    //Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, ex.getMessage());
                }
            }
        });
    }

    public void bindData(Task task) {
        taskDesc.setText(task.getTask());
        createdByView.setText("Created By: " + task.getCreatedBy());
        typeTag.setText(task.getType());
        con = "The Task id: "+task.get_id()+" which was created by "+ task.getCreatedBy() +" On date - " + task.getDateCreated()+" is ";
        eMail = task.getUseremail();
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