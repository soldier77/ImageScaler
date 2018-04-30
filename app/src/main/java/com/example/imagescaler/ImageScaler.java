package com.example.imagescaler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 图片缩放器
 */

public class ImageScaler extends View {
    private Canvas mcanvas;
    private Bitmap bitmap;
    private int bitmapId;
    private int mWidth,mHeight;
    private Matrix mMatrix;
    public ImageScaler(Context context,AttributeSet attrs){
        super(context,attrs);
        getAttrs(context,attrs);
        init();
    }

    private void getAttrs(Context context,AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.ImageScaler);
        bitmapId = ta.getResourceId(R.styleable.ImageScaler_Bitmap,0);
        ta.recycle();
    }
    private void init(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        bitmap = BitmapFactory.decodeResource(getResources(),bitmapId,options);
        int bytes = bitmap.getAllocationByteCount();
        Log.d("Imagewidth",String.valueOf(bitmap.getWidth()));
        Log.d("ImageHeight",String.valueOf(bitmap.getHeight()));
        Log.d("ImageMemory",String.valueOf(bytes));
        mMatrix = new Matrix();
        if(bitmap!=null){
        }else Log.d("ImageScaler_init","No Bitmap");

    }

    final private int MOVE = 0;//单指下的移动模式
    final private int SCALE = 1;//双指下的缩放和移动模式
    final private int NONE = 2;//没有模式
    private int mode;//图片单双指下的变化模式
    private float newspac,oldspac;
    float cx,cy;//两指中点的xy坐标；
    float px1,py1,bx1,by1;
    @Override
    public boolean onTouchEvent(MotionEvent event){
//        System.out.println("ActionIndex:"+event.getActionIndex());
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = SCALE;
                oldspac = Spacing(event);
                break;
            case MotionEvent.ACTION_DOWN:
                px1 = event.getX(0);
                py1 = event.getY(0);
                mode = MOVE;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mode==MOVE){
                    bx1 = event.getX(0);
                    by1 = event.getY(0);
                    System.out.println("bx:"+bx1+" by:"+by1+" px:"+px1+" py:"+py1);
                    mMatrix.postTranslate(bx1-px1,by1-py1);
                    px1 = bx1;
                    py1 = by1;
                }else if(mode==SCALE) {
                    newspac = Spacing(event);
                    final float scale = newspac/oldspac;
                    cx = (event.getX(0)+event.getX(1))/2;
                    cy = (event.getY(0)+event.getY(1))/2;
                    mMatrix.postScale(scale,scale,cx,cy);
                    oldspac = newspac;
                }if(mode==NONE){
                px1 = event.getX(0);
                py1 = event.getY(0);
                mode = MOVE;
            }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;
        }
        invalidate();
        return true;
    }
    private float Spacing(MotionEvent event){
        float x=event.getX(0)-event.getX(1),
                y=event.getY(0)-event.getY(1);
        return (float)Math.sqrt(x*x+y*y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null){
            canvas.drawBitmap(bitmap,mMatrix,null);
        }else
        super.onDraw(canvas);
    }
    /**
     * 宽高测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = measureWidth(widthMeasureSpec);
        mHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
    }
    private int measureWidth(int measureSpec){
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            if(specMode == MeasureSpec.AT_MOST){
                result = 200;
                result = Math.min(result,specSize);
            }
        }
        return result;
    }
    private int measureHeight(int measureSpec){
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            if(specMode == MeasureSpec.AT_MOST){
                result = 300;
                result = Math.min(result,specSize);
            }
        }
        return result;
    }
}
