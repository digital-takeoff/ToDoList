package com.test.todo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TaskAdapter taskAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView taskRecView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskRecView = findViewById(R.id.taskRecView);
        linearLayoutManager = new LinearLayoutManager(this);

        new FirebaseDatabaseHelper().readTasks(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Task> tasks, List<String> keys) {
                Log.d("TAG==>", "Hello World");
                taskAdapter = new TaskAdapter(tasks);
                taskRecView.setAdapter(taskAdapter);
                taskRecView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

    }
}
