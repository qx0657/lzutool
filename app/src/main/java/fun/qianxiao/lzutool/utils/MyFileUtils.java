package fun.qianxiao.lzutool.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MyFileUtils {
    /**
     * 文件分块工具
     * @param offset 起始偏移位置
     * @param file 文件
     * @param blockSize 分块大小
     * @return 分块数据
     */

    public static byte[] getBlock(long offset, File file, int blockSize) {

        byte[] result = new byte[blockSize];

        RandomAccessFile accessFile = null;

        try {

            accessFile = new RandomAccessFile(file, "r");

            accessFile.seek(offset);

            int readSize = accessFile.read(result);

            if (readSize == -1) {

                return null;

            } else if (readSize == blockSize) {

                return result;

            } else {

                byte[] tmpByte = new byte[readSize];

                System.arraycopy(result, 0, tmpByte, 0, readSize);

                return tmpByte;

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (accessFile != null) {

                try {

                    accessFile.close();

                } catch (IOException e1) {

                }

            }

        }

        return null;

    }
}
