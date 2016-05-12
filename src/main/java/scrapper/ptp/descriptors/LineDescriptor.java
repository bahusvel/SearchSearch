package scrapper.ptp.descriptors;

import scrapper.ptp.AbstractTerm.TERM_TYPE;

/**
 * Created by denislavrov on 2/17/15.
 */
public class LineDescriptor extends RegexDescriptor{
    public static LineDescriptor INSTANCE = new LineDescriptor();
    public LineDescriptor() {
        super("[^\\n]++", TERM_TYPE.LINE);
    }
}
