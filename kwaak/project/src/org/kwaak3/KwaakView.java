/*
 * Kwaak3
 * Copyright (C) 2010 Roderick Colenbrander
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.kwaak3;

import java.io.IOException;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

class KwaakView extends GLSurfaceView {
	private KwaakRenderer mKwaakRenderer;
	
	public KwaakView(Context context){
		super(context);
		
		/* We need the path to the library directory for dlopen in our JNI library */
		String cache_dir, lib_dir;
		try {
			cache_dir = context.getCacheDir().getCanonicalPath();
			lib_dir = cache_dir.replace("cache", "lib");
		} catch (IOException e) {
			e.printStackTrace();
			lib_dir = "/data/data/org.kwaak3/lib";
		}
		KwaakJNI.setLibraryDirectory(lib_dir);
		
		mKwaakRenderer = new KwaakRenderer();
		setRenderer(mKwaakRenderer);

		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int qKeyCode = androidKeyCodeToQuake(keyCode, event);
		//Log.d("Quake_JAVA", "onKeyDown=" + keyCode + " " + qKeyCode + " " + event.getDisplayLabel() + " " + event.getUnicodeChar() + " " + event.getNumber());
		return queueKeyEvent(qKeyCode, 1);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int qKeyCode = androidKeyCodeToQuake(keyCode, event);
		//Log.d("Quake_JAVA", "onKeyUp=" + keyCode + " " + qKeyCode + " shift=" + event.isShiftPressed() + " =" + event.getMetaState());
		return queueKeyEvent(qKeyCode, 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Log.d("Quake_JAVA", "onTouchEvent action=" + event.getAction() + " x=" + event.getX() + " y=" + event.getY() + " pressure=" + event.getPressure() + "size = " + event.getSize());
		/* Perhaps we should pass integers down to reduce the number of float computations */
		return queueMotionEvent(event.getAction(), event.getX(), event.getY(), event.getPressure());
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		//Log.d("Quake_JAVA", "onTrackballEvent action=" + event.getAction() + " x=" + event.getX() + " y=" + event.getY());
		return queueTrackballEvent(event.getAction(), event.getX(), event.getY());
	}

	public boolean queueKeyEvent(final int qKeyCode, final int state)
	{
		if(qKeyCode == 0) return true;

		/* Make sure all communication with Quake is done from the Renderer thread */
        queueEvent(new Runnable(){
            public void run() {
        		KwaakJNI.queueKeyEvent(qKeyCode, state);
            }});
        return true;
	}

	private boolean queueMotionEvent(final int action, final float x, final float y, final float pressure)
	{
		/* Make sure all communication with Quake is done from the Renderer thread */
        queueEvent(new Runnable(){
            public void run() {
        		KwaakJNI.queueMotionEvent(action, x, y, pressure);
            }});
        return true;
	}
	
	private boolean queueTrackballEvent(final int action, final float x, final float y)
	{
		/* Make sure all communication with Quake is done from the Renderer thread */
        queueEvent(new Runnable(){
            public void run() {
        		KwaakJNI.queueTrackballEvent(action, x, y);
            }});
        return true;
	}

	private static final int QK_ENTER = 13;
	private static final int QK_ESCAPE = 27;
	private static final int QK_BACKSPACE = 127;
	private static final int QK_LEFT = 134;
	private static final int QK_RIGHT = 135;
	private static final int QK_UP = 132;
	private static final int QK_DOWN = 133;
	private static final int QK_CTRL = 137;
	private static final int QK_SHIFT = 138;
	private static final int QK_CONSOLE = 340;

	private int androidKeyCodeToQuake(int aKeyCode, KeyEvent event)
	{	
		//TODO: fix backspace
		/* Convert non-ASCII keys by hand */
		switch(aKeyCode)
		{
			case KeyEvent.KEYCODE_DPAD_UP:
				return QK_UP;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				return QK_DOWN;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				return QK_LEFT;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				return QK_RIGHT;
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				return QK_ENTER;
			case KeyEvent.KEYCODE_SEARCH:
				return QK_CONSOLE;
			case KeyEvent.KEYCODE_BACK:
				return QK_ESCAPE;
			case KeyEvent.KEYCODE_DEL:
				return QK_BACKSPACE;
			case KeyEvent.KEYCODE_ALT_LEFT:
				return QK_CTRL;
			case KeyEvent.KEYCODE_SHIFT_LEFT:
				return QK_SHIFT;
		}
		
		/* Let Android do all the character conversion for us. This way we don't have
		 * to care about modifier keys and specific keyboard layouts.
		 * TODO: add some more filtering
		 */
		int uchar = event.getUnicodeChar();
		if(uchar < 127)
			return uchar;

		return 0;
	}
}
