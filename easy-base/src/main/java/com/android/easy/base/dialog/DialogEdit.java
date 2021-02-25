package com.android.easy.base.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.easy.base.R;
import com.android.easy.base.em.EditType;
import com.android.easy.base.util.StringUtils;
import com.android.easy.base.util.TextUtils;
import com.android.easy.base.widget.EditTextWatcher;


/**
 *
 * author abook23
 * 2016/9/9
 */
public class DialogEdit extends DialogFragment {

    public final static String DESCRIBE = "describe";
    public final static String TITLE = "title";

    private String title, describe;

    private TextView mTitle, mDescribe, mErrMsg;
    private EditText mContent;
    private Button mButtonCancel, mButtonOk;
    private EditType mEditType;
    private OnEditDialogListener mListener;
    private int numberDecimal;
    private int requestCode;

    private void DialogEdit() {
    }

    public static DialogEdit newInstance(String title, String describe, EditType editType, int requestCode) {
        DialogEdit fragment = new DialogEdit();
        Bundle bundle = new Bundle();
        bundle.putString(DESCRIBE, describe);
        bundle.putString(TITLE, title);
        fragment.setArguments(bundle);
        fragment.mEditType = editType;
        fragment.requestCode = requestCode;

        return fragment;
    }

    public void setDescribe(int numberDecimal) {
        this.numberDecimal = numberDecimal;
    }

    public void setEditDialogListener(OnEditDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mListener == null)
            if (context instanceof OnEditDialogListener) {
                mListener = (OnEditDialogListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnEditDialogListener");
            }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            describe = getArguments().getString(DESCRIBE);
            title = getArguments().getString(TITLE);
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);//无标题栏
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit, container);
        mTitle = (TextView) view.findViewById(R.id.title);
        mDescribe = (TextView) view.findViewById(R.id.describe);
        mErrMsg = (TextView) view.findViewById(R.id.err_msg);
        mContent = (EditText) view.findViewById(R.id.content);
        mButtonCancel = (Button) view.findViewById(R.id.but_cancel);
        mButtonOk = (Button) view.findViewById(R.id.but_ok);

        mTitle.setText(title);
        mDescribe.setText(describe);
        mButtonCancel.setVisibility(View.GONE);
        mErrMsg.setVisibility(View.GONE);

        mContent.addTextChangedListener(new MTextWatcher(mContent, mEditType, numberDecimal));

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkText(mContent.getText().toString());
            }
        });
        return view;
    }

    private boolean checkText(String s) {
        switch (mEditType) {
            case phone:
                if (!StringUtils.isMobileNO(s = s.replaceAll(" ", ""))) {
                    mErrMsg.setVisibility(View.VISIBLE);
                    mErrMsg.setText(getResources().getString(R.string.phone_err));
                    return false;
                }
                break;
            case idNumber:
                if (!StringUtils.isIdNumberNo(s = s.replaceAll(" ", ""))) {
                    mErrMsg.setVisibility(View.VISIBLE);
                    mErrMsg.setText(getResources().getString(R.string.id_number_err));
                    return false;
                }
                break;
            case email:
                if (!TextUtils.isEmail(s)) {
                    mErrMsg.setVisibility(View.VISIBLE);
                    mErrMsg.setText(getResources().getString(R.string.email_err));
                    return false;
                }
                break;
            default:
                break;
        }
        dismiss();
        mListener.onEditDialog(s, requestCode);
        return true;
    }

    public interface OnEditDialogListener {
        void onEditDialog(String value, int requestCode);
    }

    private class MTextWatcher extends EditTextWatcher {

        public MTextWatcher(EditText editText, EditType type, int decimal) {
            super(editText, type, decimal);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            super.beforeTextChanged(s, start, count, after);
            if (mErrMsg.getVisibility() != View.GONE)
                mErrMsg.setVisibility(View.INVISIBLE);
        }
    }
}
