package com.offcn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.jms.*;

@RestController
public class TestSmsController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination smsDestination;

    @RequestMapping("/sendsms")
    public String sendMsg(String mobile,String code){

        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage map=session.createMapMessage();
                map.setString("mobile", mobile);
                map.setString("template_code", "SMS_182360300");
                map.setString("sign_name", "丛生草服务");
                map.setString("param", "{\"code\":\""+code+"\"}");
                /*map.setString("param", code);*/
                return map;
            }
        });

        return "send ok";

    }
}
