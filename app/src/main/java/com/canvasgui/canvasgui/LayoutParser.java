package com.canvasgui.canvasgui;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marv on 11/10/2016.
 */

public class LayoutParser {

    private Context context;
    private XmlPullParser xmlParser;

    public LayoutParser(Context currentContext, XmlPullParser xmlParser) {
        this.context = currentContext;
        this.xmlParser = xmlParser;
    }

    public List<GUIElementDescription> retrieveLayout() throws XmlPullParserException, IOException {
        return parseXML(xmlParser);
    }

    private List parseXML(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        xmlParser.next();
        return readInput(xmlParser);
    }

    private List<GUIElementDescription> readInput(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<GUIElementDescription> entries = new ArrayList();

        //we expect the parsed xml meet the intended format, so namespace is null
        parser.require(XmlPullParser.START_TAG, null, "guiComponents");
        //read xml recursively
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                entries.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private GUIElementDescription readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");

        String type = null;
        String text = null;
        float textSize = 0.0f;
        int x = -1;
        int y= -1;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("type")) {
                type = readType(parser);
            } else if (name.equals("text")) {
                text = readText(parser);
            } else if (name.equals("x")) {
                x = Integer.parseInt(readXCoordinate(parser));
            } else if (name.equals("y")) {
                y = Integer.parseInt(readYCoordinate(parser));
            } else if (name.equals("textSize")) {
                textSize = Float.parseFloat(readTextSize(parser));
            } else {
                skip(parser);
            }
        }
        return new GUIElementDescription(type, text, textSize, x, y);
    }

    //retrieve type tags
    private String readType(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "type");
        String title = readElement(parser);
        parser.require(XmlPullParser.END_TAG, null, "type");
        return title;
    }

    //retrieve text tags
    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "text");
        String title = readElement(parser);
        parser.require(XmlPullParser.END_TAG, null, "text");
        return title;
    }

    //retrieve x coordinate
    private String readXCoordinate(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "x");
        String title = readElement(parser);
        parser.require(XmlPullParser.END_TAG, null, "x");
        return title;
    }

    //retrieve y coordinate
    private String readYCoordinate(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "y");
        String title = readElement(parser);
        parser.require(XmlPullParser.END_TAG, null, "y");
        return title;
    }

    //retrieve text size
    private String readTextSize(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "textSize");
        String title = readElement(parser);
        parser.require(XmlPullParser.END_TAG, null, "textSize");
        return title;
    }

    // read values of a tag
    private String readElement(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        //the text constant of @XmlPullParser is not to be confused with the text property of the android GUI components
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    //skip irrelevant xml elements
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        /*
         * Skipping an element means to traverse over its corresponding start and endtag.
         * depth will always be zero, if the end tag of an element that is to be skipped is reached.
         * Its children's start tags might increment @depth, however its end tags will eventually decrement
         * it to its original value. Thus, ignoring an xml element including its child elements is achieved
         */
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
