package com.android.easy.base.util;

import android.widget.EditText;

import com.android.easy.base.em.EditType;
import com.android.easy.base.widget.EditTextWatcher;

/**
 * Created by abook23 on 2016/9/12.
 */
public class EditTextUtils extends TextUtils {

    public static void editPhone(EditText edit) {
        edit.addTextChangedListener(new EditTextWatcher(edit, EditType.phone));
    }

    public static void editIdNumber(EditText edit) {
        edit.addTextChangedListener(new EditTextWatcher(edit, EditType.idNumber));
    }

    public static void editNumberDecimal(EditText edit) {
        edit.addTextChangedListener(new EditTextWatcher(edit, EditType.numberDecimal));
    }

    public static void editNumberDecimal(EditText edit, int decimals) {
        edit.addTextChangedListener(new EditTextWatcher(edit, EditType.numberDecimal, decimals));
    }

}
