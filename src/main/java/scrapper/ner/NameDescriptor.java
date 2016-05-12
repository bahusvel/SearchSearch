package scrapper.ner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrapper.ptp.AbstractTerm.TERM_TYPE;
import scrapper.ptp.Context;
import scrapper.ptp.ContextTerm;
import scrapper.ptp.TermDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by denislavrov on 7/09/15.
 */
public class NameDescriptor extends TermDescriptor {
    public NameDescriptor() {
        super(TERM_TYPE.PHRASE);
    }

    @Override
    public Collection<ContextTerm> findAllTerms(Context context) {
        String rawContext = context.getRawContext();
        Document doc = Jsoup.parse(rawContext);
        HashMap<String, Integer> namePositionMap = new HashMap<>();
        ArrayList<ContextTerm> terms = new ArrayList<>();
        for (Element person : doc.getElementsByTag("person")) {
            String name = person.text();
            int position = rawContext.indexOf(name, namePositionMap.getOrDefault(name, 0));
            namePositionMap.put(name, position);
            terms.add(new ContextTerm(this, context, position, name));
        }
        return terms;
    }

    public static void main(String[] args) {
        String rawContext = "Session Manif1: Manifold learning and dimensionality reduction 1\n" +
                "Monday, July 13, 10:50AM-12:10PM, Room: <PERSON>Brehon</PERSON>, Chair: <PERSON>Andras</PERSON>, <PERSON>Peter</PERSON>\n" +
                "10:50AM High-Dimensional Function Approximation Using Local Linear Embedding [#15134]\n" +
                "<PERSON>Peter Andras</PERSON>\n" +
                "11:10AM Learning Orthogonal Sparse Representations by using Geodesic Flow Optimization [#15540]\n" +
                "<PERSON>Henry Schuetze</PERSON>, <PERSON>Erhardt Barth</PERSON> and <PERSON>Thomas Martinetz</PERSON>\n" +
                "11:30AM Stochastic Computation of Dominant Eigenvalue and the Law of Total Variance [#15661]\n" +
                "<PERSON>George Georgiou</PERSON>, Kerstin Voigt and <PERSON>Haiyan Qiao</PERSON>\n" +
                "11:50AM Sparsity Analysis of Learned Factors in Multilayer NMF [#15396]\n" +
                "<PERSON>Ievgen Redko</PERSON> and <PERSON>Younes Bennani</PERSON>";
        System.out.println(new NameDescriptor().findAllTerms(new Context(rawContext)));
    }
}
