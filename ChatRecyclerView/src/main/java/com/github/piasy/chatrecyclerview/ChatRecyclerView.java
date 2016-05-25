/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.chatrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Piasy{github.com/Piasy} on 5/25/16.
 */
public class ChatRecyclerView extends RecyclerView {
    public ChatRecyclerView(Context context) {
        this(context, null);
    }

    public ChatRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ChatRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private long mLastScrollTime;
    private boolean mIsIdleFromDrag;

    private int mNewMessagePosition;
    private long mAutoScrollTimeout;
    private boolean mAutoScrollOnUserLeave;

    private Runnable mAutoScroll;
    private boolean mHasPendingMessage;

    public void initAutoScroll(int newMessagePosition, long timeout,
            boolean autoScrollOnUserLeave) {
        mNewMessagePosition = newMessagePosition;
        mAutoScrollTimeout = timeout;
        mAutoScrollOnUserLeave = autoScrollOnUserLeave;

        if (mAutoScrollOnUserLeave) {
            mAutoScroll = new Runnable() {
                @Override
                public void run() {
                    if (mHasPendingMessage) {
                        getAdapter().notifyItemInserted(mNewMessagePosition);
                        scrollToPosition(mNewMessagePosition);
                        mHasPendingMessage = false;
                    } else if (getChildCount() > 0) {
                        smoothScrollToPosition(mNewMessagePosition);
                    }
                }
            };
        }

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_DRAGGING:
                        mIsIdleFromDrag = true;
                        break;
                    case SCROLL_STATE_IDLE:
                        if (mIsIdleFromDrag) {
                            mLastScrollTime = System.currentTimeMillis();
                            mIsIdleFromDrag = false;
                            if (mAutoScrollOnUserLeave) {
                                removeCallbacks(mAutoScroll);
                                postDelayed(mAutoScroll, mAutoScrollTimeout);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void notifyNewMessage() {
        if (System.currentTimeMillis() - mLastScrollTime > mAutoScrollTimeout) {
            getAdapter().notifyItemInserted(mNewMessagePosition);
            if (getChildCount() > 0) {
                scrollToPosition(mNewMessagePosition);
            }
            removeCallbacks(mAutoScroll);
        } else {
            mHasPendingMessage = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mAutoScroll);
    }
}
