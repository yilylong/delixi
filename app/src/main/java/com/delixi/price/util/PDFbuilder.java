package com.delixi.price.util;

import android.content.Context;

import com.delixi.price.ElectricParts;
import com.delixi.price.OuterDBmanager;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 描述：
 * Created by zhaohl on 2017-4-1.
 */

public class PDFbuilder {


    public static void generatePDF(Context context,ElectricParts electricParts) {
        Document pdfDocument = new Document();
        try {
            PdfWriter.getInstance(pdfDocument,new FileOutputStream(OuterDBmanager.OUT_DIR+"/order.pdf"));
            pdfDocument.open();
            Font yaHeiFont = FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
            PdfPTable table = new PdfPTable(8);
            // first row
            table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Paragraph("物料号",yaHeiFont));
            table.addCell(new Paragraph("物料描述",yaHeiFont));
            table.addCell(new Paragraph("归属",yaHeiFont));
            table.addCell(new Paragraph("库存属性",yaHeiFont));
            table.addCell(new Paragraph("库存数量",yaHeiFont));
            table.addCell(new Paragraph("含税价格",yaHeiFont));
            table.addCell(new Paragraph("调整前价格",yaHeiFont));
            table.addCell(new Paragraph("调整后价格",yaHeiFont));
            // second row
            table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
            table.addCell(new Paragraph(electricParts.getMaterial_num(),yaHeiFont));
            table.addCell(new Paragraph(electricParts.getDescription(),yaHeiFont));
            table.addCell(new Paragraph(electricParts.getCategory(),yaHeiFont));
            table.addCell(new Paragraph(electricParts.getStock_properties(),yaHeiFont));
            table.addCell(electricParts.getPcs()+"");
            table.addCell(electricParts.getTax_price()+"");
            table.addCell(electricParts.getPrimary_price()+"");
            table.addCell(electricParts.getAdjust_price()+"");
            pdfDocument.add(table);
            pdfDocument.addTitle("德力西报价单");
            pdfDocument.addAuthor("delixi");
            pdfDocument.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
