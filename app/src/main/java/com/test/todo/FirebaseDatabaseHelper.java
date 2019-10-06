package com.test.todo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceTasks;
    private List<Task> taskList = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Task> tasks, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }


    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceTasks = mDatabase.getReferenceFromUrl("https://todolist2-c16ea.firebaseio.com/");
    }

    public void readTasks(final DataStatus dataStatus){
        mReferenceTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Task task = keyNode.getValue(Task.class);
                    taskList.add(task);

                }
                dataStatus.DataIsLoaded(taskList, keys);
                Log.d("FDH==>", "Data: "+taskList.get(0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FDH===> ", "Firebase Error" + databaseError);
            }
        });
    }
}
