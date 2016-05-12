package scrapper.e2tcorrelation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by denislavrov on 6/09/15.
 */
public class EntityCorrelation {

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
        /*
        Context root = new Context(rawContext);
        TermDescriptor nameDescriptor = new RawDescriptor(TERM_TYPE.PHRASE, "Kerstin Voigt");
        ContextTerm name = nameDescriptor.findFirst(root);
        ResultContext timeContext = name.before();
        TermDescriptor timeDescriptor = new RegexDescriptor("[0-9]{1,2}:[0-9]{1,2}(AM|PM)?", TERM_TYPE.WORD);
        HashMap<ContextTerm, Integer> map = timeContext.byDistance(timeDescriptor, new CharDescriptor());
        if (map.isEmpty()) System.exit(1);
        int max = map.values().stream().max(Integer::compare).get();
        ContextTerm time = map.entrySet().stream().filter(entry -> entry.getValue() == max).findFirst().get().getKey();
        System.out.println(time.getRawTerm() + " " + name.getRawTerm());
        */

        Matcher matcher = Pattern.compile("([0-9]{1,2}:[0-9]{1,2}(AM|PM)? ?-? ?){1,2}").matcher(rawContext);
        while (matcher.find()){
            System.out.println(matcher.group());
        }

    }
}
