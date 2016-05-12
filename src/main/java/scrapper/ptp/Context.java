package scrapper.ptp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by denislavrov on 2/17/15.
 */
public class Context {
    Context parentContext = null;
    int position;
    String rawContext;
    HashMap<TermDescriptor, Collection<ContextTerm>> terms = new HashMap<>();

    public Context(String rawContext) {
        this.rawContext = rawContext;
    }

    protected Context(){
        rawContext = "";
    }

    protected Context(Context parentContext, int position, String rawContext) {
        this.parentContext = parentContext;
        this.position = position;
        this.rawContext = rawContext;
    }

    public String getRawContext() {
        return rawContext;
    }

    public Collection<ContextTerm> tokenizeBy(TermDescriptor descriptor){
        return terms.computeIfAbsent(descriptor, desc -> desc.findAllTerms(this));
    }

    public static Optional<Context> fastLCA(Context p, Context q){
        int h1 = p.getHeight();
        int h2 = q.getHeight();
        // swap both nodes in case p is deeper than q.
        if (h1 > h2) {
            int h = h1;
            h1 = h2;
            h2 = h;
            Context a = p;
            p = q;
            q = a;
        }
        // invariant: h1 <= h2.
        int dh = h2 - h1;
        for (int h = 0; h < dh; h++)
            q = q.parentContext;
        while (p != null && q != null) {
            if (p == q) return Optional.of(p);
            p = p.parentContext;
            q = q.parentContext;
        }
        return Optional.empty();
    }

    public static Optional<Context> parentContext(Context... contexts){
        Context parent = contexts[0];
        for (Context context : contexts) {
            parent = fastLCA(context, parent).get();
            if (parent == null) break;
        }
        return Optional.ofNullable(parent);
    }

    private int getHeight(){
        int height = 0;
        Context p = this;
        while (p != null) {
            height++;
            p = p.parentContext;
        }
        return height;
    }

    @Override
    public String toString() {
        return "Context{" +
                "parentContext=" + parentContext +
                ", position=" + position +
                ", rawContext='" + rawContext + '\'' +
                '}';
    }

    public static void main(String[] args) {
        Context test1 = new Context("bla bla");
        Context test2 = new Context("bla");
        Context test3 = new Context("Denis");
        Context parent1 = new Context("bla bla bla");
        Context parent2 = new Context("bla bla");
        test1.parentContext = parent1;
        test2.parentContext = parent2;
        parent2.parentContext = parent1;
        System.out.println(parentContext(test1, test2, test3));
    }
}
