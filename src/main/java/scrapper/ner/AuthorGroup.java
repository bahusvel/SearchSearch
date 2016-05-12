package scrapper.ner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrapper.ptp.AbstractTerm.TERM_TYPE;
import scrapper.ptp.Context;
import scrapper.ptp.ContextTerm;
import scrapper.ptp.ResultContext;
import scrapper.ptp.descriptors.CharDescriptor;
import scrapper.ptp.descriptors.RegexDescriptor;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Created by denislavrov on 12/13/14.
 */
public class AuthorGroup {
    List<String> names;
    HashMap<String, Integer> keywords;
    String originalText;
    String time;
    String room;
    String publicationTitle;
    private final static int NAME_P_FACTOR = 15;
    private final static int PROXIMITY_FACTOR = 300;

    @Override
    public String toString() {
        return "AuthorGroup{" +
                "\nnames=" + names +
                "\nkeywords=" + keywords + "\n}";
    }

    private AuthorGroup(){}

    private void soupNames(String xml){
        Document doc = Jsoup.parse(xml);
        names = doc.getElementsByTag("person").stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }

    private static String oneWordGuesser(String originalText){
        String filtered = originalText;
        HashMap<String, String> replaceMap = new HashMap<>();
        Context root = new Context(originalText);
        NameDescriptor nameDescriptor = new NameDescriptor();
        RegexDescriptor namePart = new RegexDescriptor("([A-Z][\\w.-]++ ?)++)", TERM_TYPE.PHRASE);
        ArrayList<ContextTerm> terms = (ArrayList<ContextTerm>) nameDescriptor.findAllTerms(root);
        for (ContextTerm name : terms) {
            // if name is one word
            if (!name.getRawTerm().contains(" ")) {
                ContextTerm pterm = namePart.fintLast(name.left());
                ContextTerm nterm = namePart.findFirst(name.right());
                boolean matched = false;
                ResultContext pbetween = pterm.between(name);
                ResultContext nbetween = nterm.between(name);
                if (pbetween.getRawContext().matches("\\s*")) {
                    String original = pterm + pbetween.getRawContext() + "<PERSON>" + name.getRawTerm() + "</PERSON>";
                    String replace = "<PERSON>" + pterm + pbetween.getRawContext() + name.getRawTerm() + "</PERSON>";
                    replaceMap.put(original, replace);
                    matched = true;
                }
                if (nbetween.getRawContext().matches("\\s*")) {
                    String original = "<PERSON>" + name.getRawTerm() + "</PERSON>" + nbetween.getRawContext() + nterm;
                    String replace = "<PERSON>" + name.getRawTerm() + nbetween.getRawContext() + nterm + "</PERSON>";
                    replaceMap.put(original, replace);
                    matched = true;
                }
                if (!matched) {
                    String original = "<PERSON>" + name.getRawTerm() + "</PERSON>";
                    String replace = name.getRawTerm();
                    replaceMap.put(original, replace);
                }
            }
        }
        for (Entry<String, String> replace : replaceMap.entrySet()) {
            filtered = filtered.replaceAll(replace.getKey(), replace.getValue());
        }
        return filtered;
    }

    private static String positionalGuesser(String originalText){
        String filtered = originalText;
        Context root = new Context(originalText);
        HashSet<String> foundNames = new HashSet<>();
        NameDescriptor nameDescriptor = new NameDescriptor();
        RegexDescriptor untaggedName = new RegexDescriptor("(?<!>)(\\b([A-Z][\\w.-]+ )([A-Z][\\w.-]++ ?)++)(?!<)", TERM_TYPE.PHRASE);
        ArrayList<ContextTerm> terms = (ArrayList<ContextTerm>) nameDescriptor.findAllTerms(root);
        // slide over the array in pairs
        for (int i = 0; i < terms.size()-1; i++) {
            ContextTerm tterm = terms.get(i);
            ContextTerm nterm = terms.get(i+1);
            if (tterm.distanceTo(nterm, CharDescriptor.INSTANCE) >= 6){
                // skips things like and, or, ","
                ResultContext between = tterm.between(nterm);
                if (!between.getRawContext().contains("\n")) {
                    // lets use rules to look for names
                    untaggedName.findAllTerms(between).stream().peek(System.out::println)
                            .forEach(name -> foundNames.add(name.getRawTerm().trim()));
                }
            }
        }
        for (String name : foundNames) {
            filtered = filtered.replaceAll("(?<!>)" + name + "(?!<)", "<PERSON>" + name + "</PERSON>");
        }
        return filtered;
    }

