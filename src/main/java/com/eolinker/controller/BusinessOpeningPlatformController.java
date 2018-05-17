package com.eolinker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *      1.调用平台的对外开放的接口
 *      2.以map接收参数
 *          规定map中  key    需要返回的字段
 *                    value
 *
 *      3.以同样的map返回  key需要返回的字段
 *                       value 平台经过处理的地方的对应数据
 * Create by 向威 on 2018/5/11
 */
@Controller
@RequestMapping("/businessOpeningPlatform")
public class BusinessOpeningPlatformController {

    @RequestMapping(value ="")
    public Map businessOpeningPlatform(Map requestMap, HttpServletRequest request) throws Exception {
            HttpSession session = request.getSession(true);
            Map responseMap = (Map)session.getAttribute("userName");
            Set requestSet= requestMap.entrySet();
        Set responseSet = responseMap.entrySet();

        if (requestMap.size()==0){
            return responseMap;
        }
        //进行测试
        if (responseSet == null||responseSet.size() == 0||responseSet.size()!=requestSet.size()) {
            throw new Exception("结果不一致");

        }
        Iterator requestIterator = requestSet.iterator();
        Iterator responseIterator = responseSet.iterator();

        while (requestIterator.hasNext()) {
            if (!requestSet.contains(responseIterator.next())) {
                throw new Exception("结果不一致");
            }
        }
        return responseMap;
    }

}
