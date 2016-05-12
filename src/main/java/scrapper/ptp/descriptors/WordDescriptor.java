package scrapper.ptp.descriptors;

import scrapper.ptp.AbstractTerm.TERM_TYPE;

/**
 * Created by denislavrov on 2/17/15.
 */
public class WordDescriptor extends RegexDescriptor {
    public static WordDescriptor INSTANCE = new WordDescriptor();
    public WordDescriptor() {
        super("[^\\s]++", TERM_TYPE.WORD);
    }
}
