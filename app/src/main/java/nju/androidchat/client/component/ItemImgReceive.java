package nju.androidchat.client.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import nju.androidchat.client.R;

public class ItemImgReceive extends LinearLayout {


    @StyleableRes
    int index0 = 0;

    private ImageView imageView;
    private Context context;
    private UUID messageId;
    private OnRecallMessageRequested onRecallMessageRequested;
    private String URL;

    public ItemImgReceive(Context context, String URL, UUID messageId) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_img_receive, this);
        this.imageView = findViewById(R.id.chat_item_content_img);
        this.messageId = messageId;
        setImgURL(URL);
    }

    public String getURL() {
        return this.URL;
    }

    public void setImgURL(String URL) {
        this.URL = URL;
        setURLimage(URL);
    }

    public void setURLimage(String url) {
        Glide.with(context).load(url).into(this.imageView);

//        new Thread() {
//            @Override
//            public void run() {
//                Bitmap bmp = null;
//                try {
//                    java.net.URL myurl = new URL(url);
//                    // 获得连接
//                    HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
//                    conn.setConnectTimeout(6000);//设置超时
//                    conn.setDoInput(true);
//                    conn.setUseCaches(false);//不缓存
//                    conn.connect();
//                    InputStream is = conn.getInputStream();//获得图片的数据流
//                    bmp = BitmapFactory.decodeStream(is);
//                    is.close();
//                    imageView.setImageBitmap(bmp);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();

    }

}
