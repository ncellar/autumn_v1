package com.norswap.autumn.parsing.debugger.gui;

import com.norswap.util.Strings;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class GUIMain
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String h20 = Strings.times(120, "hello word;");
        Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        Dimension _800x600 = new Dimension(800, 600);

        JTextPane textPane = new JTextPane();
        textPane.setText(h20);
        textPane.setEditable(true);
        textPane.setFont(mono);

        JScrollPane scrollPane = new JScrollPane(textPane);
        TextLineNumber tln = new TextLineNumber(textPane);
        scrollPane.setRowHeaderView(tln);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(_800x600);
        scrollPane.setMinimumSize(_800x600);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
