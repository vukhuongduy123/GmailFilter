package classify;


import cc.mallet.classify.Classification;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.InstanceList;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import lombok.Getter;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class TextClassify {
	private final Pipe pipe;
	private final InstanceList trainingInstance;
	private static final String FILE_PATH = "E:\\Self Study Course\\java\\GmailFilter\\src\\main" +
											"\\resources\\emails_data\\spam";
	private static final TextClassify textClassify;
	private final ClassifierTrainer<?> classifierTrainer;

	public static TextClassify getInstance() {
		return textClassify;
	}

	static {
		textClassify = new TextClassify(
				"E:\\Self Study Course\\java\\GmailFilter\\src\\main\\resources\\emails_data");
	}

	private TextClassify(String path) {
		pipe = initPipe();
		FileIterator iterator =
				new FileIterator(new File(path),
								 file -> file.toString().endsWith(".txt"),
								 FileIterator.LAST_DIRECTORY);
		trainingInstance = new InstanceList(pipe);
		trainingInstance.addThruPipe(iterator);

		classifierTrainer = new NaiveBayesTrainer();
		classifierTrainer.train(trainingInstance);
	}

	public ArrayList<Classification> getClassify(InstanceList instance) {
		return classifierTrainer.getClassifier().classify(instance);
	}

	public void initStrings(List<Message> messages) throws IOException {
		for (Message message : messages) {
			MessagePart messagePart = message.getPayload();
			StringBuilder content = new StringBuilder();
			String header;
			if (messagePart == null) {
				content = new StringBuilder(
						StringUtils.newStringUtf8(Base64.decodeBase64(message.getRaw())));
				header = message.getSnippet();
			} else {
				header = messagePart.getHeaders()
									.stream()
									.filter(e -> e.getName().equalsIgnoreCase("Subject"))
									.findAny().orElseThrow().getValue();
				if (messagePart.getMimeType().equalsIgnoreCase("text/plain")) {
					content = new StringBuilder((StringUtils.newStringUtf8(Base64.decodeBase64(
							messagePart.getBody().getData()))));
				} else if (messagePart.getMimeType().equalsIgnoreCase("text/html")) {
					content = new StringBuilder(Jsoup.parse(StringUtils.newStringUtf8(
							Base64.decodeBase64((messagePart.getBody().getData())))).wholeText());
				} else if (messagePart.getMimeType().startsWith("multipart")) {
					for (MessagePart part : messagePart.getParts()) {
						if (part.getMimeType().equalsIgnoreCase("text/plain")) {
							content.append(StringUtils.newStringUtf8(
									Base64.decodeBase64(part.getBody().getData())));
						} else if (part.getMimeType().equalsIgnoreCase("text/html")) {
							content.append(Jsoup.parse(StringUtils.newStringUtf8(
									Base64.decodeBase64((part.getBody().getData())))).wholeText());
						}
					}
				}
			}

			// replace all illegal file name
			Files.write(
					Path.of(FILE_PATH + "\\" + header.replaceAll("[^a-zA-Z0-9\\\\._]+", "") +
							".txt"),
					content.toString().getBytes(), StandardOpenOption.CREATE);
		}
	}

	private Pipe initPipe() {
		ArrayList<Pipe> pipeList = new ArrayList<>();
		pipeList.add(new Input2CharSequence("UTF-8"));

		// Regular expression for what constitutes a token.
		//  This pattern includes Unicode letters, Unicode numbers,
		//   and the underscore character. Alternatives:
		//    "\\S+"   (anything not whitespace)
		//    "\\w+"    ( A-Z, a-z, 0-9, _ )
		//    "[\\p{L}\\p{N}_]+|[\\p{P}]+"   (a group of only letters and numbers OR
		//                                    a group of only punctuation marks)
		Pattern tokenPattern =
				Pattern.compile("[\\p{L}\\p{N}_]+");

		// Tokenize raw strings
		pipeList.add(new CharSequence2TokenSequence(tokenPattern));

		// Normalize all tokens to all lowercase
		pipeList.add(new TokenSequenceLowercase());

		// Remove stop words from a standard English stop list.
		//  options: [case sensitive] [mark deletions]
		pipeList.add(new TokenSequenceRemoveStopwords(false, false));

		// Rather than storing tokens as strings, convert
		//  them to integers by looking them up in an alphabet.
		pipeList.add(new TokenSequence2FeatureSequence());

		// Do the same thing for the "target" field:
		//  convert a class label string to a Label object,
		//  which has an index in a Label alphabet.
		pipeList.add(new Target2Label());

		// Now convert the sequence of features to a sparse vector,
		//  mapping feature IDs to counts.
		pipeList.add(new FeatureSequence2FeatureVector());

		// Print out the features and the label
		//pipeList.add(new PrintInputAndTarget());

		return new SerialPipes(pipeList);
	}
}
