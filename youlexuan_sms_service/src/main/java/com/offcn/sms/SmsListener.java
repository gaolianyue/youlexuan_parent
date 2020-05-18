package com.offcn.sms;

import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.offcn.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component("smsListener")
public class SmsListener implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;
    @Override
    public void onMessage(Message message) {
        if(message instanceof MapMessage){
            MapMessage map=(MapMessage)message;
            try {
                System.out.println("收到短信发送请求---》" +
                        "mobile:"+map.getString("mobile")+
                        " template_code:"+map.getString("template_code")+
                        " sign_name:"+map.getString("sign_name")+
                        " param:"+map.getString("param"));

                smsUtil.sendSms(map.getString("mobile"),
                        map.getString("template_code"),
                        map.getString("sign_name"),
                        map.getString("param"));
//                System.out.println("data:"+response.getData());

            } catch (ClientException e) {
                e.printStackTrace();
            } catch (JMSException e){
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

    }
}
