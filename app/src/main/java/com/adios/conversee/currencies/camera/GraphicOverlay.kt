/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adios.conversee.currencies.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Size
import android.view.View
import androidx.camera.core.CameraSelector
import com.adios.conversee.currencies.camera.GraphicOverlay.Graphic

/**
 * A view which renders a series of custom graphics to be overlaid on top of an associated preview
 * (i.e., the camera preview).  The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.
 *
 *
 *
 * Supports scaling and mirroring of the graphics relative the camera's preview properties.  The
 * idea is that detection items are expressed in terms of a preview size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.
 *
 *
 *
 * Associated [Graphic] items should use the following methods to convert to view coordinates
 * for the graphics that are drawn:
 *
 *  1. [Graphic.scaleX] and [Graphic.scaleY] adjust the size of the
 * supplied value from the preview scale to the view scale.
 *  1. [Graphic.translateX] and [Graphic.translateY] adjust the coordinate
 * from the preview's coordinate system to the view coordinate system.
 *
 */
class GraphicOverlay<T : Graphic>(context: Context, attrs: AttributeSet) :
    View(context, attrs) {
    private val lock = Any()
    private var widthScaleFactor = 1.0f
    private var heightScaleFactor = 1.0f
    private var facing = CameraSelector.LENS_FACING_BACK
    private val graphics = HashSet<T>()

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay.  Subclass
     * this and implement the [Graphic.draw] method to define the
     * graphics element.  Add instances to the overlay using [GraphicOverlay.add].
     */
    abstract class Graphic(private val mOverlay: GraphicOverlay<*>) {
        abstract val value: Double
        abstract val boundingBox: Rect
        var activeTime = System.currentTimeMillis()

        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         *
         *  1. [Graphic.scaleX] and [Graphic.scaleY] adjust the size of
         * the supplied value from the preview scale to the view scale.
         *  1. [Graphic.translateX] and [Graphic.translateY] adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.
         *
         *
         * @param canvas drawing canvas
         */
        abstract fun draw(canvas: Canvas)

        // Returns true if the supplied coordinates are within this graphic.
        abstract fun contains(x: Float, y: Float): Boolean

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        fun scaleX(horizontal: Float): Float {
            return horizontal * mOverlay.widthScaleFactor
        }

        // Adjusts a vertical value of the supplied value from the preview scale to the view scale.
        fun scaleY(vertical: Float): Float {
            return vertical * mOverlay.heightScaleFactor
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        fun translateX(x: Float): Float {
            return if (mOverlay.facing == CameraSelector.LENS_FACING_FRONT) {
                mOverlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        fun translateY(y: Float): Float {
            return scaleY(y)
        }

        /**
         * Returns a RectF in which the left and right parameters of the provided Rect are adjusted
         * by translateX, and the top and bottom are adjusted by translateY.
         */
        fun translateRect(inputRect: RectF): RectF {
            val returnRect = RectF()

            returnRect.left = translateX(inputRect.left)
            returnRect.top = translateY(inputRect.top)
            returnRect.right = translateX(inputRect.right)
            returnRect.bottom = translateY(inputRect.bottom)

            return returnRect
        }

        fun setScaleFactor(bufferSize: Size) {
            mOverlay.widthScaleFactor = mOverlay.width.toFloat() / bufferSize.height.toFloat()
            mOverlay.heightScaleFactor = mOverlay.height.toFloat() / bufferSize.width.toFloat()
        }

        abstract fun moveToNewPosition(newBoundingBox: Rect)

        fun postInvalidate() {
            mOverlay.postInvalidate()
        }
    }

    // Removes all graphics from the overlay.
    fun clear() {
        synchronized(lock) {
            graphics.clear()
        }
        postInvalidate()
    }

    // Adds a graphic to the overlay.
    fun add(graphic: T) {
        graphics
            .filter { it.value == graphic.value }
            .getOrNull(0)
            ?.moveToNewPosition(graphic.boundingBox)
            ?: run {
                synchronized(lock) {
                    graphics.add(graphic)
                }
                postInvalidate()
            }
    }

    // Removes a graphic from the overlay.
    fun remove(graphic: T) {
        synchronized(lock) {
            graphics.remove(graphic)
        }
        postInvalidate()
    }

    /**
     * Returns the first graphic, if any, that exists at the provided absolute screen coordinates.
     * These coordinates will be offset by the relative screen position of this view.
     * @return First graphic containing the point, or null if no text is detected.
     */
    fun getGraphicAtLocation(rawX: Float, rawY: Float): T? {
        synchronized(lock) {
            // Get the position of this View so the raw location can be offset relative to the view.
            val location = IntArray(2)
            this.getLocationOnScreen(location)
            for (graphic in graphics) {
                if (graphic.contains(rawX - location[0], rawY - location[1])) {
                    return graphic
                }
            }
            return null
        }
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    fun setCameraFacing(facing: Int) {
        synchronized(lock) {
            this.facing = facing
        }
        postInvalidate()
    }

    // Draws the overlay with its associated graphic objects.
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock) {
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }

    fun clearOld() {
        graphics.filter { it.activeTime + 200 < System.currentTimeMillis() }.forEach {
            synchronized(lock) {
                graphics.remove(it)
            }
        }
        postInvalidate()
    }
}
