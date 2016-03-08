package com.wicam.a_common_utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Hyeonmin on 2015-07-22.
 */
public class EditTextAndTextCount {
    EditText editText;
    TextView textCount;
    int maxText;

    public EditTextAndTextCount(EditText editText, TextView textCount, int maxText) {
        this.editText = editText;
        this.textCount = textCount;
        this.maxText = maxText;
    }

    public void setEditTextAndTextCount() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equalsIgnoreCase(""))
                    textCount.setText("");
                else {
                    if (s.toString().equalsIgnoreCase(" ") || s.toString().equalsIgnoreCase("\n")) {
                        editText.setText(""); // 스페이스부터 시작하지 못하도록
                        textCount.setText("");
                    }
                    if (s.length() > maxText) {
                        s.delete(maxText-1, maxText);
                    }
                    textCount.setText(String.valueOf(editText.getText().toString().length()) + "/" + String.valueOf(maxText));
                }
            }
        });
    }
}
