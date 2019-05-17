package studios.devs.mobi.ui.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapePath

/**
 * Forked from Morph-Navigation-View by Tommy Buonomo
 * Using and configuring this version as it best suits our needs.
 *
 * Changes in this file:
 *      # No changes were made.
 *
 * Copyright 2018 Tommy Buonomo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class MorphBottomNavigationViewTopEdgeTreatment(private val bottomNavigationMenuView: BottomNavigationMenuView,
                                                var morphItemRadius: Float,
                                                var morphVerticalOffset: Float,
                                                var morphCornerRadius: Float) :
        EdgeTreatment() {

    lateinit var easyShapePath: MagicShapePath

    var lastSelectedItem: Int = 0
    var selectedItem: Int = 0

    override fun getEdgePath(length: Float, interpolation: Float, shapePath: ShapePath) {
        easyShapePath = MagicShapePath.create(0f, morphVerticalOffset, length, morphVerticalOffset)

        bottomNavigationMenuView.forEachIndexed { i, view ->
            var morphHeightOffset = 0f

            //Draw only selected and last selected path
            if (view.isVisible && (i == selectedItem || i == lastSelectedItem)) {
                if (i == selectedItem) {
                    morphHeightOffset = interpolation * morphVerticalOffset
                } else if (i == lastSelectedItem) {
                    morphHeightOffset = (1 - interpolation) * morphVerticalOffset
                }

                val itemRect = view.globalVisibleRect

                val centerRadius = morphItemRadius
                val borderRadius = morphCornerRadius
                val centerX = itemRect.centerX().toFloat()
                val centerY = morphVerticalOffset + centerRadius - morphHeightOffset

                val centerCircle = MagicShapePath.CircleShape(centerX, centerY, centerRadius, MagicShapePath.PathDirection.CLOCKWISE)

                val leftCircle = MagicShapePath.CircleShape(centerX, morphVerticalOffset - borderRadius, borderRadius, MagicShapePath.PathDirection.C_CLOCKWISE)
                centerCircle.shiftOutside(leftCircle, MagicShapePath.ShiftMode.LEFT)

                val rightCircle = MagicShapePath.CircleShape(centerX, morphVerticalOffset - borderRadius, borderRadius, MagicShapePath.PathDirection.C_CLOCKWISE)
                centerCircle.shiftOutside(rightCircle, MagicShapePath.ShiftMode.RIGHT)

                easyShapePath.addCircles(leftCircle, centerCircle, rightCircle)
            }
        }


        easyShapePath.applyOn(shapePath)
    }

    fun drawDebug(canvas: Canvas, paint: Paint) {
        easyShapePath.drawDebug(canvas, paint)
    }

    private inline val View.globalVisibleRect: Rect
        get() {
            val r = Rect()
            getGlobalVisibleRect(r)
            return r
        }

}
