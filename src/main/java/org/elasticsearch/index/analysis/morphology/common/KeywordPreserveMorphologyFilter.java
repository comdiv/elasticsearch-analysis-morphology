package org.elasticsearch.index.analysis.morphology.common;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by comdiv on 09.09.2015.
 */
public class KeywordPreserveMorphologyFilter extends TokenFilter {
    private LuceneMorphology luceneMorph;
    private Iterator<String> iterator;
    private AttributeSource.State state;
    public static int DEFAULT_PRESERVE_MORPHOLOGY_FLAG = 134217728;
    public int preserveMorphologyFlag;
    public boolean usePreserveFlag;
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final FlagsAttribute flagAtt;
    private final KeywordAttribute keywordAtt;

    public KeywordPreserveMorphologyFilter(TokenStream tokenStream, LuceneMorphology luceneMorph, boolean usePreserveFlag) {
        this(tokenStream, luceneMorph, usePreserveFlag, DEFAULT_PRESERVE_MORPHOLOGY_FLAG);
    }

    public KeywordPreserveMorphologyFilter(TokenStream tokenStream, LuceneMorphology luceneMorph, boolean usePreserveFlag, int preserveMorphologyFlag) {
        this(tokenStream, luceneMorph);
        this.usePreserveFlag = usePreserveFlag;
        this.preserveMorphologyFlag = preserveMorphologyFlag;
    }

    public KeywordPreserveMorphologyFilter(TokenStream tokenStream, LuceneMorphology luceneMorph) {
        super(tokenStream);
        this.preserveMorphologyFlag = DEFAULT_PRESERVE_MORPHOLOGY_FLAG;
        this.usePreserveFlag = false;
        this.termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
        this.flagAtt = (FlagsAttribute)this.addAttribute(FlagsAttribute.class);
        this.keywordAtt = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);
        this.luceneMorph = luceneMorph;
    }

    public final boolean incrementToken() throws IOException {

        if(this.iterator != null) {
            if(this.iterator.hasNext()) {
                this.restoreState(this.state);
                this.posIncrAtt.setPositionIncrement(0);
                this.termAtt.setEmpty().append((String)this.iterator.next());
                return true;
            }

            this.state = null;
            this.iterator = null;
        }

        while(true) {
            boolean b = this.input.incrementToken();
            if(!b) {
                return false;
            }

            if(this.usePreserveFlag) {
                int s = this.flagAtt.getFlags();
                if(0 != (s & this.preserveMorphologyFlag)) {
                    return true;
                }
            }

            if(this.keywordAtt.isKeyword()){
                return true;
            }

            if(this.termAtt.length() > 0) {
                String var9 = new String(this.termAtt.buffer(), 0, this.termAtt.length());
                boolean restoreFirstCharCase = false;
                if(!var9.toLowerCase().equals(var9) && Character.isUpperCase(var9.charAt(0))) {
                    restoreFirstCharCase = true;
                    var9 = var9.toLowerCase();
                }

                if(this.luceneMorph.checkString(var9)) {
                    List forms = this.luceneMorph.getNormalForms(var9);
                    if(restoreFirstCharCase && !forms.isEmpty()) {
                        int len = forms.size();

                        for(int i = 0; i < len; ++i) {
                            String lowercased = (String)forms.get(i);
                            String restored = Character.toUpperCase(lowercased.charAt(0)) + lowercased.substring(1);
                            forms.add(restored);
                        }
                    }

                    if(forms.isEmpty()) {
                        continue;
                    }

                    if(forms.size() == 1) {
                        this.termAtt.setEmpty().append((String)forms.get(0));
                    } else {
                        this.state = this.captureState();
                        this.iterator = forms.iterator();
                        this.termAtt.setEmpty().append((String)this.iterator.next());
                    }
                }
            }

            return true;
        }
    }
}
