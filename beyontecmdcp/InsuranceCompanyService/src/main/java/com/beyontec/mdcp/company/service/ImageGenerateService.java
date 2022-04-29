package com.beyontec.mdcp.company.service;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.beyontec.mdcp.company.model.CertificateDetails;
import com.beyontec.mdcp.company.model.User;
import com.beyontec.mdcp.company.repo.UserRepo;
import com.beyontec.mdcp.company.util.InsuranceCompanyConstants;
import com.lowagie.text.pdf.codec.Base64;

@Service
public class ImageGenerateService {

	@Autowired
	private CertificateService certificateService;

	@Autowired
	private UserRepo userRepo;

	public byte[] GenerateImage(CertificateDetails certificateDetail) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm a");

		String certificateBase64 = null;
		try {
			Resource resource = new ClassPathResource("/certificateBase64.txt");
			certificateBase64 = IOUtils.toString(resource.getInputStream(), "UTF-8");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		byte[] imageByteBg;

		imageByteBg = Base64.decode(certificateBase64);
		ByteArrayInputStream bisBg = new ByteArrayInputStream(imageByteBg);
		// read the resource image
		BufferedImage getImage = null;
		try {
			getImage = ImageIO.read(bisBg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String qrCodeBase64 = certificateService.getQrBase(certificateDetail.getCertificateSerialNumber());

		BufferedImage qrImage = null;
		byte[] imageByte;

		imageByte = Base64.decode(qrCodeBase64);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		try {
			qrImage = ImageIO.read(bis);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// set width, height and type
		BufferedImage bufferedImage = new BufferedImage(745, 750, getImage.getType());

		// write on image using graphics
		Graphics2D graphics2D = bufferedImage.createGraphics();

		graphics2D.drawImage(getImage, 0, 0, getImage.getWidth(), getImage.getHeight(), null);

		graphics2D.drawImage(qrImage, 17, 14, 198, 188, null);
		graphics2D.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 34));
		graphics2D.setColor(Color.black);
		graphics2D.drawString(certificateDetail.getCertificateSerialNumber(), 310, 207);

		graphics2D.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
		graphics2D.setColor(Color.black);

		graphics2D.drawString(certificateDetail.getInsured(), 235, 245);
		graphics2D.drawString(certificateDetail.getPolicyNumber(), 190, 287);
		String commencingDate = formatter.format(certificateDetail.getCommencingDate());
		String expiryDate = formatter.format(certificateDetail.getExpiryDate());
		String commencingTime = formatterTime.format(certificateDetail.getCommencingDate());
		graphics2D.drawString(commencingDate, 250, 330);
		graphics2D.drawString(commencingTime, 610, 330);
		graphics2D.drawString(expiryDate, 210, 370);
		graphics2D.drawString(certificateDetail.getVechicleType() != null ? certificateDetail.getVechicleType() : "",
				215, 412);
		graphics2D.drawString(
				certificateDetail.getRegistartionNumber() != null ? certificateDetail.getRegistartionNumber() : "", 230,
				455);
		graphics2D.drawString(certificateDetail.getChassisNumber() != null ? certificateDetail.getChassisNumber() : "",
				155, 497);

		graphics2D.drawString(certificateDetail.getInsuranceCompany() != null
				? certificateDetail.getInsuranceCompany().getCompanyName()
				: "", 160, 623);
		graphics2D.drawString(certificateDetail.getUsage() != null ? certificateDetail.getUsage() : "", 135, 582);
		graphics2D.drawString(certificateDetail.getLicensed() != null ? certificateDetail.getLicensed() : "", 290, 540);
	
		graphics2D.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
		graphics2D.setColor(Color.black);
		graphics2D.drawString(InsuranceCompanyConstants.CERTIFICATE_DISCLAIMER, 10, 747);
		
		User user = userRepo.findByUserId(certificateDetail.getUploadedBy());
		if (user != null && !StringUtils.isEmpty(user.getSignature())) {

			BufferedImage signImage = null;

			ByteArrayInputStream bis1 = new ByteArrayInputStream(user.getSignature());
			try {
				signImage = ImageIO.read(bis1);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			graphics2D.drawImage(signImage, 160, 660, 100, 80, null);
		}

		if (certificateDetail.getStatus() == 0) {
			Font font = new Font("Stencil", Font.BOLD, 160);
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(315), 190, 740);
			Font rotatedFont = font.deriveFont(affineTransform);
			graphics2D.setFont(rotatedFont);
			graphics2D.setColor(Color.red);
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				        (float) 0.8));
			graphics2D.drawString("REVOKED", 190, 740);

		}
		
		graphics2D.dispose();

		// convert to byte array and return
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage, "jpg", baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

}
