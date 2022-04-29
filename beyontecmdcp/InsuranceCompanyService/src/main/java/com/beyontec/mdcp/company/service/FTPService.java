package com.beyontec.mdcp.company.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import com.beyontec.mdcp.company.util.InsuranceCompanyConstants;
import com.beyontec.mdcp.company.util.PropertiesExtractor;

@Service
public class FTPService {

	// Creating FTP Client instance
	private static FTPClient ftp = null;

	public FTPService() throws NumberFormatException, SocketException, IOException {
		ftp = new FTPClient();

		ftp.connect(PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_HOST_KEY),
				Integer.valueOf(PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_PORT_KEY)));
		ftp.login(PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_UERNAME_KEY),
				PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_PASSWORD_KEY));
		ftp.enterLocalPassiveMode();

		ftp.setFileType(FTP.BINARY_FILE_TYPE);
	}

	public void getFTPService() throws NumberFormatException, SocketException, IOException {
		ftp = new FTPClient();
		ftp.connect(PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_HOST_KEY),
				Integer.valueOf(PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_PORT_KEY)));
		ftp.login(PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_UERNAME_KEY),
				PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_PASSWORD_KEY));
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		// ftp.enterLocalActiveMode();
		ftp.enterLocalPassiveMode();
	}

	// Method to upload the File on the FTP Server
	public void uploadFTPFile(String fileName, InputStream input) {

		try {
			getFTPService();
			ftp.storeFile(
					PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_SHARED_DIR_KEY) + fileName, input);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}

	// Download the FTP File from the FTP Server
	public ByteArrayInputStream downloadFTPFile(String source) throws IOException {
		InputStream inStream = null;

		try {
			if (!ftp.isConnected()) {
				getFTPService();
			}
			inStream = ftp.retrieveFileStream(
					PropertiesExtractor.getProperty(InsuranceCompanyConstants.FTP_SHARED_DIR_KEY) + source);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return new ByteArrayInputStream(org.apache.commons.io.IOUtils.toByteArray(inStream));
	}

	// list the files in a specified directory on the FTP
	public boolean listFTPFiles(String directory, String fileName) {
		// lists files and directories in the current working directory
		boolean verificationFilename = false;
		FTPFile[] files;
		try {
			files = ftp.listFiles(directory);
			for (FTPFile file : files) {
				String details = file.getName();
				System.out.println(details);
				if (details.equals(fileName)) {
					System.out.println("Correct Filename");
					verificationFilename = details.equals(fileName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return verificationFilename;
	}

	// Disconnect the connection to FTP
	public void disconnect() {
		if (ftp.isConnected()) {
			try {
				ftp.logout();
				ftp.disconnect();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}
	}
}