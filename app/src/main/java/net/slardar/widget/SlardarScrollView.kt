package net.slardar.widget

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import net.slardar.noadsyoutube.R

class SlardarScrollView : NestedScrollView {
    private val onTouchListener: SlardarScrollOnTouchListener
    private var customOnTouchListener: OnTouchListener? = null


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var scrollChanged: ((SlardarScrollView) -> Unit)? = null
    private var reachTop: ((SlardarScrollView) -> Unit)? = null
    private var reachBottom: ((SlardarScrollView) -> Unit)? = null

    private var topRefresh: ((SlardarScrollView) -> Unit)? = null
    private var bottomRefresh: ((SlardarScrollView) -> Unit)? = null

    private var damping: Float

    private var reloadBitmap: Bitmap
    private var reloadBitmapOrigin: Bitmap
    private var reloadBitmapBackgroundPaint: Paint

    private var showTopRefreshIcon: Boolean = true
    private var showBottomRefreshIcon: Boolean = true

    init {
        onTouchListener = SlardarScrollOnTouchListener(this)
        setOnTouchListener(onTouchListener)
        damping = 300.0f

        reloadBitmapOrigin = getBitmapById(R.drawable.ic_reload)
        reloadBitmapBackgroundPaint = Paint()
        reloadBitmapBackgroundPaint.style = Paint.Style.FILL
        reloadBitmapBackgroundPaint.color = Color.argb(255, 85, 85, 85)
        reloadBitmap = buildReloadBitmap(reloadBitmapOrigin)
    }

    fun setScrollChangedListener(scrollChanged: ((SlardarScrollView) -> Unit)?) {
        this.scrollChanged = scrollChanged
    }

    fun removeScrollChangedListener() {
        this.setScrollChangedListener(null)
    }

    fun setReachTopListner(reachTop: ((SlardarScrollView) -> Unit)?) {
        this.reachTop = reachTop
    }

    fun removeReachTopListner() {
        this.setReachTopListner(null)
    }

    fun setReachBottom(reachBottom: ((SlardarScrollView) -> Unit)?) {
        this.reachBottom = reachBottom
    }

    fun removeReachBottomListener() {
        this.setReachBottom(null)
    }

    fun setTopRefresh(topRefresh: ((SlardarScrollView) -> Unit)?) {
        this.topRefresh = topRefresh
    }

    fun removeTopRefresh() {
        this.setTopRefresh(null)
    }

    fun setBottomRefresh(bottomRefresh: ((SlardarScrollView) -> Unit)?) {
        this.bottomRefresh = bottomRefresh
    }

    fun removeBottomRefresh() {
        this.setBottomRefresh(null)
    }

    fun setDamping(damping: Float) {
        this.damping = damping
    }

    fun getDamping(): Float {
        return this.damping
    }

    override fun setOverScrollMode(overScrollMode: Int) {
        super.setOverScrollMode(View.OVER_SCROLL_NEVER)
    }

    fun setShowTopRefreshIcon(showTopRefreshIcon: Boolean) {
        this.showTopRefreshIcon = showTopRefreshIcon
    }

    fun getShowTopRefreshIcon(): Boolean {
        return showTopRefreshIcon
    }

    fun setShowBottomRefreshIcon(showBottomRefreshIcon: Boolean) {
        this.showBottomRefreshIcon = showBottomRefreshIcon
    }

    fun getShowBottomRefreshIcon(): Boolean {
        return this.showBottomRefreshIcon
    }

