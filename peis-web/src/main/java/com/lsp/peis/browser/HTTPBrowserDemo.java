package com.lsp.peis.browser;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


import javax.swing.*;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/7 10:31
 */
public class HTTPBrowserDemo extends JFrame {

    private JLabel jlAddress, jlInfo;
    private JTextField jtfAddress;
    private JEditorPane jtpShow;
    private JPanel panel;
    private JButton btnGO;

    public static void main(String[] args) {
        HTTPBrowserDemo hbd = new HTTPBrowserDemo();
    }

    HTTPBrowserDemo() {
        jlAddress = new JLabel("地址");
        jlInfo = new JLabel();
        jtpShow = new JEditorPane();
        panel = new JPanel();
        jtfAddress = new JTextField(20);
        btnGO = new JButton("转到");

        add(panel, BorderLayout.NORTH);
        add(jtpShow, BorderLayout.CENTER);
        add(jlInfo, BorderLayout.SOUTH);

        panel.add(jlAddress);
        panel.add(jtfAddress);
        panel.add(btnGO);

        setVisible(true);
        setSize(350, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btnGO.addActionListener(new ShowHTMLListener());
        jtpShow.setEditable(false);
        jtpShow.addHyperlinkListener(new MyHyperlinkListener());
    }

    class ShowHTMLListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String str = jtfAddress.getText();
            try {
                if (!str.startsWith("http://")) {
                    str = "http://" + str;
                }
                jlInfo.setText("连接中...");
                URL address = new URL(str);
                jtpShow.setPage(address);
                jlInfo.setText("完成");
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e2) {
                // TODO: handle exception
            }
        }
    }

    class MyHyperlinkListener implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                JEditorPane pane = (JEditorPane) e.getSource();
                try {
                    pane.setPage(e.getURL());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }


}
