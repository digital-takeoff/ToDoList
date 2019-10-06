package com.test.todo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    final Calendar filterCal = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener filterDate;
    Context mContext;
    ImageButton dateFilter;

    FirebaseRecyclerPagingAdapter<Task, TaskViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        //Initialize RecyclerView
        mRecyclerView = findViewById(R.id.taskRecView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todolist2-c16ea.firebaseio.com/");

        //Initialize PagedList Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        //Initialize FirebasePagingOptions
        DatabasePagingOptions<Task> options = new DatabasePagingOptions.Builder<Task>()
                .setLifecycleOwner(this)
                .setQuery(mDatabase, config, Task.class)
                .build();



        //Initialize Adapter
        mAdapter = new FirebaseRecyclerPagingAdapter<Task, TaskViewHolder>(options) {
            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TaskViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.taskcard, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder,
                                            int position,
                                            @NonNull Task task) {
                holder.bindData(task);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        Log.d("TAG", "LOADING MORE");
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        Log.d("TAG", "LOADED");
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        Log.d("TAG", "LOADING FINISHED");
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        retry();
                        break;
                }
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                mSwipeRefreshLayout.setRefreshing(false);
                databaseError.toException().printStackTrace();
            }
        };

        //Set Adapter to RecyclerView
        Log.d("TAG==>", "Setting Adapter");
        mRecyclerView.setAdapter(mAdapter);
        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.refresh();
            }
        });


    }

    //Start Listening Adapter
    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    //Stop Listening Adapter
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void DateFilter() {
        dateFilter = findViewById(R.id.filterCal);
        filterDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                filterCal.set(Calendar.YEAR, year);
                filterCal.set(Calendar.MONTH, monthOfYear);
                filterCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }

        };

        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                new DatePickerDialog(mContext, filterDate, filterCal
                        .get(Calendar.YEAR), filterCal.get(Calendar.MONTH),
                        filterCal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

}
