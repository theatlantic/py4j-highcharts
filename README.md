# py4j-svg2png

**py4j-svg2png** is a Java application which starts a
[Py4J](http://py4j.sourceforge.net/) gateway server that allows python
applications to rasterize SVG images to PNG using the [Batik SVG
Toolkit](http://xmlgraphics.apache.org/batik/). It was created by developers
at [The Atlantic](https://github.com/theatlantic/).

## Installation

The simplest way to get started is to simply download [the latest compiled
jar](https://github.com/downloads/theatlantic/py4j-svg2png/py4j-svg2png-highcharts.jar).

If you wish to compile the jar yourself, run:

    $ ./build.sh

from the root of the checked out source directory (on windows, use `build.bat`
instead).

## Usage

To run the **py4j-svg2png** gateway server, simply call:

    $ java -jar py4j-svg2png.jar

This will set up a gateway server listening on port 25333 which py4j-python
can interact with.

Due to [a bug in Py4J](https://github.com/bartdag/py4j/issues/91), it is
necessary to install from a [patched version of
py4j-python](https://github.com/downloads/theatlantic/py4j/py4j-0.7.zip). This can be
done by unzipping and running through setup.py, or with pip:

    $ pip install https://github.com/downloads/theatlantic/py4j/py4j-0.7.zip

Once installed, the rasterizer methods can be invoked as follows:

```python
    green_square_svg = \
    """<?xml version="1.0" encoding="UTF-8"?>
    <svg xmlns="http://www.w3.org/2000/svg" version="1.0" width="400" height="400">
        <rect width="400" height="400" x="0" y="0" style="fill:green;" />
    </svg>
    """
    
    from py4j.java_gateway import JavaGateway
    gateway = JavaGateway()
    # get_rasterizer() returns an instance of
    # com.theatlantic.autograph.py4j.svg2png.SVGRasterizer
    rasterizer = gateway.get_rasterizer()
    
    # SVGRasterizer.convert() takes an svg string as its first argument.
    png_bytes = rasterizer.convert(green_square_svg)
    print typeof(png_bytes)
    # bytearray (python 2.7) or bytes (python 3.0+)
    
    # SVGRasterizer.convert() can optionally take an output file path as a second argument.
    rasterizer.convert(green_square_svg, "/path/to/output/green_square.png")
    
    # SVGRasterizer.convert_file() takes an absolute file path as its first argument.
    # It also can take a second output path argument.
    png_bytes = rasterizer.convert_file("/path/to/green_square.svg")
    f = open("/path/to/output/green_square.png", "wb")
    f.write(png_bytes)
    f.close()
```

And here is how one would call the Highcharts export methods:

```python
    json_opts = """{
        "chart": {
            "defaultSeriesType": "line",
            "width": 600,
            "height": 400
        },
        "title": { "text": "Tokyo Monthly Average Rainfall" },
        "xAxis": {
            "categories": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
        },
        "yAxis": { "title": { "text": "Rainfall (mm)" } },
        "series": [{ "data": [49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4] }]
    }"""

    from py4j.java_gateway import JavaGateway
    gateway = JavaGateway()
    # get_highcharts_exporter() returns an instance of
    # com.theatlantic.autograph.py4j.HighchartsExport
    exporter = gateway.get_highcharts_exporter()

    # HighchartsExport.export() takes a json string as its first argument.
    png_bytes = exporter.export(json_opts)
    print typeof(png_bytes)
    # bytearray (python 2.7) or bytes (python 3.0+)

    # HighchartsExport.export() can optionally take an output file path as a
    # second argument.
    exporter.export(json_opts, "/path/to/output/chart.png")

    # HighchartsExport.export_to_svg() takes the same arguments as
    # HighchartsExport.export(), but returns the svg string.
    # It also can take a second output path argument.
    svg = exporter.export_to_svg(json_opts)
    f = open("/path/to/output/chart.svg", "w")
    f.write(svg)
    f.close()   
```

## License

The Py4J Rasterizer gateway code is licensed under the [Simplified BSD
License](http://www.opensource.org/licenses/bsd-license.php) and is copyright
**The Atlantic Media Company**. View the LICENSE file under the root directory.

The Highcharts export code is derived from the work of
[one2team](https://github.com/one2team/highcharts-serverside-export), which
is licensed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

Copyright and licensing information of other third-party libraries can be found
within the lib directory.