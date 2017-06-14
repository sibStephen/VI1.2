package college.root.vi12;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class TestActivity extends AppCompatActivity {

    ImageView imgBack;
    Bitmap bitmapOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_test);

//        bitmapOriginal = BitmapFactory.decodeResource(getResources(),
  //              R.drawable.vilogo);
        imgBack = (ImageView)findViewById(R.id.imgBackground);
        imgBack.setImageBitmap(createBitmap_ScriptIntrinsicBlur(bitmapOriginal , 20.0f));



    }



    private Bitmap createBitmap_ScriptIntrinsicBlur(Bitmap src, float r) {

        //Radius range (0 < r <= 25)
        if(r <= 0){
            r = 0.1f;
        }else if(r > 25){
            r = 25.0f;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(this);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, src);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(r);
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();
        return bitmap;
    }
}
