package com.example.milymozz.orderfood;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.milymozz.orderfood.Common.Common;
import com.example.milymozz.orderfood.Model.Rating;
import com.example.milymozz.orderfood.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference ratingTbl;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;

    private String foodId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_show_comment);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        ratingTbl = database.getReference("Rating");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerComment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //SwipeLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                if (!foodId.isEmpty() && foodId != null) {
                    // Create request query
                    Query query = ratingTbl.orderByChild("foodId").equalTo(foodId);

                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query, Rating.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtUserPhone.setText(model.getUserPhone());
                            holder.txtComment.setText(model.getComment());
                        }

                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View itemView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout, parent, false);

                            return new ShowCommentViewHolder(itemView);
                        }
                    };

                    loadComment(foodId);
                }

            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                if (getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                if (!foodId.isEmpty() && foodId != null) {

                    Query query = ratingTbl.orderByChild("foodId").equalTo(foodId);

                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query, Rating.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtUserPhone.setText(model.getUserPhone());
                            holder.txtComment.setText(model.getComment());
                        }

                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View itemView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout, parent, false);

                            return new ShowCommentViewHolder(itemView);
                        }
                    };

                    loadComment(foodId);
                }

            }
        });

    }

    private void loadComment(String foodId) {
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);

    }
}
