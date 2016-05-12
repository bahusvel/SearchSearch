package scrapper.ner;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by denislavrov on 12/2/14.
 */
public class EntityRecognition{
    private final static Logger logger = Logger.getLogger(EntityRecognition.class);
    private String input;

    private static final String serializedClassifier = "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz";
    private static AbstractSequenceClassifier<CoreLabel> classifier;

    static {
        try {
            logger.info(serializedClassifier);
            classifier = CRFClassifier.getClassifier(serializedClassifier);
            logger.info("CRFClassifier created!!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception CRFClassifier: " + e);
        }
    }

    public EntityRecognition(String input) throws Exception {
        logger.info("EntityRecognition constructor: " + input);
        this.input = input;
    }

    public ArrayList<String> getNames(){
        // TODO use this regex -> (?<=<PERSON>)(.*?)(?=</PERSON>) removes the need to parse out tags
        String inlineXML = classifier.classifyWithInlineXML(input);
        String regex = "<PERSON>(.*?)</PERSON>";
        Matcher matcher = Pattern.compile(regex).matcher(inlineXML);
        ArrayList<String> results = new ArrayList<String>();
        int taglen = "<PERSON>".length();
        while (matcher.find()){
            String tmp = matcher.group();
            results.add(tmp.substring(taglen, tmp.length() - (taglen+1)));
        }
        return results;
    }

    public HashSet<String> getUniqueNames(){
        String inlineXML = classifier.classifyWithInlineXML(input);
        String regex = "<PERSON>(.*?)</PERSON>";
        Matcher matcher = Pattern.compile(regex).matcher(inlineXML);
        HashSet<String> results = new HashSet<>();
        int taglen = "<PERSON>".length();
        while (matcher.find()){
            String tmp = matcher.group();
            results.add(tmp.substring(taglen, tmp.length() - (taglen+1)));
        }
        return results;
    }
    public String xmlClasifierString(){
        return classifier.classifyWithInlineXML(input);
    }
}
