package com.beyontec.mdcp.company.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.beyontec.mdcp.company.dto.Attachment;
import com.beyontec.mdcp.company.dto.CertificateIssueDto;
import com.beyontec.mdcp.company.dto.SendMailDto;
import com.beyontec.mdcp.company.model.CertificateDetails;
import com.beyontec.mdcp.company.model.InsuranceCompany;
import com.beyontec.mdcp.company.repo.CertificateRepo;
import com.beyontec.mdcp.company.repo.CompanyRepo;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.service.ImageGenerateService;
import com.beyontec.mdcp.company.service.SendMailService;
import com.github.jknack.handlebars.Template;

@Service
public class CertificateIssuesUtil {

	@Autowired
	private HandlebarTemplateLoader templateLoader;

	@Autowired
	private SendMailService sendMailService;

	@Autowired
	private CertificateRepo certificateRepo;
	
	@Autowired
	private CompanyRepo companyRepo;
	
	@Autowired
	private ImageGenerateService imageGenerateService;
	
	public void showCertificateByPDF(CertificateIssueDto certificateIssueDto) throws ParseException, Exception{
		
		Map<String, Object> data = new HashMap<>();
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateIssueDto.getCertificateNo());
		String b64 = Base64.getEncoder().encodeToString((imageGenerateService.GenerateImage(certificateDetails)));
		data.put("certificateImg", "data:image/jpeg;base64," + b64);
		InsuranceCompany company = companyRepo.findByCompanyId(certificateIssueDto.getCompanyId());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

			Template template = templateLoader.getTemplate("certificate");
			String mergedTemplate = template.apply(data);
			Document doc = getDocumentBuilder().parse(new ByteArrayInputStream(mergedTemplate.getBytes("UTF-8")));
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocument(doc, null);
			renderer.layout();
			renderer.createPDF(out);
			renderer.finishPDF();
			Attachment attachment = new Attachment();

			List<Attachment> attachmentList = new ArrayList<Attachment>();
			SendMailDto mailDto = new SendMailDto();
			mailDto.setSubject("MDCP - Certificate issuance - " + certificateDetails.getRegistartionNumber());
			mailDto.setMessage("<html>\r\n" + "<head>\r\n" + "</head>\r\n" + "\r\n" + "<body>\r\n"
					+ "  <p>Dear "+certificateIssueDto.getPolicyHolder()+",</p>\r\n"
					+ "  <p>Your insurance certificate for vehicle registration number "+ certificateIssueDto.getRegistrationNo()+"  has been issued by "+company.getCompanyName()+", Please check your e-mail attachment.</p><br/><br/>"
					+ "  Thank you<br/><br/>Team MDCP<br/><br/><b>**This is a system generated Email, please do not reply**</b>" + "</body>\r\n" + "\r\n" + "</html>");
			if(certificateIssueDto.getEmail().contains(",")) {
				mailDto.setToEmail(Arrays.asList(certificateIssueDto.getEmail().split(",")));
			}else {
				mailDto.setToEmail(Arrays.asList(certificateIssueDto.getEmail()));
			}
			attachment.setAttachmentName("certificate.pdf");
			attachment.setFileByte(out.toByteArray());
			attachment.setContentType("application/pdf");
			attachmentList.add(attachment);
			mailDto.setAttachments(attachmentList);
			sendMailService.sendEmail(mailDto);
	}

	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(false);
		fac.setValidating(false);
		fac.setFeature("http://xml.org/sax/features/namespaces", false);
		fac.setFeature("http://xml.org/sax/features/validation", false);
		fac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		return fac.newDocumentBuilder();
	}

	public Response<String> showCertificateByImage(CertificateIssueDto certificateIssueDto) throws ParseException {

		Response<String> response = new Response<>();
		Map<String, Object> data = new HashMap<>();
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateIssueDto.getCertificateNo());
		String b64 = Base64.getEncoder().encodeToString((imageGenerateService.GenerateImage(certificateDetails)));	
		data.put("certificateImg", "data:image/jpeg;base64,"+ b64);
		ByteArrayOutputStream out = new ByteArrayOutputStream();		try {

			Template template = templateLoader.getTemplate("certificate");
			String mergedTemplate = template.apply(data);
			Document doc = getDocumentBuilder().parse(new ByteArrayInputStream(mergedTemplate.getBytes("UTF-8")));
			String outputFile = ".//certificateIssue.pdf";
			OutputStream os = new FileOutputStream(outputFile);
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocument(doc, null);
			renderer.layout();
			renderer.createPDF(out);
			renderer.finishPDF();
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setData(Base64.getEncoder().encodeToString(out.toByteArray()));
		response.setStatus(200);
		return response;
	}

	public Response<String> showCertificateImage(CertificateIssueDto certificateIssueDto) throws ParseException {

		Response<String> response = new Response<>();
		Map<String, Object> data = new HashMap<>();
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateIssueDto.getCertificateNo());
		String b64 = Base64.getEncoder().encodeToString((imageGenerateService.GenerateImage(certificateDetails)));	
		data.put("certificateImg", "data:image/jpeg;base64,"+ b64);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String mergedTemplate = null;
		try {

			Template template = templateLoader.getTemplate("certificate");
			mergedTemplate = template.apply(data);

			InputStream in = new ByteArrayInputStream(mergedTemplate.getBytes("UTF-8"));
			BufferedImage bImageFromConvert = ImageIO.read(in);

			ImageIO.write(bImageFromConvert, "jpeg", out);

		} catch (Exception e) {
			e.printStackTrace();
		}

		response.setData(Base64.getEncoder().encodeToString(out.toByteArray()));

		response.setStatus(200);
		return response;
	}
	
	public ByteArrayInputStream showCertificateByPDFImage(CertificateIssueDto certificateIssueDto) throws ParseException {

		Response<String> response = new Response<>();
		Map<String, Object> data = new HashMap<>();
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateIssueDto.getCertificateNo());
		String b64 = Base64.getEncoder().encodeToString((imageGenerateService.GenerateImage(certificateDetails)));	
		data.put("certificateImg", "data:image/jpeg;base64,"+ b64);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {

			Template template = templateLoader.getTemplate("certificate");
			String mergedTemplate = template.apply(data);
			Document doc = getDocumentBuilder().parse(new ByteArrayInputStream(mergedTemplate.getBytes("UTF-8")));
			String outputFile = ".//certificateIssue.pdf";
			OutputStream os = new FileOutputStream(outputFile);
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocument(doc, null);
			renderer.layout();
			renderer.createPDF(out);
			renderer.finishPDF();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(out.toByteArray());
	}
}
