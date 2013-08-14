package com.mt.gox.cn.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class ViewDragableSpace extends ScrollLayout {

	public ViewDragableSpace(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewDragableSpace(Context context) {
		super(context);
	}

	public ViewDragableSpace(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean IsCanMove(int deltaX) {
		/*if (deltaX > 0 && mCurScreen == getChildCount()-1) {
			mOnViewChangeListener.onViewChanged(mCurScreen+1);
			return false;
		}*/
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}

			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			if (IsCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}
				mLastMotionX = x;
				scrollBy(deltaX, 0);
			}

			break;

		case MotionEvent.ACTION_UP:

			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}

			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					/*&& mCurScreen < getChildCount() - 1*/) {
				// Fling enough to move right
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}
	
	@Override
	public void snapToDestination() {
		final int screenWidth = getWidth();

		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}
	
	@Override
	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();

			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);

			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout

			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.onViewChanged(mCurScreen);
			}
		}
	}
	
}
