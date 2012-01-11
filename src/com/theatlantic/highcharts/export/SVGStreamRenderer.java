/*
  Modified by The Atlantic Media Company from original source.
  See <https://github.com/one2team/highcharts-serverside-export> for original.
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this work except in compliance with the License. You may obtain a copy
  of the License in the LICENSE file, or at:
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*/
package com.theatlantic.highcharts.export;

import com.theatlantic.highcharts.export.Renderer.ChartRenderer;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.IOUtils;

class SVGStreamRenderer extends ChartRenderer {

    @Override
    public void render() {
        ByteArrayOutputStream baos = null;
        Reader reader = null;
        
        try {    
            if (getOutputStream() == null) {
                throw (new RuntimeException("outputstream cannot be null"));
            }
            
            baos = new ByteArrayOutputStream();

            // if transcoder is null, render directly to output stream
            if (transcoder == null) {
                wrapped.setOutputStream(getOutputStream()).render();
            } else {
                wrapped.setOutputStream(baos).render();
                reader = new StringReader(baos.toString());
                transcoder.transcode(new TranscoderInput(reader), new TranscoderOutput(getOutputStream()));
            }

        } catch (TranscoderException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(baos);
            if (reader != null) {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    @Override
    public Renderer setGlobalOptions(String options) {
        wrapped.setGlobalOptions(options);
        return this;
    }

    @Override
    public Renderer setChartOptions(String options) {
        wrapped.setChartOptions(options);
        return this;
    }

    Transcoder getTranscoder() {
        return transcoder;
    }
    
    public SVGStreamRenderer(Renderer wrapped, Transcoder transcoder) {
        this.wrapped = wrapped;
        this.transcoder = transcoder;
    }

    private final Renderer wrapped;

    private final Transcoder transcoder;
}