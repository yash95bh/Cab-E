package com.eviort.cabedriver.swipebtn;

import android.view.MotionEvent;
import android.view.View;

final class TouchUtils {
    private TouchUtils() {
    }

    static boolean isTouchOutsideInitialPosition(MotionEvent event, View view) {
        return event.getX() > view.getX() + view.getWidth();
    }
}
