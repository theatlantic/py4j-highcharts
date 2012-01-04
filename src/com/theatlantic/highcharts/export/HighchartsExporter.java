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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import org.mozilla.javascript.RhinoException;

public class HighchartsExporter {

    public HighchartsExporter(ExportType type, SVGRendererInternal internalRenderer) {
        this.type = type;
        this.renderer = new SVGStreamRenderer(new SVGRenderer(internalRenderer),
            type.getTranscoder());
    }

    public void export(String chartOptions, String globalOptions, OutputStream out) {
        try {
            render(chartOptions, globalOptions, out);
        } catch (RhinoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw (new RuntimeException(e));
        } finally {
            if (out != null) {
                IOUtils.closeQuietly(out);
            }
        }
        
    }
    
    public void export(String chartOptions, String globalOptions, File file) {
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            render(chartOptions, globalOptions, fos);
        } catch (Exception e) {
            e.printStackTrace();
            throw (new RuntimeException(e));
        } finally {
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
        }
    }

    private void render(String chartOptions, String globalOptions, OutputStream out) {
        renderer.setChartOptions(chartOptions)
                .setGlobalOptions(globalOptions)
                .setOutputStream(out)
                .render();
    }

    public SVGStreamRenderer getRenderer() {
        return renderer;
    }

    public ExportType getType() {
        return type;
    }

    private final SVGStreamRenderer renderer;

    private final ExportType type;
}
