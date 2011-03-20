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
import android.view.KeyEvent;
import android.view.MotionEvent;

class KwaakView extends GLSurfaceView {
    private KwaakRenderer mKwaakRenderer;
    private int mBaseX = 0;
    private int mBaseY = 0;
    private short pressedKeys[] = null;
    private byte movementFingerIdx = -1;
    private boolean menuPressed = false;
    private boolean searchPressed = false;
    
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
        KwaakJNI.setGameDirectory("/mnt/sdcard/quake3");
        KwaakJNI.setLibraryDirectory(lib_dir);
        
        
        mKwaakRenderer = new KwaakRenderer();
        setRenderer(mKwaakRenderer);

        setFocusable(true);
        setFocusableInTouchMode(true);
        pressedKeys = new short[2];
        pressedKeys[0] = 0;
        pressedKeys[1] = 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int qKeyCode = androidKeyCodeToQuake(keyCode, event);
        //Log.d("Quake_JAVA", "onKeyDown=" + keyCode + " " + qKyCode + " " + event.getDisplayLabel() + " " + event.getUnicodeChar() + " " + event.getNumber());
        if(keyCode == KeyEvent.KEYCODE_SEARCH)
            searchPressed = true;
        else if(searchPressed && keyCode == KeyEvent.KEYCODE_MENU)
            menuPressed = true;
        else if((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) && menuPressed)
            return super.onKeyDown(keyCode, event);    
        return queueKeyEvent(qKeyCode, 1);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int qKeyCode = androidKeyCodeToQuake(keyCode, event);
        //Log.d("Quake_JAVA", "onKeyUp=" + keyCode + " " + qKeyCode + " shift=" + event.isShiftPressed() + " =" + event.getMetaState());
        if(keyCode == KeyEvent.KEYCODE_SEARCH)
            searchPressed = false;
        else if((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) && menuPressed)
        {
            menuPressed = false;
            return super.onKeyUp(keyCode, event);
        }
        return queueKeyEvent(qKeyCode, 0);
    }
    
    private int fabs(int i)
    {
        if(i < 0)
            return -i;
        return i;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d("Quake_JAVA", "onTouchEvent action=" + event.getAction() + " x=" + event.getX() + " y=" + event.getY() + " pressure=" + event.getPressure() + "size = " + event.getSize());
        /* Perhaps we should pass integers down to reduce the number of float computations */
        int pointerCount = event.getPointerCount();
        final int action = (event.getActionMasked() & MotionEvent.ACTION_MASK);
        
        for (int i = 0; i < pointerCount; i++)
        {
            int x = (int) event.getX(i);
            int y = (int) event.getY(i);
            if(!searchPressed && x > 240 && (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN)
            {
                mBaseX = x;
                mBaseY = y;
                movementFingerIdx = (byte) event.getPointerId(i);
            }
            else if(!searchPressed && mBaseX != 0 && x > 225 && event.getPointerId(i) == movementFingerIdx 
                    && (action & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_POINTER_UP)
            {
                if((action & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_UP)
                {
                    if(KwaakJNI.calculateMotion(mBaseX, mBaseY, x, y, pressedKeys) == 0)
                        return true;
                }
                else
                    movementFingerUp();
            }
            else if((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP)
            {
                if (event.getPointerId(event.getActionIndex()) == movementFingerIdx)
                    movementFingerUp();
                else
                    queueMotionEvent((action & MotionEvent.ACTION_MASK), x, y, event.getPressure());
            }
            else
                queueMotionEvent((action & MotionEvent.ACTION_MASK), x, y, event.getPressure());
        }
        return true;
    }

    private void movementFingerUp()
    {
        mBaseX = 0;
        mBaseY = 0;
        movementFingerIdx = -1;
        for(byte z = 0; z < 2; ++z)
        {
            if(pressedKeys[z] != 0)
            {
                queueKeyEvent(pressedKeys[z], 0);
                pressedKeys[z] = 0;
            }
        }
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

    private static final short QK_ENTER = 13;
    private static final short QK_ESCAPE = 27;
    private static final short QK_BACKSPACE = 127;
    private static final short QK_LEFT = 134;
    private static final short QK_RIGHT = 135;
    private static final short QK_UP = 132;
    private static final short QK_DOWN = 133;
    private static final short QK_CTRL = 137;
    private static final short QK_SHIFT = 138;
    //private static final int QK_CONSOLE = 340;

    private static final short QK_F1 = 145;
    private static final short QK_F2 = 146;
    private static final short QK_F3 = 147;
    //private static final int QK_F4 = 148;
    
    private short androidKeyCodeToQuake(int aKeyCode, KeyEvent event)
    {    
        /* Convert non-ASCII keys by hand */
        switch(aKeyCode)
        {
            /* For now map the focus buttons to F1 and let the user remap it in game.
             * This should allow some basic movement on the Nexus One if people map it to forward.
             * At least on the Milestone the camera button itself is shared with the Focus one. You have
             * to press focus first and then you hit camera, this leads to the following event sequence which
             * I don't handle right now: focus_down -> camera_down -> camera_up -> focus_up.
             */
            case KeyEvent.KEYCODE_FOCUS:
                return QK_F1;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return QK_F2;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return QK_F3;
            case KeyEvent.KEYCODE_DPAD_UP:
                return QK_UP;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return QK_DOWN;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return QK_LEFT;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return QK_RIGHT;
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                /* Center is useful for shooting if you only use the keyboard */
                return QK_CTRL;
            case KeyEvent.KEYCODE_ENTER:
                return QK_ENTER;
            //case KeyEvent.KEYCODE_SEARCH:
            //    return QK_CONSOLE;
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
        short uchar = (short) event.getUnicodeChar();
        if(uchar < 127)
            return uchar;

        return 0;
    }
}
