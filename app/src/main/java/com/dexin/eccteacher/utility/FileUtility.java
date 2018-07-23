package com.dexin.eccteacher.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 文件工具
 */
public final class FileUtility {
    private static final String TAG = "TAG_FileUtility";

    /**
     * Bitmap保存为文件的方法,指定路径的话就按照指定的路径保存,否则保存到SD卡应用缓存目录下
     *
     * @param context   Context对象
     * @param bitmap    Bitmap对象
     * @param parentDir 存储目录
     * @param fileName  文件名
     * @param quality   保存质量
     * @return 保存后的文件对象
     */
    @Nullable
    public static File saveBitmapToFile(Context context, Bitmap bitmap, String parentDir, String fileName, int quality) {
        FileOutputStream fileOutputStream = null;
        try {
            if (bitmap != null) {
                File file = (TextUtils.isEmpty(parentDir) || TextUtils.isEmpty(fileName)) ? new File(context.getExternalCacheDir(), "tempFile.jpg") : new File(parentDir, fileName);
                if (file.exists()) file.delete();
                fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
                fileOutputStream.flush();
                bitmap.recycle();
                return file;
            } else {
                return null;
            }
        } catch (Exception e) {
            LogUtility.e(TAG, "saveBitmapToFile: ", e);
            return null;
        } finally {
            try {
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (Exception e) {
                LogUtility.e(TAG, "saveBitmapToFile: ", e);
            }
        }
    }
}
