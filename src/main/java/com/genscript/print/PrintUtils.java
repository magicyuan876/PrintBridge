package com.genscript.print;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.genscript.print.dto.PrintDTO;

public class PrintUtils {

    private static final Logger logger = LoggerFactory.getLogger(PrintUtils.class);

    /**
     * @deprecated 使用 {@link com.genscript.print.service.PrintService#printWithoutDialogAsync(List)} 替代
     */
    @Deprecated
    public static void printWithOutDialog(List<PrintDTO> printDTOList) {
        // 委托给新的服务层
        com.genscript.print.ui.PrintMainFrame mainFrame = PrintServerUI.getMainFrame();
        if (mainFrame != null) {
            com.genscript.print.service.PrintService printService = new com.genscript.print.service.PrintService(mainFrame.getPrintQueueModel());
            printService.printWithoutDialog(printDTOList);
        } else {
            logger.error("PrintMainFrame not initialized. Cannot print without dialog.");
        }
    }

    public static void printLocalFile(File[] files) {
        int i = 0;
        for (File file : files) {
            try {
                PDDocument document = PDDocument.load(file);
                //打印本地文件
                //                 PDDocument document = PDDocument.load(new File(filaPath));
                PrinterJob job = PrinterJob.getPrinterJob();

                //            job.setPageable(new PDFPageable(document));
                PageFormat pageFormat = new PageFormat();
                pageFormat.setOrientation(i);
                Paper paper = new Paper();

                PDRectangle pdrect = getRotatedCropBox(document.getPage(0));

                paper.setImageableArea(5,5,pdrect.getWidth(),pdrect.getHeight());
                pageFormat.setPaper(paper);

                // override the page format
                Book book = new Book();
                // append all pages
                book.append(new PDFPrintable(document, Scaling.SCALE_TO_FIT, false, 0.0f, true), pageFormat, document.getNumberOfPages());
                job.setPageable(book);

                job.print();
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    static PDRectangle getRotatedCropBox(PDPage page) {
        PDRectangle cropBox = page.getCropBox();
        int rotationAngle = page.getRotation();
        return rotationAngle != 90 && rotationAngle != 270 ? cropBox : new PDRectangle(cropBox.getLowerLeftY(), cropBox.getLowerLeftX(), cropBox.getHeight(), cropBox.getWidth());
    }

//    public static void main(String[] args) {
//
//        File[] files = new File[2];
//        files[0] = new File("D://sqd.pdf");
//        files[1] = new File("D://COA.pdf");
//        printLocalFile(files);
//    }

/**
 * @deprecated 使用 {@link com.genscript.print.service.PrintService#printWithDialogAsync(List)} 替代
 */
@Deprecated
    public static void printWithDialog(List<PrintDTO> printDTOList) {
        // 委托给新的服务层
        com.genscript.print.ui.PrintMainFrame mainFrame = PrintServerUI.getMainFrame();
        if (mainFrame != null) {
            com.genscript.print.service.PrintService printService = new com.genscript.print.service.PrintService(mainFrame.getPrintQueueModel());
            printService.printWithDialog(printDTOList);
        } else {
            logger.error("PrintMainFrame not initialized. Cannot print with dialog.");
        }
    }
}
