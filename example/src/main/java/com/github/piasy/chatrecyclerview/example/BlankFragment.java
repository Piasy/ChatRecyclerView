package com.github.piasy.chatrecyclerview.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.github.piasy.chatrecyclerview.ChatRecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment implements InputDialog.Action {

    private Adapter mAdapter;
    private ChatRecyclerView mChatRecyclerView;
    private Button mBtnAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChatRecyclerView = (ChatRecyclerView) view.findViewById(R.id.mChatRv);
        mChatRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        mAdapter = new Adapter();
        mChatRecyclerView.setAdapter(mAdapter);
        mChatRecyclerView.initAutoScroll(0, 5000, true);

        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChatRecyclerView
        //        .getLayoutParams();
        //params.height = dip2px(170);
        //params.topMargin = 0;
        //mChatRecyclerView.setLayoutParams(params);

        mBtnAdd = (Button) view.findViewById(R.id.mBtnAdd);
        view.findViewById(R.id.mBtnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAdd.setVisibility(View.GONE);
                InputDialog inputDialog = new InputDialog();
                inputDialog.setTargetFragment(BlankFragment.this, 1001);
                getFragmentManager()
                        .beginTransaction()
                        .add(inputDialog, "InputDialog")
                        .commit();
            }
        });
    }

    public int dip2px(int dipValue) {
        float reSize = getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    @Override
    public void send(String text) {
        mBtnAdd.setVisibility(View.VISIBLE);
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
