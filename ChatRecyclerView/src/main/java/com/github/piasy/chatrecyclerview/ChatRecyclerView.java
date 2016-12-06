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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

/**
 * Created by Piasy{github.com/Piasy} on 5/25/16.
 */
public class ChatRecyclerView extends RecyclerView {

    private long mLastScrollTime;
    private boolean mIsIdleFromDrag;
    private int mNewMessagePosition;
    private long mAutoScrollTimeout;
    private boolean mAutoScrollOnUserLeave;
    private Runnable mAutoScroll;
    /**
     * There is a bug, in Xiaomi MI NOTE LTE, and One plus 3, YOLO's live room chat,
     * when use want to input text, we show a dialog fragment, when this dialog fragment shows,
     * the recycler view show its first item, without triggering the scroll callback.
     *
     * So we observe the global layout changes, and when we are showing the first item
     * (last visible position is item count - 1), we force scroll to bottom immediately.
     */
    private ViewTreeObserver.OnGlobalLayoutListener mScroll2TopFix;
    private volatile int mPendingMsgCount;

    public ChatRecyclerView(Context context) {
        this(context, null);
    }

    public ChatRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ChatRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initAutoScroll(int newMessagePosition, long timeout,
            boolean autoScrollOnUserLeave) {
        mNewMessagePosition = newMessagePosition;
        mAutoScrollTimeout = timeout;
        mAutoScrollOnUserLeave = autoScrollOnUserLeave;

        if (mAutoScrollOnUserLeave) {
            mAutoScroll = new Runnable() {
                @Override
                public void run() {
                    if (mPendingMsgCount > 0) {
                        // notifyItem*** is problematic, causing
                        // `java.lang.IndexOutOfBoundsException: Inconsistency detected.
                        // Invalid view holder adapter`
                        // use notifyDataSetChanged instead
                        getAdapter().notifyDataSetChanged();
                        scrollToPosition(mNewMessagePosition);
                        mPendingMsgCount = 0;
                    } else if (getAdapter().getItemCount() > 0) {
                        smoothScrollToPosition(mNewMessagePosition);
                    }
                }
            };
        }
        mScroll2TopFix = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getLayoutManager() instanceof LinearLayoutManager) {
                    int lastVisiblePos = ((LinearLayoutManager) getLayoutManager())
                                                 .findLastVisibleItemPosition() + 1;
                    if (lastVisiblePos == getAdapter().getItemCount()) {
                        mAutoScroll.run();
                    }
                }
            }
        };

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

        getViewTreeObserver().addOnGlobalLayoutListener(mScroll2TopFix);
    }

    public void notifyNewMessage() {
        if (System.currentTimeMillis() - mLastScrollTime > mAutoScrollTimeout) {
            if (mPendingMsgCount > 0) {
                // notifyItem*** is problematic, causing
                // `java.lang.IndexOutOfBoundsException: Inconsistency detected.
                // Invalid view holder adapter`
                // use notifyDataSetChanged instead
                getAdapter().notifyDataSetChanged();
                scrollToPosition(mNewMessagePosition);
                mPendingMsgCount = 0;
            } else if (mPendingMsgCount == 0) {
                // normal case, we can have animation :)
                getAdapter().notifyItemInserted(mNewMessagePosition);
                scrollToPosition(mNewMessagePosition);
            } else if (getAdapter().getItemCount() > 0) {
                smoothScrollToPosition(mNewMessagePosition);
            }
            removeCallbacks(mAutoScroll);
        } else {
            mPendingMsgCount++;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAutoScroll != null) {
            removeCallbacks(mAutoScroll);
        }
        if (mScroll2TopFix != null) {
            getViewTreeObserver().removeOnGlobalLayoutListener(mScroll2TopFix);
        }
    }
}
