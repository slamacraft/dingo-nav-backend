package com.dingdo.controller;

import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.extendService.otherService.PythonService;
import com.dingdo.model.params.VocabularyInfo;
import com.dingdo.test.Test;
import com.dingdo.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("params")
public class ParamsController {

    @Autowired
    private PythonService pythonService;

    @RequestMapping("vocabulary")
    public VocabularyInfo getVocabularyInfo(){
        NaiveBayesComponent naiveBayesComponent = (NaiveBayesComponent) SpringContextUtils.getBean("naiveBayesComponent");
        return pythonService.getVocabularyInfo();
    }
}
