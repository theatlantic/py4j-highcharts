package com.theatlantic.autograph.py4j;

import javax.script.ScriptException;

import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;

/**
 * An extension of javax.script.ScriptException that allows
 * the cause of an exception to be set.
 */
public class ExtendedScriptException extends ScriptException {
    
    private static final long serialVersionUID = 1L;
      
    private ExtendedScriptException(
            Throwable cause,
            String message,
            String fileName,
            int lineNumber,
            int columnNumber) {
        super(message, fileName, lineNumber, columnNumber);
        this.initCause(cause);
    }
    
    private static class FakeRhinoException extends Exception {
        
        private static final long serialVersionUID = 1L;
        
        public int lineNumber;
        public int columnNumber;
        public String message;
        public String fileName;

        public FakeRhinoException(Exception e) {
            if (e instanceof RhinoException) {
                RhinoException re = (RhinoException) e;
                fileName = re.sourceName();
                lineNumber = (lineNumber = re.lineNumber()) == 0 ? -1 : lineNumber;
                columnNumber = (columnNumber = re.columnNumber()) == 0 ? -1 : columnNumber;
            }
            if (e instanceof EcmaError) {
                EcmaError ee = (EcmaError) e;
                message = String.format("[%s] %s: %s (%s:%d col %d)\n%s", ee.getName(), ee.getErrorMessage(), ee.details(), ee.sourceName(), ee.lineNumber(), ee.columnNumber(), ee.getScriptStackTrace());
            } else if (e instanceof JavaScriptException) {
                JavaScriptException jse = (JavaScriptException) e;
                Object value = jse.getValue();

                String valueMessage = (value != null && value.getClass().getName().equals("org.mozilla.javascript.NativeError") ?
                               value.toString() :
                               value.toString());
                message = String.format("%s: %s (%s:%d col %d)\n%s", valueMessage, jse.details(), jse.sourceName(), jse.lineNumber(), jse.columnNumber(), jse.getScriptStackTrace());
            } else {
                message = e.getMessage();
            }
        }

    }
    
    private static FakeRhinoException fakeRhinoException;
    
    private static FakeRhinoException makeFakeRhinoException(Exception e) {
        if (fakeRhinoException == null) {
            fakeRhinoException = new FakeRhinoException(e);
        }
        return fakeRhinoException;
    }

    public ExtendedScriptException(String s) {
        super(s);
    }
    
    public ExtendedScriptException(Exception e) {
        this(e,
            makeFakeRhinoException(e).message,
            makeFakeRhinoException(e).fileName,
            makeFakeRhinoException(e).lineNumber,
            makeFakeRhinoException(e).columnNumber);
        fakeRhinoException = null;
    }
    
    public ExtendedScriptException(String message, String fileName, int lineNumber) {
        super(message, fileName, lineNumber);
    }
    
    public ExtendedScriptException(Throwable cause, String message, String fileName, int lineNumber) {
        super(message, fileName, lineNumber);
        this.initCause(cause);
    }

    public ExtendedScriptException(String message,
            String fileName,
            int lineNumber,
            int columnNumber) {
        super(message, fileName, lineNumber, columnNumber);
    }

}