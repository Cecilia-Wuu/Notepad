package com.notepad.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class Notepad extends JFrame {

//    private static final long serialVersionUID = -8533346717300013642L;
    private JTextArea jta;//文本域
    private JScrollPane jsp;//带滚动条的面板容器
    private static String name;
    private JFileChooser jfc = new JFileChooser();

    public static void main(String[] args) {
        new Notepad().init();
    }

    // 初始化界面
    public void init() {
        this.setSize(800, 600);
//        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        name = "新建文本文档.txt";
        // 设置文件名
        this.setTitle(name + " - 记事本");
        // 添加菜单栏
        this.addMenu();
        // 添加文本框
        jta = new JTextArea();
        // 添加滚动条
        jsp = new JScrollPane(jta);
        jsp.setPreferredSize(new Dimension(780, 550));
        this.add(jsp, BorderLayout.CENTER);
        this.setVisible(true);// 设置窗口可见
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (showSaveDialog() !=JOptionPane.CANCEL_OPTION)// 若不是取消按钮，关闭程序
                {
                    setDefaultCloseOperation(3);
                }
            }
        });
    }

    // 添加菜单栏
    private void addMenu() {
        // 添加菜单栏
        JMenuBar jmb = new JMenuBar();
        // 设置菜单栏位置在顶级窗口的顶部
        setJMenuBar(jmb);
        // 菜单栏菜单
        JMenu jm_file = new JMenu("File");
        JMenu jm_edit = new JMenu("Edit");
        jmb.add(jm_file);
        jmb.add(jm_edit);
        // 给文件菜单添加菜单项
        JMenuItem item1 = new JMenuItem("open");
        JMenuItem item2 = new JMenuItem("save");
        jm_file.add(item1);
        jm_file.add(item2);

        // "打开"菜单项添加动作监听器
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 是否保存对原文件修改
                if (showSaveDialog() == JOptionPane.CANCEL_OPTION) {// 取消按钮，则返回
                    return;
                }
                // 弹出一个 "Open File" 文件选择器对话框
                int select = jfc.showOpenDialog(Notepad.this);
                // 选择打开文件，则读写文件
                if (select == JFileChooser.APPROVE_OPTION) {
                    jta.setText(readFile());// 写入文本框
                    jta.setCaretPosition(0);// 定位光标至行首
                }
            }
        });

        // “保存”菜单项添加动作监听器
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jfc.getSelectedFile() == null) {
                    createFile();//调用创建文件方法
                } else {
                    showSaveDialog();//调用文件是否保存方法
                }
            }
        });
    }

    // 读取文件
    private String readFile() {
        // 声明缓冲字符流变量
        BufferedReader br = null;
        StringBuilder sb = null;
        try {
             br = new BufferedReader(new FileReader(jfc.getSelectedFile()));
             sb = new StringBuilder();// 创建缓冲字符串
            String str;
            int count = 0;//控制添加换行符，与原文保持一致
            while ((str = br.readLine()) !=null) {
                if (count == 0)
                    sb.append(str);
                if (count++ != 0)
                    sb.append("\n" + str);// 添加换行
            }
        } catch (FileNotFoundException e1) {
            // 弹出“文件未找到”对话框，返回null
            JOptionPane.showMessageDialog(null, "未找到该文件！");
            return null;
        } catch (IOException e1) {
            // 弹出“文件读取异常”对话框，返回null
            JOptionPane.showMessageDialog(null, "文件读取异常");
            return null;
        } finally {
            // 关闭字符流
            if (br != null)
                try {
                    br.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
        name = jfc.getSelectedFile().getName();
        setTitle(name + " - 记事本");//设置文件名
        return sb.toString();
    }

    // 保存对话框
    private int showSaveDialog() {
        int select = 0;
        // 判断文本框是否为空
        if (!jta.getText().equals("")) {
            // 判断文件是否为空
            if (jfc.getSelectedFile() == null) {
                select = JOptionPane.showConfirmDialog(Notepad.this, "是否保存修改?");
                if (select == JOptionPane.YES_OPTION) {
                    createFile();//文件不存在，则创建文件
                }
            } else {
                if (!isSaved()) {// 文件未保存
                    select = JOptionPane.showConfirmDialog(Notepad.this, "是否保存修改?");
                    if (select == JOptionPane.YES_OPTION) {
                        saveFile(jfc.getSelectedFile());
                    }
                }
            }
        }
        return select;// 返回选项
    }

    // 创建新文件
    private void createFile() {
        // String name = null;
        File file = null;
        // 选择保存或取消
        if (jfc.showSaveDialog(Notepad.this) == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();// 获取选中的文件
        } else {
            return;
        }
        name = jfc.getName(file);// 获取输入的文件名
        if (file.exists()) { // 若选择已有文件----询问是否要覆盖
            int i = JOptionPane.showConfirmDialog(null, "该文件已存在,是否覆盖原文件", "确认", JOptionPane.YES_NO_OPTION);
            if (i == JOptionPane.YES_OPTION) {
                saveFile(file);
            } else {
                jfc.showSaveDialog(Notepad.this);// 重新选择
            }
        } else {//文件不存在，则直接保存
            saveFile(file);
        }
    }

    // 判断文件是否保存
    private Boolean isSaved() {
        // 比较内容
        if (jta.getText().equals(readFile())) {
            return true;
        }
        return false;
    }

    // 保存文件,文件不存在则创建新文件
    private void saveFile(File file) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(jta.getText());//写入文件
            bw.flush();
        } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(Notepad.this, "文件保存出错" + e1.getMessage());
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();//关闭缓冲流
            } catch (IOException e1) {
            }
        }
    }
}
