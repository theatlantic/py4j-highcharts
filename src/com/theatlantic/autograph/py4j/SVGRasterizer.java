package com.theatlantic.autograph.py4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.FileNotFoundException;

import java.net.MalformedURLException;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;


import py4j.Py4JJavaException;

/**
 * Uses the Batik <tt>PNGTranscoder</tt> API to rasterize SVGs to PNG images.
 *
 * @author <a href="https://github.com/fdintino">Frankie Dintino</a>
 * @version $Id$
 */
public class SVGRasterizer {
    
    /**
     * The PNG transcoder created by the constructor.
     */
    protected PNGTranscoder transcoder;
    
    protected float width;
    
    protected float height;
    
    /**
     * Constructs a new <tt>SVGRasterizer</tt>
     */
    public SVGRasterizer() {
        transcoder = new PNGTranscoder();
        // Turn off sRGB
        transcoder.addTranscodingHint(PNGTranscoder.KEY_GAMMA, new Float(0.0F));
        width = -1;
        height = -1;
    }
    
    /**
     * Lets you specify the raster image width.
     *
     * This will override the width attribute on the root svg element.
     * If the raster image height is not provided (using
     * <tt>set_height()</tt>), the transcoder will compute the raster image
     * height by keeping the aspect ratio of the SVG document.
     *
     * @param w the desired raster image width.
     */
    public void set_width(Integer w) {
        width = w.floatValue();
    }
    
    /**
     * Lets you specify the raster image width.
     *
     * This will override the width attribute on the root svg element.
     * If the raster image height is not provided (using
     * <tt>set_height()</tt>), the transcoder will compute the raster image
     * height by keeping the aspect ratio of the SVG document.
     *
     * @param w the desired raster image width.
     */
    public void set_width(Double w) {
        width = w.floatValue();
    }
    
    /**
     * Lets you specify the raster image height.
     *
     * This will override the height attribute on the root svg element.
     * If the raster image width is not provided (using
     * <tt>set_width()</tt>), the transcoder will compute the raster image
     * width by keeping the aspect ratio of the SVG document.
     *
     * @param h the desired raster image height.
     */
    public void set_height(Integer h) {
        height = h.floatValue();
    }
    
    /**
     * Lets you specify the raster image height.
     *
     * This will override the height attribute on the root svg element.
     * If the raster image width is not provided (using
     * <tt>set_width()</tt>), the transcoder will compute the raster image
     * width by keeping the aspect ratio of the SVG document.
     *
     * @param h the desired raster image height.
     */
    public void set_height(Double h) {
        height = h.floatValue();
    }
    
    protected void preConvert() {
        if (width > 0) {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
            width = -1;
        }
        if (height > 0) {
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);
            height = -1;
        }
    }

    /**
     * Takes the string contents of an SVG document and returns a
     * byte array of the transcoded PNG.
     *
     * py4j converts this return value to a Python bytearray (2.7+) or bytes (3.x)
     *
     * @param svgString the string contents of an SVG document.
     * @return The transcoded PNG.
     * @throws Py4JJavaException if an error occurred during the conversion
     */
    public byte[] convert(String svgString) throws Py4JJavaException {
        preConvert();
        StringReader stringSource = new StringReader(svgString);
        TranscoderInput input = new TranscoderInput(stringSource);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);
        try {
            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            throw new Py4JJavaException("Error while transcoding", e);
        }
        try {
            ostream.flush();
        } catch (IOException e) {
            throw new Py4JJavaException("Error while transcoding", e);
        }
        return ostream.toByteArray();
    }
    
    /**
     * Takes the string contents of an SVG document and saves a transcoded PNG
     * of it to the specified destination file.
     *
     * @param svgString the string contents of an SVG document.
     * @param destFile the file to which the transcoded PNG should be saved.
     * @throws Py4JJavaException if an error occurred during the conversion
     */
    public void convert(String svgString, String destFile) throws Py4JJavaException {
        preConvert();
        StringReader stringSource = new StringReader(svgString);
        TranscoderInput input = new TranscoderInput(stringSource);
        FileOutputStream ostream;
        try {
            ostream = new FileOutputStream(new File(destFile));
        } catch (FileNotFoundException e) {
            throw new Py4JJavaException(
                    String.format("Could not create file '%s' as output source", destFile), e);
        }
        
        TranscoderOutput output = new TranscoderOutput(ostream);
        try {
            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            throw new Py4JJavaException("Error while transcoding", e);
        }
        try {
            ostream.flush();
            ostream.close();
        } catch (IOException e) {
            throw new Py4JJavaException("Error while writing file to disk", e);
        }
    }
    
    /**
     * Takes an SVG file location and returns a <tt>ByteArrayOutputStream</tt> of the
     * transcoded PNG.
     *
     * py4j converts this return value to a Python bytearray (2.7+) or bytes (3.x)
     *
     * @param svgFile the path to the SVG file.
     * @return a <tt>ByteArrayOutputStream</tt> of the transcoded PNG.
     * @throws Py4JJavaException if an error occurred during the conversion
     */
    public byte[] convert_file(String svgFile) throws Py4JJavaException {
        preConvert();
        String svgURI;
        try {
            svgURI = new File(svgFile).toURI().toURL().toString();
        } catch (MalformedURLException e) {
            throw new Py4JJavaException(
                    String.format("Error with source file name '%s'", svgFile), e);
        }
        
        TranscoderInput input;
        try {
            input = new TranscoderInput(svgURI);
        } catch (Exception e) {
            throw new Py4JJavaException(
                    String.format("Could not use file '%s' as input source", svgURI), e);
        }
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);
        try {
            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            throw new Py4JJavaException("Error while transcoding", e);
        }
        try {
            ostream.flush();
        } catch (IOException e) {
            throw new Py4JJavaException("Error while transcoding", e);
        }
        return ostream.toByteArray();
    }
    
    /**
     * Takes an SVG file location and saves a transcoded PNG of it to the
     * specified destination file.
     *
     * @param svgFile the path to the SVG file.
     * @param destFile the file to which the transcoded PNG should be saved.
     * @throws Py4JJavaException if an error occurred during the conversion
     */
    public void convert_file(String svgFile, String destFile) throws Py4JJavaException {
        preConvert();
        String svgURI;
        try {
            svgURI = new File(svgFile).toURI().toURL().toString();
        } catch (MalformedURLException e) {
            throw new Py4JJavaException(
                    String.format("Error with source file name '%s'", svgFile), e);
        }
        
        TranscoderInput input = new TranscoderInput(svgURI);
        FileOutputStream ostream;
        try {
            ostream = new FileOutputStream(new File(destFile));
        } catch (FileNotFoundException e) {
            throw new Py4JJavaException(
                    String.format("Could not create file '%s' as output source", destFile), e);
        }
        
        TranscoderOutput output = new TranscoderOutput(ostream);
        try {
            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            throw new Py4JJavaException("Error while transcoding", e);
        }
        try {
            ostream.flush();
            ostream.close();
        } catch (IOException e) {
            throw new Py4JJavaException("Error while writing file to disk", e);
        }
    }
    
}
