package com.example.demo.controller;

import com.example.demo.Component.classifier.NaiveBayesComponent;
import com.example.demo.extendService.otherService.PythonService;
import com.example.demo.model.params.VocabularyInfo;
import com.example.demo.test.Test;
import com.example.demo.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("params")
public class ParamsController {

    @Autowired
    private PythonService pythonService;
    @Autowired
    private Test test;

    @RequestMapping("vocabulary")
    public VocabularyInfo getVocabularyInfo(){
        NaiveBayesComponent naiveBayesComponent = (NaiveBayesComponent) SpringContextUtils.getBean("naiveBayesComponent");

        System.out.println(test);
        return pythonService.getVocabularyInfo();
    }
}
