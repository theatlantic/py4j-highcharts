# py4j-svg2png

**py4j-svg2png** is a Java application which starts a
[Py4J](http://py4j.sourceforge.net/) gateway server that allows python
applications to rasterize SVG images to PNG using the [Batik SVG
Toolkit](http://xmlgraphics.apache.org/batik/). It was created by developers
at [The Atlantic](https://github.com/theatlantic/).

## Installation

The simplest way to get started is to simply download [the latest compiled
jar](https://github.com/downloads/theatlantic/py4j-svg2png/py4j-svg2png.jar).

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

## License

The Py4J Rasterizer gateway code is licensed under the [Simplified BSD
License](http://www.opensource.org/licenses/bsd-license.php) and is copyright
**The Atlantic Media Company**. View the LICENSE file under the root directory.
Copyright and licensing information of third-party libraries can be found
within the lib directory.
