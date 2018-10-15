package com.lokaur.materialsearchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaur.materialsearchview.interfaces.OnSearchListener;

public class SimpleSearchView extends RelativeLayout {

    private EditText mSearchET;
    private RelativeLayout mSearchRL;
    private OnSearchListener mOnSearchListener;

    public SimpleSearchView(Context context) {
        super(context);
        initLayout();
        init(null, 0);
    }

    public SimpleSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
        init(attrs, 0);
    }

    public SimpleSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
        init(attrs, defStyleAttr);
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        mOnSearchListener = onSearchListener;
    }

    public boolean isFocused() {
        return mSearchET.isFocused();
    }

    public boolean isVisible() {
        return mSearchRL.getVisibility() == VISIBLE;
    }

    private void initLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.simple_search_view, this, true);
        mSearchET = findViewById(R.id.searchET);
        mSearchRL = findViewById(R.id.searchRL);

        ImageButton backIB = findViewById(R.id.backBT);
        backIB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        ImageButton clearIB = findViewById(R.id.clearBT);
        clearIB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchET.setText("");
            }
        });

        mSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mOnSearchListener != null)
                    mOnSearchListener.onSearchQueryChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mOnSearchListener != null)
                        mOnSearchListener.onSearchQuerySubmit(mSearchET.getText().toString());

                    unFocusSearch();
                    return true;
                }

                return false;
            }
        });
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.SimpleSearchView, defStyleAttr, 0);
        if (typedArray != null) {
            if (typedArray.hasValue(R.styleable.SimpleSearchView_msvHint))
                mSearchET.setHint(typedArray.getString(R.styleable.SimpleSearchView_msvHint));

            if (typedArray.hasValue(R.styleable.SimpleSearchView_msvText))
                mSearchET.setText(typedArray.getString(R.styleable.SimpleSearchView_msvText));

            typedArray.recycle();
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (isVisible() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mSearchET.clearFocus();
        }

        return super.dispatchKeyEventPreIme(event);
    }

    private void focusSearch() {
        mSearchET.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(mSearchET, InputMethodManager.SHOW_IMPLICIT);
    }

    private void unFocusSearch() {
        if (mSearchET.isFocused()) {
            mSearchET.clearFocus();
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mSearchET.getWindowToken(), 0);
            }
        }
    }

    public void show() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(200);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSearchRL.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                focusSearch();
                if (mOnSearchListener != null) {
                    mOnSearchListener.onSearchViewShown();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mSearchRL.startAnimation(fadeIn);
    }

    public void hide() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(200);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                unFocusSearch();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSearchRL.setVisibility(GONE);
                if (mOnSearchListener != null) {
                    mOnSearchListener.onSearchViewClosed();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mSearchRL.startAnimation(fadeOut);
    }
}
