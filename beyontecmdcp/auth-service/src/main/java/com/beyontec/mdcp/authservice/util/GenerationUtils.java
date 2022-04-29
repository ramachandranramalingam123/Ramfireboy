package com.beyontec.mdcp.authservice.util;

import org.apache.commons.lang3.ArrayUtils;
import org.passay.CharacterData;
import org.passay.*;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.WordListDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.beyontec.mdcp.authservice.model.User;
import com.beyontec.mdcp.authservice.repository.UserRepository;
import com.beyontec.mdcp.authservice.service.SendMailService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Component
public class GenerationUtils {
	

	@Autowired
	private UserRepository userRepo;



	@Autowired
	private SendMailService sendMailService;

	private static final String DEFAULT_MESSAGE_PATH = "/messages.properties";

	private MessageResolver messageResolver;
	private String allowedChars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjklmnpqrstuvwxyz123456789!@#$";

	public static String generateOtp() {
		List rules = Arrays.asList(new CharacterRule(EnglishCharacterData.Digit, 6));
		PasswordGenerator generator = new PasswordGenerator();
		return generator.generatePassword(6, rules);
	}

	@PostConstruct
	public void init() {
		Properties props = new Properties();
		try (InputStream in = this.getClass().getResourceAsStream(DEFAULT_MESSAGE_PATH)) {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		messageResolver = new PropertiesMessageResolver(props);
	}

	public String generateRandomPassword() {
		List<CharacterRule> rules = Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, 1),
				new CharacterRule(EnglishCharacterData.LowerCase, 1), new CharacterRule(EnglishCharacterData.Digit, 1),
				new CharacterRule(new CharacterData() {
					@Override
					public String getErrorCode() {
						return "INVALID_CHARS";
					}

					@Override
					public String getCharacters() {
						return allowedChars;
					}
				}));

		PasswordGenerator generator = new PasswordGenerator();
		return generator.generatePassword(8, rules);
	}

	
	public List<String> validateAndChangePassword(User user, String newPassword) {

		String[] userId = user.getUserId().toString().toLowerCase().split("\\s+");
		String[] userName = user.getUserName().toLowerCase().split("\\s+");

		String[] userDetails = ArrayUtils.addAll(userId, userName);
		
		
		List<String> invalidWords = Arrays.asList(userDetails);

		Collections.sort(invalidWords);

		WordListDictionary wordListDictionary = new WordListDictionary(
				new ArrayWordList(invalidWords.toArray(new String[invalidWords.size()]), false));

		PasswordValidator validator = new PasswordValidator(messageResolver,
				Arrays.asList(new LengthRule(8, 30), new CharacterRule(EnglishCharacterData.UpperCase, 1),
						new CharacterRule(EnglishCharacterData.LowerCase, 1),
						new CharacterRule(EnglishCharacterData.Digit, 1),
						new CharacterRule(EnglishCharacterData.Special, 1), new WhitespaceRule(),
						new DictionarySubstringRule(wordListDictionary), new UsernameRule()));

		PasswordData passwordData = new PasswordData(newPassword);
		passwordData.setUsername(user.getUserId().toString());

		RuleResult result = validator.validate(passwordData);

		if (!result.isValid()) {
			return validator.getMessages(result);
		}

		return Collections.emptyList();

	}
}
