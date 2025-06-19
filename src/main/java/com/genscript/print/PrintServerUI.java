package com.genscript.print;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.genscript.print.dto.PrintDTO;
import com.genscript.print.ui.PrintMainFrame;

/**
 * PrintServerUI兼容性适配器
 * 保持向后兼容的同时委托给新的架构
 * 
 * @author Magic_yuan
 * @version 2.0.0
 * @deprecated 建议使用 {@link PrintMainFrame} 替代
 */
@Deprecated
public class PrintServerUI {

    private static final Logger logger = LoggerFactory.getLogger(PrintServerUI.class);
    /**
     * @deprecated 直接使用 {@link PrintMainFrame} 替代
     */
    @Deprecated
    public static JList jList = new JList();
    // 保持向后兼容的静态引用
    private static PrintMainFrame mainFrame;
    /**
     * @deprecated 使用 {@link PrintMainFrame#getPrintQueueModel()} 替代
     */
    @Deprecated
    public static volatile List<PrintDTO> PRINT_LIST = new ArrayList<PrintDTO>() {

        @Override
        public boolean add(PrintDTO printDTO) {
            if (mainFrame != null) {
                mainFrame.addPrintTask(printDTO);
                return true;
            }
            return super.add(printDTO);
        }

        @Override
        public void clear() {
            if (mainFrame != null) {
                mainFrame.clearPrintQueue();
            }
            super.clear();
        }

        @Override
        public PrintDTO[] toArray() {
            if (mainFrame != null) {
                List<PrintDTO> tasks = mainFrame.getPrintQueueModel().getAllPrintTasks();
                return tasks.toArray(new PrintDTO[0]);
            }
            return super.toArray(new PrintDTO[0]);
        }
    };
    /**
     * @deprecated 使用 {@link PrintMainFrame#getErrorTasks()} 替代
     */
    @Deprecated
    public static volatile List<PrintDTO> ERROR_PRINT_LIST = new ArrayList<PrintDTO>() {

        @Override
        public PrintDTO[] toArray() {
            if (mainFrame != null) {
                List<PrintDTO> errorTasks = mainFrame.getErrorTasks();
                return errorTasks.toArray(new PrintDTO[0]);
            }
            return super.toArray(new PrintDTO[0]);
        }
    };

    /**
     * 兼容性主方法
     * @deprecated 使用 {@link PrintApplication#main(String[])} 替代
     */
    @Deprecated
    public static void main(String[] args) {
        logger.warn("PrintServerUI.main() is deprecated. Use PrintApplication.main() instead.");

        // 委托给新的应用程序入口
        PrintApplication.main(args);
    }

    /**
     * 获取主窗口引用
     */
    public static PrintMainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * 设置主窗口引用（供PrintMainFrame调用）
     */
    public static void setMainFrame(PrintMainFrame frame) {
        mainFrame = frame;

        // 更新jList引用以保持兼容性
        if (frame != null) {
            // 这里可以设置一个代理JList，但为了简化，我们保持原有的jList
            // 实际应用中，外部代码应该迁移到新的API
        }
    }
}