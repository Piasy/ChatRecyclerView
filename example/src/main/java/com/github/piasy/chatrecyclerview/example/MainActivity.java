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

public class MainActivity extends AppCompatActivity implements InputDialog.Action {

    private Adapter mAdapter;
    private ChatRecyclerView mChatRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChatRecyclerView = (ChatRecyclerView) findViewById(R.id.mChatRv);
        mChatRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        mAdapter = new Adapter();
        mChatRecyclerView.setAdapter(mAdapter);
        mChatRecyclerView.initAutoScroll(0, 5000, true);

        findViewById(R.id.mBtnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InputDialog().show(getFragmentManager(), "InputDialog");
            }
        });
    }

    @Override
    public void send(String text) {
        mAdapter.add(text);
        mChatRecyclerView.notifyNewMessage();
    }

    static class Adapter extends RecyclerView.Adapter<VH> {
        List<String> mItems = new ArrayList<>();

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ui_item, parent, false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.mTv.setText(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        void add(String text) {
            mItems.add(0, text);
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