    private static String stripTags(String input, String... tags){
        String filtered = input;
        for (String tag : tags) {
            filtered = filtered.replaceAll('<'+tag+'>',"");
            filtered = filtered.replaceAll("</"+tag+'>',"");
        }
        return filtered;
    }

    public static ArrayList<AuthorGroup> extractProximityGroups(String ungrouped) {
        ArrayList<AuthorGroup> authorGroups = new ArrayList<>();
        EntityRecognition er;
        try{
            er = new EntityRecognition(ungrouped);
            String processed = er.xmlClasifierString();
            // run semantic guessing algorithm to find unmatched names
            processed = stripTags(processed, "ORGANIZATION", "LOCATION");
            processed = oneWordGuesser(processed);
            processed = positionalGuesser(processed);
            String nameGroupRegEx = "(<PERSON>(.*?)</PERSON>((.|\\s){0," + NAME_P_FACTOR + "}(?=<PERSON>))?)+";
            RegexDescriptor descriptor = new RegexDescriptor(nameGroupRegEx, TERM_TYPE.PHRASE);
            ContextTerm pTerm = null;
            for (ContextTerm contextTerm : descriptor.findAllTerms(new Context(processed))) {
                AuthorGroup group = new AuthorGroup();
                String groupProximity;
                if(pTerm != null)
                    groupProximity = pTerm.between(contextTerm).getRawContext();
                else
                    groupProximity = contextTerm.before().getRawContext();
                group.originalText = groupProximity;
                group.soupNames(contextTerm.getRawTerm());
                group.keywordsFromText();
                authorGroups.add(group);
                pTerm = contextTerm;
            }

        }catch (Exception e){
        }
        return authorGroups;
    }

    public HashMap<String, Integer> getKeywords() {
        return keywords;
    }

    private void keywordsFromText(){
        String filtered = originalText;
        // remove names from keyword text
        for (String name : names){
            filtered = filtered.replaceAll(name, "");
        }
        // strip out times, may later capture them for whatever reason
        filtered = filtered.replaceAll("[0-9]?[0-9]:[0-9][0:9].{0,2}", "");
        keywords = analyzeKeywords(filtered);
    }

    private static HashMap<String, Integer> analyzeKeywords(String originalText){
        HashMap<String, Integer> tokens = new HashMap<>();
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
        try (TokenStream ts = analyzer.tokenStream("myfield", originalText)){
            ts.reset(); // Resets this stream to the beginning. (Required)
            while (ts.incrementToken()) {
                tokens.merge(ts.getAttribute(CharTermAttribute.class).toString(), 1, Math::addExact);
            }
            ts.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private static int numberOfWords(String text){
        String trim = text.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\W+").length;
    }

    public static Collection<AuthorGroup> lookupProximityGroups(Collection<AuthorGroup> groups, String name){
        return groups.stream()
                .filter(group -> group.names.contains(name))
                .collect(Collectors.toList());
    }

    public static HashSet<String> getUniqueNames(Collection<AuthorGroup> groups){
        HashSet<String> names = new HashSet<>();
        groups.forEach(group -> { // filter added to prevent single word names being passed
            List<String> groupNames = group.names.stream()
                    .filter(name -> numberOfWords(name) > 1 && numberOfWords(name) < 6)
                    .collect(Collectors.toList());
            names.addAll(groupNames);
        });
        return names;
    }

    public static void main(String[] args) {
        String rawContext = "Session Manif1: Manifold learning and dimensionality reduction 1\n" +
                "Monday, July 13, 10:50AM-12:10PM, Room: Brehon, Chair: Andras, Peter\n" +
                "10:50AM High-Dimensional Function Approximation Using Local Linear Embedding [#15134]\n" +
                "Peter Andras\n" +
                "11:10AM Learning Orthogonal Sparse Representations by using Geodesic Flow Optimization [#15540]\n" +
                "Henry Schuetze, Erhardt Barth and Thomas Martinetz\n" +
                "11:30AM Stochastic Computation of Dominant Eigenvalue and the Law of Total Variance [#15661]\n" +
                "George Georgiou, Kerstin Voigt and Haiyan Qiao\n" +
                "11:50AM Sparsity Analysis of Learned Factors in Multilayer NMF [#15396]\n" +
                "Ievgen Redko and Younes Bennani";
        System.out.println(extractProximityGroups(rawContext));
    }
}

