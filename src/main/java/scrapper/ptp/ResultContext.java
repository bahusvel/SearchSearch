package scrapper.ptp;

import java.util.*;

/**
 * Created by denislavrov on 2/17/15.
 */
public class ResultContext extends Context {
    boolean orderReverse = false;

    private ResultContext(String rawContext) {
        super(rawContext);
    }


    private ResultContext(Context parentContext, int position, String rawContext, boolean orderReverse) {
        super(parentContext, position, rawContext);
        this.orderReverse = orderReverse;
    }

    public static ResultContext from(Context context, int begin, int end, boolean orderReverse){
        return new ResultContext(context, begin, context.rawContext.substring(begin, end), orderReverse);
    }

    public static ResultContext from(Context context, int begin, boolean orderReverse){
        return from(context, begin, context.rawContext.length(), orderReverse);
    }

    public ResultContext intersect(Context other){
        Optional<Context> parent = fastLCA(this, other);
        if (parent.isPresent()){
            int maxBegin = Math.max(other.position, position);
            int minEnd =  Math.min(other.position + other.rawContext.length(), position + rawContext.length());
            return from(parent.get(), maxBegin, minEnd, false);
        }
        return null;
    }

    public ContextTerm firstByDescriptor(TermDescriptor descriptor){
        for (ContextTerm contextTerm : descriptor.findAllTerms(this)) {
            return contextTerm;
        }
        return null;

    }

    public HashMap<ContextTerm, Integer> byDistance(TermDescriptor descriptor, TermDescriptor unit){
        HashMap<ContextTerm, Integer> distanceMap = new HashMap<>();
        for (ContextTerm contextTerm : descriptor.findAllTerms(this)) {
            ResultContext mid = ResultContext.from(this, 0, contextTerm.position, false);
            distanceMap.put(contextTerm, unit.findAllTerms(mid).size());
        }
        return distanceMap;
    }

    public static void main(String[] args) {
    }
}
