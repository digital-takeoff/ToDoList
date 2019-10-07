package com.test.todo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.shreyaspatil.firebase.recyclerpagination.LoadingState.LOADED;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    final Calendar filterCal = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener filterDate;
    Context mContext;
    ImageButton dateFilter;
    ImageButton filterMenu;
    String searchString = null, type = null, date = null;
    SearchView searchView;
    PagedList.Config config;
    final int limit = 10;
    int start = 0;
    List<Task> tasks = new ArrayList<>();
    FirebaseRecyclerPagingAdapter<Task, TaskViewHolder> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        DateFilter();

        filterMenu = findViewById(R.id.filterBtn);
        filterMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), filterMenu);
            popup.getMenuInflater().inflate(R.menu.filtermenu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    type = menuItem.getTitle().toString();
                    searchView.setIconified(false);
                    searchView.requestFocus();
                    return true;
                }
            });
            popup.show();

        });

        //Initialize RecyclerView
        mRecyclerView = findViewById(R.id.taskRecView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todolist2-c16ea.firebaseio.com/");

        //Initialize PagedList Configuration
        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        //Initialize FirebasePagingOptions
        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter(s, null, config);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        if(searchString == null && date == null){
            filter(null, null, config);
        }

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
                filterByDate();
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

    private void filterByDate() {
        String myFormat = "d-M-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String finalDate = (sdf.format(filterCal.getTime()));
        filter(null, finalDate, config);
    }

    public void filter(String searchString, String date, PagedList.Config config){
        if(searchString == null && date == null){
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
                int counter = 0;
                @Override
                protected void onLoadingStateChanged(@NonNull LoadingState state) {
                    switch (state) {
                        case LOADING_INITIAL:
                        case LOADING_MORE:
                            Log.d("TAG", "LOADING MORE");
                            mSwipeRefreshLayout.setRefreshing(true);
                            break;

                        case LOADED:
                            counter++;
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
                    if(state == LOADED && counter == 1){
                        mRecyclerView.setAdapter(mAdapter);
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
        }else if(date != null){
            Log.d("==>" , "Date: " + date);

            Query query = mDatabase.orderByChild("dateCreated").equalTo(date)
                    .limitToFirst(10);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tasks.clear();
                    start += (limit+1);
                    for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                        Task task = keyNode.getValue(Task.class);
                        tasks.add(task);
                    }
                    mRecyclerView.setAdapter(new TaskAdapter(tasks));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(type != null){
            Log.d("==>" , "Date: " + date);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d("Type==>", ""+type +"Name"+ s);
                    String q = "";
                    if(type.equals("Name")){
                        q = "createdBy";
                    }else{
                        q = "type";
                    }
                    Log.d("TAG", q);
                    Query query = mDatabase.orderByChild(q).equalTo(s)
                            .limitToFirst(10);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tasks.clear();
                            start += (limit+1);
                            for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                                Task task = keyNode.getValue(Task.class);
                                tasks.add(task);
                            }
                            mRecyclerView.setAdapter(new TaskAdapter(tasks));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });



        }
    }
}
