package com.theatlantic.autograph.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.theatlantic.autograph.py4j.HighchartsExport;
import com.theatlantic.autograph.py4j.SVGRasterizer;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Main {

    private static class OutputTypeValidator implements IParameterValidator {
        public void validate(String name, String value) throws ParameterException {
            if (!(value.equals("svg")) && !(value.equals("png"))) {
                throw new ParameterException(String.format(
                    "Parameter %s must be either svg or png (found %s)", name, value));
            }
        }
    }

    private static class InputTypeValidator implements IParameterValidator {
        public void validate(String name, String value) throws ParameterException {
            if (!(value.equals("svg")) && !(value.equals("json"))) {
                throw new ParameterException(String.format(
                    "Parameter %s must be either svg or json (found %s)", name, value));
            }
        }
    }
    
    private static class JCommanderParams {
        @Parameter(arity = 2, description="[Input file] [Output file]")
        public List<String> files = new ArrayList<String>();
        
        @Parameter(names = {"--in"}, required = true,
               validateWith = InputTypeValidator.class,
                description = "Input type (svg or json)")
        public String inputType;
        
        @Parameter(names = {"--out"}, required = true,
               validateWith = OutputTypeValidator.class,
                description = "Output type (svg or png)")
        public String outputType;
        
        @Parameter(names = { "--width", "-w" }, description = "Width, if rasterizing")
        public Integer width;
        
        @Parameter(names = { "--height", "-h" }, description = "Height, if rasterizing")
        public Integer height;
        
        @Parameter(names = { "--help", "-?" }, description = "Command Usage")
        public boolean help = false;
    }
    
    public static void main(String[] args) {
        JCommanderParams params = new JCommanderParams();
        JCommander commander = new JCommander(params);
        try {
            commander = new JCommander(params, args);
        } catch (ParameterException e) {
            System.err.println("Error: " + e.getMessage());
            commander.usage();
            System.exit(1);
        }
        if (params.help) {
            commander.usage();
            System.exit(1);
        }
        if (params.inputType.equals("svg")) {
            if (params.outputType.equals("svg")) {
                throw new ParameterException("Cannot convert from svg to svg.");
            }
        }
        String inputFilePath = params.files.get(0);
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            throw new ParameterException(String.format(
                "Could not find input file '%s'", inputFilePath));
        }
        String outputFilePath = params.files.get(1);
        File outputFile = new File(outputFilePath);
        
        if (params.inputType.equals("svg")) {
            SVGRasterizer rasterizer = new SVGRasterizer();
            if (params.width != null) {
                rasterizer.set_width(params.width);
            }
            if (params.height != null) {
                rasterizer.set_height(params.height);
            }
            rasterizer.convert_file(inputFilePath, outputFilePath);
            System.out.println("Successfully converted svg to png");
        } else if (params.inputType.equals("json")) {
            HighchartsExport exporter = new HighchartsExport();
            if (params.width != null) {
                exporter.set_width(params.width);
            }
            if (params.height != null) {
                exporter.set_height(params.height);
            }
            if (params.outputType.equals("svg")) {
                exporter.export_file_to_svg(inputFilePath, outputFilePath);
                System.out.println("Successfully converted json to svg");
            } else if (params.outputType.equals("png")) {
                exporter.export_file(inputFilePath, outputFilePath);
                System.out.println("Successfully converted json to png");
            }
        }
    }

}
