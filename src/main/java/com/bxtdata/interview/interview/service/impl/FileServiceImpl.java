package com.bxtdata.interview.interview.service.impl;

import com.bxtdata.interview.interview.mapper.BreakRecordMapper;
import com.bxtdata.interview.interview.pojo.body.UploadingBody;
import com.bxtdata.interview.interview.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private BreakRecordMapper breakRecordMapper;

    private static void addWatermark(BufferedImage image) {
        // 创建一个图形上下文
        Graphics2D g2d = image.createGraphics();
        // 设置水印文本
        String watermarkText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 设置水印字体和颜色
        Font font = new Font("Arial", Font.BOLD, 24);
        g2d.setFont(font);
        g2d.setColor(Color.RED);
        // 获取水印文本的尺寸
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(watermarkText);
        int textHeight = fm.getHeight();
        // 计算水印位置（在右下角）
        int x = image.getWidth() - textWidth - 10;
        int y = image.getHeight() - textHeight - 10;
        // 绘制水印文本
        g2d.drawString(watermarkText, x, y);

        // 释放图形上下文
        g2d.dispose();
    }

    private static String extractFileType(String base64Image) {
        int delimiterIndex = base64Image.indexOf(";");
        if (delimiterIndex != -1) {
            String prefix = base64Image.substring(0, delimiterIndex);
            int slashIndex = prefix.indexOf("/");
            if (slashIndex != -1) {
                return prefix.substring(slashIndex + 1);
            }
        }
        return null;
    }

    private static String extractFileString(String base64Image) {
        return base64Image.substring(base64Image.indexOf(",") + 1);
    }

    @Override
    public String saveBase64Image(UploadingBody body) {
        //上传文件
        byte[] decodedBytes = Base64.getDecoder().decode(extractFileString(body.getImage())); //获得base64图片数据部分
        String suffix = extractFileType(body.getImage()); //文件类型
        if (suffix == null) throw new RuntimeException("文件格式异常");
        String fileName = body.getId() + "." + suffix; //文件名
        try {
            String filePath = ResourceUtils.getURL("classpath:static").getPath() + "/" + fileName;
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            //打水印
            addWatermark(image);
            //保存文件
            File file = new File(filePath);
            ImageIO.write(image, suffix, file);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //更新状态
        breakRecordMapper.updateStateById(body.getId(), 1);
        return fileName;
    }
}
