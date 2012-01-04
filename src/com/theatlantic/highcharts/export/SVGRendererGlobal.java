package com.theatlantic.highcharts.export;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;

public class SVGRendererGlobal extends ImporterTopLevel {
    
    boolean initialized;
    
    public SVGRendererGlobal() { }
    
    public SVGRendererGlobal(Context cx) {
        init(cx);
    }

    public SVGRendererGlobal(ContextFactory factory) {
        init(factory);
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public void init(Context cx) {
        initStandardObjects(cx, false);
        initialized = true;
    }
    
    public void init(ContextFactory factory) {
        factory.call(new ContextAction() {
            @Override        
            public Object run(Context cx) {
                init(cx);
                return null;
            }
        });
   }
}