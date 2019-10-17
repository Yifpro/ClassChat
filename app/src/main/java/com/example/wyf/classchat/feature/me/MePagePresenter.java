package com.example.wyf.classchat.feature.me;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.util.BitmapUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hyphenate.chat.EMClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator on 2018/1/21/021.
 */
public class MePagePresenter implements MePageContract.Presenter {

    private Activity activity;
    private MePageContract.View view;

    public MePagePresenter(Activity activity, MePageContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    /**
     * 生成二维码
     */
    public void generateQr() {
        String userId = EMClient.getInstance().getCurrentUser();
        Bitmap tempBitmap = createQRCode(EMClient.getInstance().getCurrentUser());
        Bitmap logoBitmap = BitmapFactory.decodeFile(BitmapUtils.getPath(FileType.CONTACT, userId, userId));
        if (!BitmapUtils.isExist(FileType.CONTACT, userId, userId) && logoBitmap == null) {
            logoBitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_classchat);
        }
        Bitmap qrbitmap = addLogo(tempBitmap, logoBitmap);
        view.displayQr(qrbitmap);
    }

    /**
     * 用于生成qr
     *
     * @param content 用于生成的内容
     * @return 二维码图片
     */
    private Bitmap createQRCode(String content) {
        int width = 600;
        int height = 600;
        QRCodeWriter codeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> maps = new HashMap<>();
        maps.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = codeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, maps);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 给二维码增加logo
     *
     * @param qrBitmap   二维码
     * @param logoBitmap logo
     * @return 加过logo的二维码
     */
    private Bitmap addLogo(Bitmap qrBitmap, Bitmap logoBitmap) {
        int qrBitmapWidth = qrBitmap.getWidth();
        int qrBitmapHeight = qrBitmap.getHeight();
        int logoBitmapWidth = logoBitmap.getWidth();
        int logoBitmapHeight = logoBitmap.getHeight();
        Bitmap blankBitmap = Bitmap.createBitmap(qrBitmapWidth, qrBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blankBitmap);
        canvas.drawBitmap(qrBitmap, 0, 0, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        float scaleSize = 1.0f;
        while ((logoBitmapWidth / scaleSize) > (qrBitmapWidth / 5) || (logoBitmapHeight / scaleSize) > (qrBitmapHeight / 5)) {
            scaleSize *= 2;
        }
        float sx = 1.0f / scaleSize;
        canvas.scale(sx, sx, qrBitmapWidth / 2, qrBitmapHeight / 2);
        canvas.drawBitmap(logoBitmap, (qrBitmapWidth - logoBitmapWidth) / 2, (qrBitmapHeight - logoBitmapHeight) / 2, null);
        canvas.restore();
        return blankBitmap;
    }
}
