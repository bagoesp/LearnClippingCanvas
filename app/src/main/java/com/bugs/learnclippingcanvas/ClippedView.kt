package com.bugs.learnclippingcanvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ClippedView : View {
    private lateinit var mPaint: Paint
    private lateinit var mPath: Path

    private val mClipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val mClipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val mClipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val mClipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
    private val mRectInset = resources.getDimension(R.dimen.rectInset)
    private val mSmallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val mCircleRadius = resources.getDimension(R.dimen.circleRadius)

    private val mTextOffset = resources.getDimension(R.dimen.textOffset)
    private val mTextSize = resources.getDimension(R.dimen.textSize)

    private val mColumnOne = mRectInset
    private val mColumnTwo = mColumnOne + mRectInset + mClipRectRight

    private val mRowOne = mRectInset
    private val mRowTwo = mRowOne + mRectInset + mClipRectBottom
    private val mRowThree = mRowTwo + mRectInset + mClipRectBottom
    private val mRowFour = mRowThree + mRectInset + mClipRectBottom
    private val mTextRow = mRowFour + (1.5 * mClipRectBottom).toInt()

    private lateinit var mRectF: RectF

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        isFocusable = true
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        mPaint.textSize = resources.getDimension(R.dimen.textSize)
        mPath = Path()
        mRectF = RectF( Rect( mRectInset.toInt(), mRectInset.toInt(),
            (mClipRectRight-mRectInset).toInt(), (mClipRectBottom-mRectInset).toInt() ) )
    }

    private fun drawClippedRectangle(canvas: Canvas){
        // Set the boundaries of the clipping rectangle for whole picture.
        canvas.clipRect(mClipRectLeft, mClipRectTop, mClipRectRight, mClipRectBottom)

        // Fill the canvas with white.
        // With the clipped rectangle, this only draws
        // inside the clipping rectangle.
        // The rest of the surface remains gray.
        canvas.drawColor(Color.WHITE)

        // Change the color to red and
        // draw a line inside te clipping rectangle.
        mPaint.color = Color.RED
        canvas.drawLine(mClipRectLeft, mClipRectTop, mClipRectRight, mClipRectBottom, mPaint)

        // Set the color to green and
        // draw a circle inside the clipping rectangle.
        mPaint.color = Color.GREEN
        canvas.drawCircle(mCircleRadius, mClipRectBottom - mCircleRadius, mCircleRadius, mPaint)

        // Set the color to blue and draw text aligned with the right edge
        // of the clipping rectangle.
        mPaint.color = Color.BLUE
        // Align the RIGHT side of the origin.
        mPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(context.getString(R.string.clipping), mClipRectRight, mTextOffset, mPaint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.GRAY)
        canvas.save()

        // draw 1st rectangle
        canvas.translate(mColumnOne, mRowOne)
        drawClippedRectangle(canvas)
        canvas.restore()

        // draw 2nd rectangle
        canvas.save()
        canvas.translate(mColumnTwo, mRowOne)
        canvas.clipRect(2 * mRectInset, 2 * mRectInset,
            mClipRectRight - 2 * mRectInset, mClipRectBottom-2 * mRectInset)
        canvas.clipRect(4 * mRectInset, 4 * mRectInset,
        mClipRectRight-4 * mRectInset, mClipRectBottom-4 * mRectInset,
        Region.Op.DIFFERENCE)
        drawClippedRectangle(canvas)
        canvas.restore()

        // draw 3rd rectangle
        canvas.save()
        canvas.translate(mColumnOne, mRowTwo)
        mPath.rewind()
        mPath.addCircle(mCircleRadius, mClipRectBottom-mCircleRadius, mCircleRadius,
            Path.Direction.CCW)
        canvas.clipPath(mPath, Region.Op.DIFFERENCE)
        drawClippedRectangle(canvas)
        canvas.restore()

        // draw 4th rectangle
        canvas.save()
        canvas.translate(mColumnTwo, mRowTwo)
        canvas.clipRect(mClipRectLeft, mClipRectTop, mClipRectRight-mSmallRectOffset,
            mClipRectBottom-mSmallRectOffset)
        canvas.clipRect(mClipRectLeft+mSmallRectOffset, mClipRectTop+mSmallRectOffset,
            mClipRectRight, mClipRectBottom, Region.Op.INTERSECT)
        drawClippedRectangle(canvas)
        canvas.restore()

        // draw 5th rectangle
        canvas.save()
        canvas.translate(mColumnOne, mRowThree)
        mPath.rewind()
        mPath.addCircle(mClipRectLeft+mRectInset+mCircleRadius,
            mClipRectTop+mCircleRadius+mRectInset,
            mCircleRadius, Path.Direction.CCW)
        mPath.addRect(mClipRectRight/2-mCircleRadius,
            mClipRectTop+mCircleRadius+mRectInset,
            mClipRectRight/2+mCircleRadius,
            mClipRectBottom-mRectInset, Path.Direction.CCW)
        canvas.clipPath(mPath)
        drawClippedRectangle(canvas)
        canvas.restore()

        // draw 6th rectangle
        canvas.save()
        canvas.translate(mColumnTwo, mRowThree)
        mPath.rewind()
        mPath.addRoundRect(mRectF, (mClipRectRight/4).toFloat(),
            (mClipRectRight/4).toFloat(), Path.Direction.CCW)
        canvas.clipPath(mPath)
        drawClippedRectangle(canvas)
        canvas.restore()

        // draw 7th rectangle
        canvas.save()
        canvas.translate(mColumnOne, mRowFour)
        canvas.clipRect(2*mRectInset, 2*mRectInset,
            mClipRectRight-2*mRectInset, mClipRectBottom-2*mRectInset)
        drawClippedRectangle(canvas)
        canvas.restore()

        // draw text translate with transformation applied
        canvas.save()
        mPaint.color = Color.CYAN
        mPaint.textAlign = Paint.Align.LEFT
        canvas.translate(mColumnTwo, mTextRow)
        canvas.drawText(context.getString(R.string.translated), 0F, 0F, mPaint)
        canvas.restore()

        // draw text translate and skew transformation applied
        canvas.save()
        mPaint.textSize = mTextSize
        mPaint.textAlign = Paint.Align.RIGHT
        // position text
            canvas.translate(mColumnTwo, mTextRow)
            // apply skew transformation.
            canvas.skew(0.2F, 0.3F)
            canvas.drawText(context.getString(R.string.skewed), 0F, 0F, mPaint)
        canvas.restore()
    } // End draw
}