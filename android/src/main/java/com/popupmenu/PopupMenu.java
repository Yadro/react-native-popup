package com.popupmenu;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PopupMenu extends ReactContextBaseJavaModule {

    public PopupMenu(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "PopupMenu";
    }

    @ReactMethod
    public void showInput(String title, String content, ReadableArray buttons, String placeholder, String value, final Callback successCallback) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(currentActivity);
        builder = addTitleContent(builder, title, content);
        builder = addButtons(builder, buttons);
        builder
            .inputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            .input(placeholder, value, new MaterialDialog.InputCallback() {
                @Override
                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    WritableMap response = Arguments.createMap();
                    response.putString("input", input.toString());
                    successCallback.invoke(response);
                }
            })
            .show();
    }

    /**
     * title React.PropTypes.string,
     * items React.PropTypes.arrayOf(React.PropTypes.string),
     * callback
     */
    @ReactMethod
    public void showMultiChoice(String title, String content, ReadableArray buttons,
                                final ReadableArray items, final Callback successCallback) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(currentActivity);
        builder = addTitleContent(builder, title, content);
        builder = addButtons(builder, buttons);
        builder
            .items(readableArrToArr(items))
            .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                    WritableMap response = Arguments.createMap();
                    WritableArray items2 = Arguments.createArray();
                    for (Integer integer : which) {
                        items2.pushInt(integer);
                    }
                    response.putArray("items", items2);
                    WritableArray text2 = Arguments.createArray();
                    for (CharSequence str : text) {
                        text2.pushString(str.toString());
                    }
                    response.putArray("text", text2);
                    successCallback.invoke(response);
                    return true;
                }
            })
            .show();
    }

    @ReactMethod
    public void showRadio(String title, String content, ReadableArray buttons, final ReadableMap checkbox,
                          ReadableArray items, final Callback successCallback) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(currentActivity);
        builder = addTitleContent(builder, title, content);
        builder = addButtons(builder, buttons);
        builder = addCheckbox(builder, checkbox);
        builder
                .items(readableArrToArr(items))
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        WritableMap response = Arguments.createMap();
                        response.putInt("which", which);
                        if (text != null) {
                            response.putString("item", text.toString());
                        }
                        if (checkbox.hasKey("text") || checkbox.hasKey("value")) {
                            response.putBoolean("checkbox", dialog.isPromptCheckBoxChecked());
                        }
                        successCallback.invoke(response);
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        return true;
                    }
                })
                .show();
    }

    @ReactMethod
    public void showChoice(String title, String content, ReadableArray items, final Callback successCallback) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(currentActivity);
        builder = addTitleContent(builder, title, content);
        builder
                .items(readableArrToArr(items))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        WritableMap response = Arguments.createMap();
                        response.putInt("which", which);
                        response.putString("item", text.toString());
                        successCallback.invoke(response);
                    }
                })
                .show();
    }

    /**
     * title React.PropTypes.string,
     * content React.PropTypes.content,
     * buttons React.PropTypes.arrayOf(React.PropTypes.string),
     * items React.PropTypes.arrayOf(React.PropTypes.string),
     * callback
     */
    @ReactMethod
    public void showDialog(String title, String content, ReadableArray buttons,
                           ReadableMap checkbox, Boolean dark, final Callback successCallback) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        final Checkbox parsedCheckbox = parseCheckbox(checkbox);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(currentActivity);
        builder = addDark(builder, dark);
        builder = addTitleContent(builder, title, content);
        builder = addButtons(builder, buttons);
        builder = addCheckbox(builder, checkbox);
        if (parsedCheckbox != null) {
            builder = builder.checkBoxPrompt(parsedCheckbox.text, parsedCheckbox.value, null);
        }
        builder
            .onAny(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (parsedCheckbox != null) {
                        successCallback.invoke(responseDialogWithCheckbox(actionToInt(which), dialog.isPromptCheckBoxChecked()));
                    } else {
                        successCallback.invoke(actionToInt(which));
                    }
                }
            })
            .show();
    }

    private MaterialDialog.Builder addDark(MaterialDialog.Builder builder, Boolean dark) {
        return builder.theme(dark ? Theme.DARK : Theme.LIGHT);
    }

    private MaterialDialog.Builder addTitleContent(MaterialDialog.Builder builder, String title, String content) {
        if (title.length() != 0) {
            builder = builder.title(title);
        }
        if (content.length() != 0) {
            builder = builder.content(content);
        }
        return builder;
    }

    private MaterialDialog.Builder addButtons(MaterialDialog.Builder builder, ReadableArray buttons) {
        ArrayList<CharSequence> buttonItems = readableArrToArr(buttons);
        switch (buttonItems.size()) {
            case 1:
                return builder
                        .positiveText(buttonItems.get(0));
            case 2:
                return builder
                        .negativeText(buttonItems.get(0))
                        .positiveText(buttonItems.get(1));
            case 3:
                return builder
                        .neutralText(buttonItems.get(0))
                        .negativeText(buttonItems.get(1))
                        .positiveText(buttonItems.get(2));
        }
        return builder;
    }

    private MaterialDialog.Builder addCheckbox(MaterialDialog.Builder builder, ReadableMap checkbox) {
        Checkbox parsedCheckbox = parseCheckbox(checkbox);
        if (parsedCheckbox == null) {
            return builder;
        }
        return builder.checkBoxPrompt(parsedCheckbox.text, parsedCheckbox.value, null);
    }

    private Checkbox parseCheckbox(ReadableMap checkbox) {
        String text;
        Boolean value = false;
        if (checkbox.hasKey("text")) {
            text = checkbox.getString("text");
        } else {
            return null;
        }
        if (checkbox.hasKey("value")) {
            value = checkbox.getBoolean("value");
        }
        return new Checkbox(text, value);
    }

    private ArrayList<CharSequence> readableArrToArr(ReadableArray items) {
        ArrayList<CharSequence> charSequences = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            charSequences.add(new StringBuilder(items.getString(i)));
        }
        return charSequences;
    }

    private int actionToInt(DialogAction which) {
        switch (which) {
            case NEGATIVE:
                return -1;
            case NEUTRAL:
                return 0;
            case POSITIVE:
                return 1;
        }
        return -2;
    }

    private WritableMap responseDialogWithCheckbox(int which, Boolean checked) {
        WritableMap response = Arguments.createMap();
        response.putInt("which", which);
        response.putBoolean("checkbox", checked);
        return response;
    }

    private class Checkbox {
        public final String text;
        public final Boolean value;
        public Checkbox(String text, Boolean value) {
            this.text = text;
            this.value = value;
        }
    }

    private class Button {
        public String text;
        public Callback callback;

        public Button(String text, Callback callback) {
            this.text = text;
            this.callback = callback;
        }
    }
}