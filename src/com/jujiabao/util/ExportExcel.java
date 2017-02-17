package com.jujiabao.util;
 
import java.io.File;
import java.io.IOException;
import java.util.List;
 

import jxl.CellView;
import jxl.HeaderFooter;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
 
/**
 * 数据按照Excel表格式导出
 * @author Hello.Ju
 *
 */
public class ExportExcel {
    private String Title;
    private List<String> Header;
    private List<List<String>> Data;
 
    private WritableWorkbook workbook;
    private WritableSheet wsheet;
 
    private HeaderFooter pageHeader;
    private HeaderFooter pageFooter;
    private WritableFont titleFont;
    private WritableCellFormat titleFormat;
    private WritableFont headerFont;
	private WritableCellFormat headerFormat;
	private WritableFont contentFont;
	private WritableCellFormat contentFormat;

	{
		// 设置打印页头
		pageHeader = new HeaderFooter();
		pageHeader.getLeft().appendDate();
		pageHeader.getLeft().append(" ");
		pageHeader.getLeft().appendTime();
		pageHeader.getCentre().appendWorkSheetName();
		pageHeader.getRight().append("");

		// 设置打印页脚
		pageFooter = new HeaderFooter();
		pageFooter.getCentre().appendPageNumber();
		pageFooter.getCentre().append("/");
		pageFooter.getCentre().appendTotalPages();

		// 设置标题
		titleFont = new WritableFont(WritableFont.createFont("黑体"), 32,
				WritableFont.BOLD);
		titleFormat = new WritableCellFormat(titleFont);
		try {
			titleFormat.setAlignment(Alignment.CENTRE);
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		} catch (WriteException e) {
			e.printStackTrace();
		}

		// 创建表头样式
		headerFont = new WritableFont(WritableFont.createFont("宋体"), 14,
				WritableFont.BOLD);
		headerFormat = new WritableCellFormat(headerFont);
		try {
			headerFormat.setAlignment(Alignment.CENTRE);
			headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			headerFormat.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
 
		// 创建内容样式
		contentFont = new WritableFont(WritableFont.createFont("宋体"), WritableFont.DEFAULT_POINT_SIZE,
				WritableFont.NO_BOLD);
		contentFormat = new WritableCellFormat(contentFont);
		try {
			contentFormat.setAlignment(Alignment.CENTRE);
			contentFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			contentFormat.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
    }
 
    public ExportExcel() {
    };
 
    public ExportExcel(String title, List<String> header, List<List<String>> data) {
        super();
        Title = title;
        Header = header;
        Data = data;
    }
 
    public boolean save(File fileName) throws IOException {
        // 创建WorkBook 和 Sheet
        workbook = Workbook.createWorkbook(fileName);
        wsheet = workbook.createSheet(this.Title, workbook.getNumberOfSheets());
        // 设置工作表全局设定
        SheetSettings sheetSettings = wsheet.getSettings();
        sheetSettings.setHeader(this.pageHeader);
        sheetSettings.setFooter(this.pageFooter);
        // sheetSettings.setDefaultRowHeight(500);
        sheetSettings.setFitToPages(true);
        sheetSettings.setPrintHeaders(false);
        sheetSettings.setDisplayZeroValues(true);
        sheetSettings.setPrintGridLines(true);
        sheetSettings.setFitWidth(1);
        try {
            // 标题文字
            Label nc1 = new Label(0, 0, this.Title, titleFormat);
            // 合并单元格
            wsheet.mergeCells(0, 0, this.Header.size() - 1, 0);
            CellView view = new CellView();
            view.setAutosize(true);//设置行宽自动显示
            
            // 插入标题单元格
            wsheet.addCell(nc1);
            // 写入表头
            for (int i = 0; i < this.Header.size(); ++i) {
                Label nc = new Label(i, 1, this.Header.get(i), headerFormat);
                wsheet.addCell(nc);
                wsheet.setColumnView(i, view);
            }
            // 加入数据
            for (int i = 0; i < this.Data.size(); ++i) {
                for (int j = 0; j < this.Data.get(i).size(); ++j) {
                    Label nc = new Label(j, i + 2, this.Data.get(i).get(j), contentFormat);
                    wsheet.addCell(nc);
                    wsheet.setColumnView(j, view);
                }
            }
            workbook.write();
            workbook.close();
        } catch (RowsExceededException e) {
            e.printStackTrace();
            return false;
        } catch (WriteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return Title;
    }
 
    /**
     * @return the header
     */
    public List<String> getHeader() {
        return Header;
    }
 
    /**
     * @return the data
     */
    public List<List<String>> getData() {
        return Data;
    }
 
    /**
     * @return the workbook
     */
    public WritableWorkbook getWorkbook() {
        return workbook;
    }
 
    /**
     * @return the wsheet
     */
    public WritableSheet getWsheet() {
        return wsheet;
    }
 
    /**
     * @return the pageHeader
     */
    public HeaderFooter getPageHeader() {
        return pageHeader;
    }
 
    /**
     * @return the pageFooter
     */
    public HeaderFooter getPageFooter() {
        return pageFooter;
    }
 
    /**
     * @return the titleFont
     */
    public WritableFont getTitleFont() {
        return titleFont;
    }
 
    /**
     * @return the titleFormat
     */
    public WritableCellFormat getTitleFormat() {
        return titleFormat;
    }
 
    /**
     * @return the headerFont
     */
    public WritableFont getHeaderFont() {
        return headerFont;
    }
 
    /**
     * @return the headerFormat
     */
    public WritableCellFormat getHeaderFormat() {
        return headerFormat;
    }
 
    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        Title = title;
    }
 
    /**
     * @param header
     *            the header to set
     */
    public void setHeader(List<String> header) {
        Header = header;
    }
 
    /**
     * @param data
     *            the data to set
     */
    public void setData(List<List<String>> data) {
        Data = data;
    }
 
    /**
     * @param workbook
     *            the workbook to set
     */
    public void setWorkbook(WritableWorkbook workbook) {
        this.workbook = workbook;
    }
 
    /**
     * @param wsheet
     *            the wsheet to set
     */
    public void setWsheet(WritableSheet wsheet) {
        this.wsheet = wsheet;
    }
 
    /**
     * @param pageHeader
     *            the pageHeader to set
     */
    public void setPageHeader(HeaderFooter pageHeader) {
        this.pageHeader = pageHeader;
    }
 
    /**
     * @param pageFooter
     *            the pageFooter to set
     */
    public void setPageFooter(HeaderFooter pageFooter) {
        this.pageFooter = pageFooter;
    }
 
    /**
     * @param titleFont
     *            the titleFont to set
     */
    public void setTitleFont(WritableFont titleFont) {
        this.titleFont = titleFont;
    }
 
    /**
     * @param titleFormat
     *            the titleFormat to set
     */
    public void setTitleFormat(WritableCellFormat titleFormat) {
        this.titleFormat = titleFormat;
    }
 
    /**
     * @param headerFont
     *            the headerFont to set
     */
    public void setHeaderFont(WritableFont headerFont) {
        this.headerFont = headerFont;
    }
 
    /**
     * @param headerFormat
     *            the headerFormat to set
     */
    public void setHeaderFormat(WritableCellFormat headerFormat) {
        this.headerFormat = headerFormat;
    }
}

