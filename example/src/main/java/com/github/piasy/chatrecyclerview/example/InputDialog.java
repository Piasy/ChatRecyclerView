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

package com.github.piasy.chatrecyclerview.example;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

/**
 * Created by Piasy{github.com/Piasy} on 6/3/16.
 */
public class InputDialog extends DialogFragment {

    private Action mAction;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAction = (Action) getTargetFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAction = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        // without title and title divider

        // Less dimmed background; see http://stackoverflow.com/q/13822842/56285
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //CHECKSTYLE:OFF
        params.dimAmount = 0;
        //CHECKSTYLE:ON
        window.setAttributes(params);

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);

        // Transparent background; see http://stackoverflow.com/q/15007272/56285
        // (Needed to make dialog's alpha shadow look good)
        window.setBackgroundDrawableResource(android.R.color.transparent);

        final Resources res = getResources();
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        if (titleDividerId > 0) {
            final View titleDivider = getDialog().findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(res.getColor(android.R.color.transparent));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        return inflater.inflate(R.layout.ui_input, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText editText = (EditText) view.findViewById(R.id.mEtInput);
        view.findViewById(R.id.mBtnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAction.send(editText.getText().toString());
                dismiss();
            }
        });
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (!isOpen) {
                            dismiss();
                        }
                    }
                });
        editText.post(new Runnable() {
            @Override
            public void run() {
                UIUtil.showKeyboard(getContext(), editText);
            }
        });
    }

    public int dip2px(int dipValue) {
        float reSize = getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    public interface Action {
        void send(String text);
    }
}
