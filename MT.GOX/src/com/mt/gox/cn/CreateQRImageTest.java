package com.mt.gox.cn;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
/**
 * @类功能说明: 生成二维码图片示例
 * @时间:2013-4-18上午11:10:42
 * @版本:V1.0
 */
public class CreateQRImageTest
{
	private ImageView sweepIV;
	private int QR_WIDTH = 200, QR_HEIGHT = 200;
	/**
	 * @方法功能说明: 生成二维码图片,实际使用时要初始化sweepIV,不然会报空指针错误
	 * @作者:饶正勇
	 * @时间:2013-4-18上午11:14:16
	 * @参数: @param url 要转换的地址或字符串,可以是中文
	 * @return void
	 * @throws
	 */
	public void createQRImage(String url)
	{
		try
		{
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			sweepIV.setImageBitmap(bitmap);
		}
		catch (WriterException e)
		{
			e.printStackTrace();
		}
	}
}
