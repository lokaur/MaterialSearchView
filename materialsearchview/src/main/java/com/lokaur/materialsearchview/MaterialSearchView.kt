package com.lokaur.materialsearchview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.lokaur.materialsearchview.interfaces.OnSearchListener

class MaterialSearchView : RelativeLayout {

    private lateinit var mSearchET: EditText
    private lateinit var mSearchRL: RelativeLayout
    private var mOnSearchListener: OnSearchListener? = null

    constructor(context: Context?) : super(context) {
        initLayout()
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initLayout()
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout()
        init(attrs, defStyleAttr)
    }

    fun setOnSearchListener(onSearchListener: OnSearchListener) {
        mOnSearchListener = onSearchListener
    }

    override fun isFocused(): Boolean {
        return mSearchET.isFocused
    }

    fun isVisible(): Boolean {
        return mSearchRL.visibility == View.VISIBLE
    }

    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.msv_simple_search_view, this, true)
        mSearchET = findViewById(R.id.msv_searchET)
        mSearchRL = findViewById(R.id.msv_searchRL)

        val backIB = findViewById<ImageButton>(R.id.msv_backBT)
        backIB.setOnClickListener { hide() }

        val clearIB = findViewById<ImageButton>(R.id.msv_clearBT)
        clearIB.setOnClickListener {
            mSearchET.setText("")
            mOnSearchListener?.onSearchQueryCleared()
        }

        mSearchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mOnSearchListener?.onSearchQueryChange(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        mSearchET.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mOnSearchListener?.onSearchQuerySubmit(mSearchET.text.toString())
                unFocusSearch()
                true
            } else false
        }
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context
                .obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, defStyleAttr, 0)
        if (typedArray != null) {
            if (typedArray.hasValue(R.styleable.MaterialSearchView_hint))
                mSearchET.hint = typedArray.getString(R.styleable.MaterialSearchView_hint)

            if (typedArray.hasValue(R.styleable.MaterialSearchView_text))
                mSearchET.setText(typedArray.getString(R.styleable.MaterialSearchView_text))

            typedArray.recycle()
        }
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        if (isVisible() && event.keyCode == KeyEvent.KEYCODE_BACK) {
            mSearchET.clearFocus()
        }

        return super.dispatchKeyEventPreIme(event)
    }


    private val inputMethodManager: InputMethodManager? = context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private fun focusSearch() {
        mSearchET.requestFocus()
        inputMethodManager?.showSoftInput(mSearchET, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun unFocusSearch() {
        if (mSearchET.isFocused) {
            mSearchET.clearFocus()
            inputMethodManager?.hideSoftInputFromWindow(mSearchET.windowToken, 0)
        }
    }

    fun show() {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 200
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                mSearchRL.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                focusSearch()
                mOnSearchListener?.onSearchViewShown()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        mSearchRL.startAnimation(fadeIn)
    }

    fun hide() {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.duration = 200
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                unFocusSearch()
            }

            override fun onAnimationEnd(animation: Animation) {
                mSearchRL.visibility = View.GONE
                mOnSearchListener?.onSearchViewClosed()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        mSearchRL.startAnimation(fadeOut)
    }

    fun getSearchQuery(): String {
        return mSearchET.text.toString()
    }
}