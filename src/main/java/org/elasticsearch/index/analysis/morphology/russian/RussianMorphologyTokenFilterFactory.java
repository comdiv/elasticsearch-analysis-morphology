/*
 * Copyright 2012 Igor Motov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticsearch.index.analysis.morphology.russian;

import org.apache.log4j.LogManager;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.analyzer.MorphologyFilter;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettings;

import java.io.IOException;

/**
 *
 */
public class RussianMorphologyTokenFilterFactory extends AbstractTokenFilterFactory {

    private final LuceneMorphology luceneMorph;

    @Inject
    public RussianMorphologyTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, String name, Settings settings) {
        super(index, indexSettings, name, settings);
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException ex) {
            throw new ElasticsearchException("Unable to load Russian morphology analyzer", ex);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new MorphologyFilter(tokenStream, luceneMorph,true);
    }
}
