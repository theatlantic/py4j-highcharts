package com.theatlantic.autograph.py4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import org.mozilla.javascript.RhinoException;

import com.theatlantic.highcharts.export.ExportType;
import com.theatlantic.highcharts.export.HighchartsExporter;


import py4j.Py4JJavaException;

/**
 * Exports SVG or PNG from a json input of Highcharts options.
 *
 * @author <a href="https://github.com/fdintino">Frankie Dintino</a>
 * @version $Id$
 */
public class HighchartsExport {
    
    HighchartsExporter svgFromJsonExporter;
    SVGRasterizer rasterizer;
    
    public HighchartsExport() {
        svgFromJsonExporter = ExportType.svg.createJsonExporter();
        rasterizer = new SVGRasterizer();
    }
    
    public String export_to_svg(String jsonOptions) {
        ByteArrayOutputStream svgOutput = new ByteArrayOutputStream();
        try {
            try {
                svgFromJsonExporter.export(jsonOptions, null, svgOutput);
            } catch (RhinoException re) {
                re.printStackTrace();
                throw new ExtendedScriptException(re);
            }
        } catch (ExtendedScriptException e) {
            throw new Py4JJavaException(e.getMessage(), e);
        }
        return svgOutput.toString();
    }
    
    public void export_to_svg(String jsonOptions, String destFile) {
        FileOutputStream ostream;
        try {
            ostream = new FileOutputStream(new File(destFile));
        } catch (FileNotFoundException e) {
            throw new Py4JJavaException(
                    String.format("Could not create file '%s' as output source", destFile), e);
        }
        try {
            try {
                svgFromJsonExporter.export(jsonOptions, null, ostream);
            } catch (RhinoException re) {
                re.printStackTrace();
                throw new ExtendedScriptException(re);
            }
        } catch (ExtendedScriptException e) {
            throw new Py4JJavaException(e.getMessage(), e);
        }
    }
    
    public byte[] export(String jsonOptions) throws Py4JJavaException {
        String svgString = export_to_svg(jsonOptions);
        return rasterizer.convert(svgString);
    }
    
    public void export(String jsonOptions, String destFile) throws Py4JJavaException {
        String svgString = export_to_svg(jsonOptions);
        rasterizer.convert(svgString, destFile);
    }
}