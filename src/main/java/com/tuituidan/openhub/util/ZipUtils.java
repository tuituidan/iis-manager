package com.tuituidan.openhub.util;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import lombok.experimental.UtilityClass;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.springframework.util.Assert;

/**
 * ZipUtils.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2021/3/8
 */
@UtilityClass
public class ZipUtils {

    private static final Charset CHARSET_GBK = Charset.forName("GBK");

    /**
     * 解压文件
     *
     * @param distPath distPath
     * @param inputStream inputStream
     */
    public static void unzip(InputStream inputStream, String distPath) {
        try {
            String zipPath = distPath + File.separator + StringExtUtils.getUuid() + ".zip";
            FileUtils.copyInputStreamToFile(inputStream, new File(zipPath));
            try (ZipFile zipFile = new ZipFile(zipPath)) {
                zipFile.setCharset(CHARSET_GBK);
                Assert.isTrue(zipFile.isValidZipFile(), "请上传ZIP文件");
                zipFile.extractAll(distPath);
            }
            FileUtils.forceDelete(new File(zipPath));
        } catch (Exception ex) {
            throw new IllegalArgumentException("解压失败", ex);
        }

    }

}
