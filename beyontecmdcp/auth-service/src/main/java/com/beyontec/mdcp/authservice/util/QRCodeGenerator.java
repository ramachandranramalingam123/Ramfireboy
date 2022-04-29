package com.beyontec.mdcp.authservice.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.codec.binary.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {
    public static final String QR_CODE_IMAGE_PATH = "classpath:images";

    public static String generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
    	
    	
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        File f = new File(filePath);		//change path of image according to you
		FileInputStream fis = new FileInputStream(f);
		byte byteArray[] = new byte[(int)f.length()];
		fis.read(byteArray);
		String imageString = Base64.encodeBase64String(byteArray);
		
       path.toFile().delete();
       return  imageString;
        
    }
    
    public static String getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		byte[] pngData = pngOutputStream.toByteArray();
		String imageString = Base64.encodeBase64String(pngData);

		// path.toFile().delete();
		return imageString;
	}



}