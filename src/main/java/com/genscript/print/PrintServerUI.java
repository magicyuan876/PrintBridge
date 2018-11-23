package com.genscript.print;

import com.alibaba.fastjson.JSONObject;
import com.genscript.print.dto.PrintDTO;
import com.sun.awt.AWTUtilities;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintServerUI extends JFrame {

    public static volatile List<PrintDTO> PRINT_LIST = new ArrayList<PrintDTO>();

    public static volatile List<PrintDTO> ERROR_PRINT_LIST = new ArrayList<PrintDTO>();

    public static JList jList = new JList();

    public static void main(String[] args) {
        try {
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("RootPane.setupButtonVisible", false);
            BeautyEyeLNFHelper.translucencyAtFrameInactive = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        BorderLayout layout = new BorderLayout();
        final JFrame f = new JFrame();
        f.setLayout(layout);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JLabel text = new JLabel();
        final JLabel statusText = new JLabel();
        text.setText("SCM打印控件 v1.0");
        panel1.add(text);
        panel1.add(statusText);
        panel1.setPreferredSize(new Dimension(0, 50));
        jList.setPreferredSize(new Dimension(650, 0));

        JButton btn = new JButton("打印");
        btn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        panel1.add(btn);
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("点击了按钮呢");
                int[] indexs = jList.getSelectedIndices();

                if (jList.getModel().getSize() == 0) {
                    Object[] options = { "确定" };
                    JOptionPane pane2 = new JOptionPane("没有可以打印的文件", JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_OPTION, null, options, options[0]);
                    JDialog dialog = pane2.createDialog(f, "提示");
                    dialog.setVisible(true);
                } else if (indexs == null || indexs.length == 0) {
                    Object[] options = { "确定", "取消" };
                    JOptionPane pane2 = new JOptionPane("是否打印当前列表中的所有文件?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[1]);
                    JDialog dialog = pane2.createDialog(f, "提示");
                    dialog.setVisible(true);
                    Object selectedValue = pane2.getValue();
                    if (selectedValue == options[0]) {
                        List<PrintDTO> printDTOList = new ArrayList<PrintDTO>();
                        ListModel<PrintDTO> listModel = jList.getModel();
                        for (int i = 0; i < listModel.getSize(); i++) {
                            printDTOList.add(listModel.getElementAt(i));
                        }

                        PrintUtils.printWithDialog(printDTOList);
                    }
                } else {
                    List<PrintDTO> printDTOList = new ArrayList<PrintDTO>();
                    ListModel<PrintDTO> listModel = jList.getModel();
                    for (int index : indexs) {
                        printDTOList.add(listModel.getElementAt(index));
                    }

                    PrintUtils.printWithDialog(printDTOList);
                }

            }
        });

        MyJcheckBox cell = new MyJcheckBox();
        jList.setCellRenderer(cell);

        jList.setSelectionModel(new DefaultListSelectionModel() {

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });

        JScrollPane jScrollPane = new JScrollPane(jList);
        jScrollPane.setPreferredSize(new Dimension(700, 400));
        jScrollPane.setViewportView(jList);
        panel2.add(jScrollPane);
        panel2.setPreferredSize(new Dimension(800, 500));
        f.add(panel1, BorderLayout.NORTH);
        f.add(panel2, BorderLayout.CENTER);
        f.setSize(800, 600);
        f.setTitle("Print Tool");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(200, 200, 800, 600);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {

            public void windowOpened(WindowEvent e) {
                System.out.println("=====open============");
                boolean status = SingleFileHTTPServer.initServer();
                if (status) {
                    statusText.setText("服务开启成功");
                } else {
                    statusText.setText("服务开启失败");
                }
                return;
            }

            public void windowClosing(WindowEvent e) {
                System.out.println("=================");
                exit();
            }

            public void windowClosed(WindowEvent e) {

            }

            public void windowIconified(WindowEvent e) {

            }

            public void windowDeiconified(WindowEvent e) {

            }

            public void windowActivated(WindowEvent e) {

            }

            public void windowDeactivated(WindowEvent e) {

            }

            public void exit() {
                Object[] options = { "确定", "取消" };
                JOptionPane pane2 = new JOptionPane("退出之后无法监听浏览器打印", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[1]);
                JDialog dialog = pane2.createDialog(f, "警告");
                dialog.setVisible(true);
                Object selectedValue = pane2.getValue();
                if (selectedValue == null || selectedValue == options[1]) {
                    f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 这个是关键
                } else if (selectedValue == options[0]) {
                    f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                }
            }

        });
    }

}
