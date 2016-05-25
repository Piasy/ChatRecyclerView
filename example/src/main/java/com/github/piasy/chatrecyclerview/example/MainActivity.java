package com.github.piasy.chatrecyclerview.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.piasy.chatrecyclerview.ChatRecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ChatRecyclerView chatRecyclerView = (ChatRecyclerView) findViewById(R.id.mChatRv);
        chatRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        final Adapter adapter = new Adapter();
        chatRecyclerView.setAdapter(adapter);
        chatRecyclerView.initAutoScroll(0, 3000, true);

        findViewById(R.id.mBtnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(mCount++);
                chatRecyclerView.notifyNewMessage();
            }
        });
    }

    static class Adapter extends RecyclerView.Adapter<VH> {
        List<Integer> mItems = new ArrayList<>();

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ui_item, parent, false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.mTv.setText(String.valueOf(mItems.get(position)));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        void add(int item) {
            mItems.add(0, item);
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView mTv;

        public VH(View itemView) {
            super(itemView);
            mTv = (TextView) itemView;
        }
    }
}
