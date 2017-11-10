package com.lsp.peis.browser;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.browser.Browser;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.MouseListener;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.layout.FormAttachment;
//import org.eclipse.swt.layout.FormData;
//import org.eclipse.swt.layout.FormLayout;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Text;
import org.apache.zookeeper.Shell;

import java.awt.AWTException;
import java.awt.*;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/7 10:26
 */
public class Broswer {

//    private Text text;
//    protected Shell shell;
//
//    /**
//     * Launch the application
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//        // try {
//        // CPBroswer window = new CPBroswer();
//        // window.open();
//        // } catch (Exception e) {
//        // e.printStackTrace();
//        // }
//        TrayIcon trayIcon = null;
//        if (SystemTray.isSupported()) // 判断系统是否支持系统托盘
//        {
//            SystemTray tray = SystemTray.getSystemTray(); // 创建系统托盘
//            java.awt.Image image = Toolkit.getDefaultToolkit().getImage(
//                    "C:\\Users\\Administrator\\Desktop\\1.jpg");// 载入图片,这里要写你的图标路径哦
//
//            ActionListener listener = new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    Broswer window = new Broswer();
//                    window.open();
//                }
//
//            };
//            // 创建弹出菜单
//            PopupMenu popup = new PopupMenu();
//            // 主界面选项
//            MenuItem mainFrameItem = new MenuItem("主界面");
//            mainFrameItem.addActionListener(listener);
//
//            // 退出程序选项
//            MenuItem exitItem = new MenuItem("退出程序");
//            exitItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    if (JOptionPane.showConfirmDialog(null, "确定退出系统") == 0) {
//                        System.exit(0);
//                    }
//                }
//            });
//
//            //聊天
//            MenuItem chatItem = new MenuItem("聊天");
//            chatItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    if (JOptionPane.showConfirmDialog(null, "确定退出系统") == 0) {
//                        System.exit(0);
//                    }
//                }
//            });
//
//
//            popup.add(mainFrameItem);
//            popup.add(chatItem);
//            popup.add(exitItem);
//
//
//            trayIcon = new TrayIcon(image, "清普", popup);// 创建trayIcon
//            trayIcon.addActionListener(listener);
//            try {
//                tray.add(trayIcon);
//            } catch (AWTException e1) {
//                e1.printStackTrace();
//            }
//        }
//
//    }
//
//    /**
//     * Open the window
//     */
//    public void open() {
//        final Display display = Display.getDefault();
//        createContents();
//        shell.open();
//        shell.layout();
//        while (!shell.isDisposed()) {
//            if (!display.readAndDispatch())
//                display.sleep();
//        }
//    }
//
//    /**
//     * Create contents of the window
//     */
//    protected void createContents() {
//        shell = new Shell();
//        shell.setLayout(new FormLayout());
//        shell.setMinimumSize(1024, 768);
//        // shell.setSize(1280, 800);
//        shell.setMaximized(true);
//        // shell.setFullScreen(true);
//        shell.setText("杭州清普区域卫生系统");
//        final Browser browser = new Browser(shell, SWT.NONE);
//
//        browser.addMouseListener(new MouseListener() {
//            public void mouseDoubleClick(MouseEvent arg0) {
//            }
//
//            public void mouseDown(MouseEvent event) {
//                // if (event.button == 3)
//                // browser.execute("document.oncontextmenu = function()
//                // {return false;}");
//            }
//
//            public void mouseUp(MouseEvent arg0) {
//            }
//
//        });
//
//        final FormData fd_browser = new FormData();
//        fd_browser.bottom = new FormAttachment(100, 0);
//        fd_browser.right = new FormAttachment(100, 0);
//        fd_browser.top = new FormAttachment(0, 0);
//        fd_browser.left = new FormAttachment(0, 0);
//
//        browser.setLayoutData(fd_browser);
//        // browser.setUrl("http://192.8.60.41:8080/his");
//        browser.setUrl("http://127.0.0.1:8080/his");
//
//        // final Menu menu = new Menu(shell, SWT.BAR);
//        // shell.setMenuBar(menu);
//        //
//        // final MenuItem newSubmenuMenuItem = new MenuItem(menu, SWT.CASCADE);
//        // newSubmenuMenuItem.setText("文件");
//        //
//        // final Menu menu_1 = new Menu(newSubmenuMenuItem);
//        // newSubmenuMenuItem.setMenu(menu_1);
//        //
//        // final MenuItem newItemMenuItem = new MenuItem(menu_1, SWT.NONE);
//        // newItemMenuItem.setText("退出");
//        //
//        // final MenuItem newItemMenuItem_1 = new MenuItem(menu, SWT.NONE);
//        // newItemMenuItem_1.setText("关于");
//        //
//        // text = new Text(shell, SWT.BORDER);
//        // final FormData fd_text = new FormData();
//        // fd_text.right = new FormAttachment(100, -62);
//        // fd_text.bottom = new FormAttachment(0, 25);
//        // fd_text.top = new FormAttachment(0, 0);
//        // fd_text.left = new FormAttachment(0, 0);
//        // text.setLayoutData(fd_text);
//        // text.setText(browser.getUrl());
//        //
//        // Button button;
//        // button = new Button(shell, SWT.NONE);
//        // final FormData fd_button = new FormData();
//        // fd_button.right = new FormAttachment(100, -6);
//        // fd_button.bottom = new FormAttachment(text, 22, SWT.TOP);
//        // fd_button.top = new FormAttachment(text, 0, SWT.TOP);
//        // button.setLayoutData(fd_button);
//        // button.setText("转到");
//    }
//


}
