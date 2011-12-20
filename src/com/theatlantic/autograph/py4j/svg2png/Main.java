package com.theatlantic.autograph.py4j.svg2png;

import py4j.GatewayServer;
import py4j.Py4JNetworkException;

/**
 * Initializes the Py4J Gateway server which allows access to
 * <tt>SVGRasterizer</tt> and its methods through py4j.
 *
 * @author <a href="https://github.com/fdintino">Frankie Dintino</a>
 * @version $Id$
 */
public class Main {
    
    /**
     * Returns a new instance of <tt>SVGRasterizer</tt> for the java gateway.
     *
     * @return A new <tt>SVGRasterizer</tt> instance.
     */
    public SVGRasterizer get_rasterizer() {
        return new SVGRasterizer();
    }
    
    /**
     * The main method, which creates an instance of this class and starts the
     * gateway server.
     */
    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new Main());
        try {
            gatewayServer.start();
        } catch (Py4JNetworkException e) {
            gatewayServer.shutdown();
            gatewayServer.start();
        }
        
        System.out.println("Gateway Server Started");
    }
}