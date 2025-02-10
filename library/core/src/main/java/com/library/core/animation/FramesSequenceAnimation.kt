package com.library.core.animation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.widget.ImageView
import java.lang.ref.SoftReference

class FramesSequenceAnimation(
    mImageView: ImageView?,
    private var mFrames: IntArray,
    fps: Int
) {
    private var mIndex = 0// current frame
    private var mShouldRun =
        false// true if the animation should continue running. Used to stop the animation
    private var mIsRunning =
        false// true if the animation currently running. prevents starting the animation twice
    private var mIsResetWhenStart = false
    private var mCycledCount = 0// true reset index to 0. prevents use current index

    private var mSoftReferenceImageView: SoftReference<ImageView>? =
        null // Used to prevent holding ImageView when it should be dead.
    private var mHandler: Handler? = null
    private var mDelayMillis = 0L
    private val mOnAnimationStoppedListener: OnAnimationStoppedListener? = null
    private var mOnAnimationUpdateListener: OnAnimationUpdateListener? = null

    private var mBitmap: Bitmap? = null
    private var mBitmapOptions: BitmapFactory.Options? = null

    init {
        mImageView?.let { imageView ->
            mHandler = Handler()
            mIndex = -1
            mSoftReferenceImageView = SoftReference(imageView)
            mShouldRun = false
            mIsRunning = false
            mDelayMillis = 1000L / fps

            imageView.setImageResource(mFrames[0])

            // use in place bitmap to save GC work (when animation images are the same size & type)
            (imageView.drawable as? BitmapDrawable)?.bitmap?.let { bmp ->
                val width = bmp.width
                val height = bmp.height
                bmp.config?.let { config ->
                    mBitmap = Bitmap.createBitmap(width, height, config)
                }
            }

            mBitmapOptions = BitmapFactory.Options()
            // setup bitmap reuse options.
            mBitmapOptions?.inBitmap = mBitmap
            mBitmapOptions?.inMutable = true
            mBitmapOptions?.inSampleSize = 1
        }
    }

    private fun getNext(): Int {
        mIndex++
        if (mIndex >= mFrames.size) {
            mIndex = 0
            mCycledCount++
            mOnAnimationUpdateListener?.onAnimationCycled(
                mSoftReferenceImageView?.get(),
                mCycledCount
            )
        }
        return mFrames[mIndex]
    }

    fun setIsResetWhenStart(resetWhenStart: Boolean): FramesSequenceAnimation? {
        mIsResetWhenStart = resetWhenStart
        return this
    }

    @Synchronized
    fun start() {
        mShouldRun = true
        if (mIsResetWhenStart) {
            mIndex = 0
            mCycledCount = 0
        }
        if (mIsRunning) return

        var runnable = object : Runnable {
            override
            fun run() {
                val imageView = mSoftReferenceImageView?.get()
                if (!mShouldRun || imageView == null) {
                    mIsRunning = false
                    mOnAnimationStoppedListener?.onAnimationStopped()
                    return
                }

                mIsRunning = true
                mHandler?.postDelayed(this, mDelayMillis)

                if (imageView.isShown) {
                    val imageRes: Int = getNext()
                    mOnAnimationUpdateListener?.onAnimationUpdate(
                        imageView,
                        mIndex,
                        mCycledCount
                    )
                    if (mBitmap != null) { // so Build.VERSION.SDK_INT >= 11
                        var bitmap: Bitmap? = null
                        try {
                            bitmap = BitmapFactory.decodeResource(
                                imageView.resources,
                                imageRes,
                                mBitmapOptions
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                        } else {
                            imageView.setImageResource(imageRes)
                            mBitmap?.recycle()
                            mBitmap = null
                        }
                    } else {
                        imageView.setImageResource(imageRes)
                    }
                }

            }
        }
        mHandler?.post(runnable)
    }

    fun setOnAnimationUpdateListener(onAnimationUpdateListener: OnAnimationUpdateListener?) {
        mOnAnimationUpdateListener = onAnimationUpdateListener
    }

    /**
     * Stops the animation
     */
    @Synchronized
    fun stop() {
        mShouldRun = false
    }

    interface OnAnimationStoppedListener {
        fun onAnimationStopped()
    }

    interface OnAnimationUpdateListener {
        fun onAnimationUpdate(
            imageView: ImageView?,
            frame: Int,
            cycledCount: Int
        )

        fun onAnimationCycled(
            imageView: ImageView?,
            cycledCount: Int
        )
    }
}