package link.standen.michael.slideshow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PoseImageView extends android.support.v7.widget.AppCompatImageView {
    public PoseImageView(Context context) {
        super(context);
    }
    public PoseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PoseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.BLACK);
        super.onDraw(canvas);

        canvas.drawLine(0, 0, 100, 100, p);

        canvas.drawLine(0, 0, 20, 20, p);
        canvas.drawLine(20, 0, 0, 20, p);

    }
}
