package org.mi.biz.tool.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mi.api.tool.dto.EmailDTO;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * @program: mi-community
 * @description:
 * @author: Micah
 * @create: 2020-12-14 18:19
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class MailHelper {

    private final Configuration configuration;

    private final JavaMailSender javaMailSender;

    private final MailProperties mailProperties;

    public void sendEmail(EmailDTO message,String templatePath){
        String html = null;
        try {
            Template template = this.configuration.getTemplate(templatePath);
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, BeanUtil.beanToMap(message));
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        if (StrUtil.isNotEmpty(html)){
            try {
                MimeMessageHelper messageHelper = new MimeMessageHelper(this.javaMailSender.createMimeMessage(),true);
                messageHelper.setSubject(message.getTitle());
                messageHelper.setFrom(mailProperties.getUsername());
                messageHelper.setTo(message.getTo());
                messageHelper.setText(html,true);
                this.javaMailSender.send(messageHelper.getMimeMessage());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }



}
