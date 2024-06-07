package com.ashok.demos.htmltopdfgenerator.util;

import com.lowagie.text.DocumentException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.ui.Model;

public class ReportUtil {

    public static final String SAMPLE_JSON = """
                        {
                        	"secondLevel":
                        	[
                        			{
                        				"thirdLevel": {
                        					"fourthLevel": {
                        						"data": [
                        							"Ashok",
                        							"Kishor",
                        							"Ramakrishna"
                        						]
                        					}
                        				}
                        			},
                        			{
                        				"thirdLevel": [
                        				{
                        					"fourthLevel": {
                        						"data": [
                        							"Divya",
                        							"Navya",
                        							"Sireesha"
                        						]
                        					}
                        				},
                        					{
                        					"fourthLevel": {
                        						"data": [
                        							"Mathews",
                        							"Nicholas",
                        							"Bhulakshmi"
                        						]
                        					}
                        				}
                        				]
                        			},
                        		{
                        				"thirdLevel": {
                        					"fourthLevel": {
                        						"fifthLevel": {
                        							"data": [
                        								"Divya",
                        								"Navya",
                        								"Sireesha"
                        							]
                        						}
                        					}
                        				}
                        			}
                        	]
                        }
                        """;

    public static final List<String> JSON_PATH_LIST = Arrays.asList("secondLevel.thirdLevel[1].fourthLevel.data"
            ,"secondLevel.thirdLevel[0].fourthLevel.data"
            ,"secondLevel.thirdLevel[1].fourthLevel[1].data"
            ,"secondLevel[2].thirdLevel.fourthLevel.fifthLevel.data");

    public static void generatePdfFromHtml(String html) throws IOException, DocumentException {
        String outputFolder = System.getProperty("user.home") + File.separator + "thymeleaf.pdf";
        OutputStream outputStream = new FileOutputStream(outputFolder);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }

    public static byte[] getPdfFromHtml(String html) throws IOException, DocumentException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();

    }

    public static String parseThymeleafTemplate(Map dataTable ) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("tableData", dataTable);

        return templateEngine.process("templates/report", context);
    }

    public static String generateReport(Model model){
        try {

            Map tableData = JsonUtil.generateReport(SAMPLE_JSON, JSON_PATH_LIST);

            System.out.println("Entry Set: "+tableData.entrySet().toString());

            model.addAttribute("tableData",  tableData);

            String htmlContent = parseThymeleafTemplate(tableData);

            System.out.println("Generating Report");

            generatePdfFromHtml(htmlContent);

            return "report";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
