package com.ashok.demos.htmltopdfgenerator.controller;

import com.ashok.demos.htmltopdfgenerator.util.JsonUtil;
import com.ashok.demos.htmltopdfgenerator.util.ReportUtil;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.ashok.demos.htmltopdfgenerator.util.ReportUtil.JSON_PATH_LIST;
import static com.ashok.demos.htmltopdfgenerator.util.ReportUtil.SAMPLE_JSON;

@Controller
public class AppController {

    @GetMapping(value="/generateReport")
    public String parseJson(Model model){
            return ReportUtil.generateReport(model);
    }

    @SneakyThrows
    @GetMapping(value="/downloadPdf")
    public ResponseEntity<?> downloadPdf(){

        Map tableData = JsonUtil.generateReport(SAMPLE_JSON, JSON_PATH_LIST);
        String htmlContent = ReportUtil.parseThymeleafTemplate(tableData);
        byte[] pdfContent = ReportUtil.getPdfFromHtml(htmlContent);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);

    }
}
