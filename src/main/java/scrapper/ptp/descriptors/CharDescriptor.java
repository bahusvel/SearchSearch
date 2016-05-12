package scrapper.ptp.descriptors;

import scrapper.ptp.AbstractTerm.TERM_TYPE;
import scrapper.ptp.Context;
import scrapper.ptp.ContextTerm;
import scrapper.ptp.TermDescriptor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by denislavrov on 3/4/15.
 */
public class CharDescriptor extends TermDescriptor {
    public static CharDescriptor INSTANCE = new CharDescriptor();

    public CharDescriptor() {
        super(TERM_TYPE.CHAR);
    }

    @Override
    public Collection<ContextTerm> findAllTerms(Context context) {
        ArrayList<ContextTerm> terms = new ArrayList<>();
        char[] chars = context.getRawContext().toCharArray();
        int count = 0;
        for (char aChar : chars) {
            terms.add(new ContextTerm(this, context, count++, Character.toString(aChar)));
        }
        return terms;
    }
}
