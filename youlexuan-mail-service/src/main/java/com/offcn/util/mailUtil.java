package com.offcn.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Controller
@RequestMapping("/mail")
public class mailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    @RequestMapping("/sendMail")
    public Object sendMail(){
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper ;
        Properties prop = new Properties();
        String from;
        try {
            //从配置文件中拿到发件人邮箱地址
            prop.load(this.getClass().getResourceAsStream("/properties/mail.properties"));
            from = prop.get("mail.smtp.username")+"";
            helper=new MimeMessageHelper(message,true);
            helper.setFrom(from);//发件人邮箱
            helper.setTo("gaolianyue815@163.com");//收件人邮箱
            helper.setSubject("优乐选商城注册信息");//邮件的主题
            helper.setText("<p>注册成功</p> +" +
                    "<a href='http://localhost:9106/register.html'>登录界面</a><br/>" ,true);//邮件的文本内容，true表示文本以html格式打开
            javaMailSender.send(message);//发送邮件
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "发送成功";
    }

}
