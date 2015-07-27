package com.norswap.autumn.parsing.debug;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.Walks;
import com.norswap.util.graph_visit.GraphTransformer;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;

import static com.norswap.autumn.parsing.debug.Debugger.DEBUGGER;

public class GUI extends Application
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String file;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        String grammarFile = "src/com/norswap/autumn/test/grammars/Java8.autumn";

        DEBUGGER.grammar = Autumn.grammarFromFile(grammarFile)
            .walk(GraphTransformer.from(GUI::transform, Walks.inPlace));

        DEBUGGER.source = Source.fromFile("src/com/norswap/autumn/parsing/Parser.java");
        launch(args);
    }

    public static ParsingExpression transform(ParsingExpression pe)
    {
        Breakpoint out = new Breakpoint();
        out.operand = pe;
        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void start(Stage primaryStage)
    {
        WebView root = new WebView();
        WebEngine engine = root.getEngine();

        engine.load(
            ClassLoader.getSystemResource("debugger/debugger.html").toExternalForm());

        JSBridge bridge = new JSBridge();
        bridge._text = DEBUGGER.source.text().toString();


        engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) ->
        {
            // Note: there's also engine.documentProperty(), but this seems to be called last.

            if (newState == Worker.State.SUCCEEDED)
            {
                JSObject jsWindow = DEBUGGER.jsWindow = (JSObject) engine.executeScript("window");
                jsWindow.setMember("java", bridge);
                engine.executeScript("console.log = function(message) { java.log(message); };");
                engine.getDocument().getElementById("text").setTextContent(bridge._text);
                new Thread(DEBUGGER::start).start();
            }
            else if (newState == Worker.State.FAILED)
            {
                Throwable e = engine.getLoadWorker().getException();
                if (e != null)
                {
                    System.err.println("js exception while loading: " + e);
                }
            }
        });

        engine.setOnAlert(event -> System.out.println("Alert: " + event.getData()));

        primaryStage.setTitle("Autumn Debugger");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}