    fun getScrollBottom(): Int {
        return this.getChildAt(this.childCount - 1).bottom + this.paddingBottom - this.height
    }


    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        when (onTouchListener.isRefreshLoading()) {
            SlardarScrollOnTouchListener.MatrixRefresh.TOP -> {
                if (showTopRefreshIcon) {
                    val loadingMatrix: Matrix? =
                        onTouchListener.getReloadLoadingMatrix(SlardarScrollOnTouchListener.MatrixRefresh.TOP)
                    loadingMatrix?.run {
                        canvas.drawBitmap(reloadBitmap, this, null)
                    }
                }
            }
            SlardarScrollOnTouchListener.MatrixRefresh.BOTTOM -> {
                if (showBottomRefreshIcon) {
                    val loadingMatrix: Matrix? =
                        onTouchListener.getReloadLoadingMatrix(SlardarScrollOnTouchListener.MatrixRefresh.BOTTOM)
                    loadingMatrix?.run {
                        canvas.drawBitmap(reloadBitmap, this, null)
                    }
                }
            }
            else -> {

            }
        }
    }

    fun setReloadBitmap(reloadBitmap: Bitmap) {
        this.reloadBitmapOrigin = reloadBitmap
        this.reloadBitmap = buildReloadBitmap(reloadBitmapOrigin)
    }

    fun setReloadBitmapBackgroundColor(color: Int) {
        reloadBitmapBackgroundPaint.color = color
        this.reloadBitmap = buildReloadBitmap(reloadBitmapOrigin)
    }

    private fun buildReloadBitmap(reloadBitmap: Bitmap): Bitmap {
        val newSize: Int = Math.round(reloadBitmap.width * 1.8f)
        val padding: Float = (newSize - reloadBitmap.width) / 2.0f

        val tmpBitmap: Bitmap = Bitmap.createBitmap(newSize, newSize, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(tmpBitmap)
        canvas.drawCircle(
            tmpBitmap.width / 2.0f,
            tmpBitmap.width / 2.0f,
            tmpBitmap.width / 2.0f,
            reloadBitmapBackgroundPaint
        )

        canvas.drawBitmap(reloadBitmap, padding, padding, null)
        return tmpBitmap
    }

    private class SlardarScrollOnTouchListener(private val scrollView: SlardarScrollView) : OnTouchListener {
        private val refreshRate: Float = 0.5f
        private val loadingRate: Float = 0.1f
        private val rotationRate: Float = 135.0f

        private var bottom: Int = 0
        private var startScrollY: Int = -1
        private var startY: Float = -1.0f
        private var loadingSize: Int = 0
        private var currentRawY: Float = 0.0f

        private var viewBottom: Int = 0

        private var refreshLoading = false

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            //Reach bottom
            if (scrollView.scrollY == bottom) {
                scrollView.reachBottom?.invoke(scrollView)
            }
            //Reach Top
            if (scrollView.scrollY == 0) {
                scrollView.reachTop?.invoke(scrollView)
            }

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (startScrollY == 0 && (startY - event.rawY) < 0 && scrollView.topRefresh != null) {
                        refreshLoading = true
                    }

                    if (startScrollY == bottom && (startY - event.rawY) > 0 && scrollView.bottomRefresh != null) {
                        refreshLoading = true
                    }

                    if (refreshLoading) {
                        currentRawY = event.rawY
                        scrollView.invalidate()
                    }
                }

                MotionEvent.ACTION_DOWN -> {
                    bottom =
                        scrollView.getChildAt(scrollView.childCount - 1).bottom + scrollView.paddingBottom - scrollView.height
                    startScrollY = scrollView.scrollY
                    startY = event.rawY

                    loadingSize = if (scrollView.width < scrollView.height) {
                        Math.round(scrollView.width * loadingRate)
                    } else {
                        Math.round(scrollView.height * loadingRate)
                    }

                    viewBottom = scrollView.getChildAt(scrollView.childCount - 1).bottom
                }

                MotionEvent.ACTION_UP -> {
                    if (refreshLoading && (startY - event.rawY) > scrollView.damping * refreshRate) {
                        scrollView.bottomRefresh?.run {
                            this.invoke(scrollView)
                        }
                    }


                    if ((event.rawY - startY) > scrollView.damping * refreshRate && refreshLoading) {
                        scrollView.topRefresh?.run {
                            this.invoke(scrollView)
                        }
                    }


                    startScrollY = -1
                    startY = -1.0f
                    refreshLoading = false
                    v.performClick()
                    scrollView.invalidate()
                }

                else -> {
                    Log.wtf("Event", MotionEvent.actionToString(event.action))
                }
            }

            return if (scrollView.customOnTouchListener != null) {
                scrollView.customOnTouchListener!!.onTouch(v, event)
            } else {
                refreshLoading
            }
        }

        fun getStartScrollY(): Int {
            return this.startScrollY
        }

        fun isRefreshLoading(): MatrixRefresh {
            return if (refreshLoading) {
                if (startScrollY == 0) {
                    MatrixRefresh.TOP
                } else {
                    MatrixRefresh.BOTTOM
                }
            } else {
                MatrixRefresh.NONE
            }
        }

        fun getReloadLoadingMatrix(matrixRefresh: MatrixRefresh): Matrix {
            val matrix = Matrix()
            return when (matrixRefresh) {
                MatrixRefresh.TOP -> {
                    matrix.setRotate(
                        ((currentRawY - startY) * rotationRate / scrollView.damping),
                        scrollView.reloadBitmap.width / 2.0f,
                        scrollView.reloadBitmap.width / 2.0f
                    )
                    matrix.postScale(
                        loadingSize / scrollView.reloadBitmap.width.toFloat(),
                        loadingSize / scrollView.reloadBitmap.width.toFloat()
                    )
                    if (currentRawY - startY > scrollView.damping) {
                        matrix.postTranslate((scrollView.width - loadingSize) / 2.0f, scrollView.damping - loadingSize)
                    } else {
                        matrix.postTranslate(
                            (scrollView.width - loadingSize) / 2.0f,
                            currentRawY - startY - loadingSize
                        )
                    }
                    matrix
                }

                MatrixRefresh.BOTTOM -> {

                    matrix.setRotate(
                        (-(currentRawY - startY) * rotationRate / scrollView.damping),
                        scrollView.reloadBitmap.width / 2.0f,
                        scrollView.reloadBitmap.width / 2.0f
                    )
                    matrix.postScale(
                        loadingSize / scrollView.reloadBitmap.width.toFloat(),
                        loadingSize / scrollView.reloadBitmap.width.toFloat()
                    )
                    if (currentRawY - startY < -scrollView.damping) {
                        matrix.postTranslate(
                            (scrollView.width - loadingSize) / 2.0f,
                            viewBottom.toFloat() - scrollView.damping
                        )
                    } else {
                        matrix.postTranslate(
                            (scrollView.width - loadingSize) / 2.0f,
                            viewBottom.toFloat() + currentRawY - startY
                        )
                    }
                    matrix
                }

                else -> {
                    matrix
                }

            }
        }

        enum class MatrixRefresh {
            TOP, BOTTOM, NONE
        }
    }

    private fun getBitmapById(id: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, id)!!
        val bitmap: Bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}