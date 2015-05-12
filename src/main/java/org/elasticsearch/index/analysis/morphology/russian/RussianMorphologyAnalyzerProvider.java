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

import org.apache.lucene.morphology.analyzer.MorphologyAnalyzer;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;

import java.io.IOException;

/**
 *
 */
public class RussianMorphologyAnalyzerProvider extends AbstractIndexAnalyzerProvider<MorphologyAnalyzer> {

    private final MorphologyAnalyzer analyzer;

    @Inject
    public RussianMorphologyAnalyzerProvider(Index index, @IndexSettings Settings indexSettings,
                                             @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        throw new ElasticsearchException("flag settings "+indexSettings.get("flags.a"));
        /*try {
            analyzer = new RussianAnalyzer();
        } catch (IOException ex) {
            throw new ElasticsearchException("Unable to load Russian morphology analyzer", ex);
        }*/
    }

    @Override
    public MorphologyAnalyzer get() {
        return this.analyzer;
    }
}
