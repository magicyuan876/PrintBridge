package com.genscript.print;

import com.genscript.print.dto.PrintDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PrintUtils {

    public static void printWithOutDialog(List<PrintDTO> printDTOList) {

        PrintServerUI.PRINT_LIST.clear();
        for (PrintDTO printDTO : printDTOList) {
            try {
                System.out.println(printDTO.getFileUrl());
                URL url = new URL(printDTO.getFileUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                PDDocument document = PDDocument.load(connection.getInputStream());
                PrintServerUI.PRINT_LIST.add(printDTO);
                PrintServerUI.jList.setListData(PrintServerUI.PRINT_LIST.toArray());
                //打印本地文件
                //                 PDDocument document = PDDocument.load(new File(filaPath));
                PrinterJob job = PrinterJob.getPrinterJob();

                PageFormat pageFormat = new PageFormat();
                if (printDTO.isLandscape()) {
                    pageFormat.setOrientation(PageFormat.LANDSCAPE);
                }

                if(StringUtils.isNotBlank(printDTO.getFileName())){
                    job.setJobName(printDTO.getFileName());
                }

                // override the page format
                Book book = new Book();
                // append all pages
                book.append(new PDFPrintable(document, Scaling.SCALE_TO_FIT), pageFormat, document.getNumberOfPages());
                job.setPageable(book);

                job.print();

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public static void printWithDialog(List<PrintDTO> printDTOList) {
        for (PrintDTO printDTO : printDTOList) {
            try {
                System.out.println(printDTO.getFileUrl());
                URL url = new URL(printDTO.getFileUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                PDDocument document = PDDocument.load(connection.getInputStream());
                PrinterJob job = PrinterJob.getPrinterJob();
                //打印本地文件
                PageFormat pageFormat = new PageFormat();
                if (printDTO.isLandscape()) {
                    pageFormat.setOrientation(PageFormat.LANDSCAPE);
                }

                // override the page format
                Book book = new Book();
                // append all pages
                if(StringUtils.isNotBlank(printDTO.getFileName())){
                    job.setJobName(printDTO.getFileName());
                }
                book.append(new PDFPrintable(document, Scaling.SCALE_TO_FIT), pageFormat, document.getNumberOfPages());
                job.setPageable(book);

                boolean a = job.printDialog();
                if (a) {
                    job.print();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
