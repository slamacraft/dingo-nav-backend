package com.dingdo.controller;

import com.dingdo.extendService.otherService.PythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("params")
public class ParamsController {

    @Autowired
    private PythonService pythonService;


}
