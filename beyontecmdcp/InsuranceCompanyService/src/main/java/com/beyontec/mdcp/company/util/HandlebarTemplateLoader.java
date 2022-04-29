package com.beyontec.mdcp.company.util;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class HandlebarTemplateLoader {

    private Handlebars handlebars;

    @PostConstruct
    public void loadHandlebarTemplates() {
        TemplateLoader loader = new ClassPathTemplateLoader("/templates", ".hbs");
        handlebars = new Handlebars(loader);
        org.beryx.hbs.Helpers.register(handlebars);
    }

    public Template getTemplate(String templateName) throws IOException {
        Template template = this.handlebars.compile(templateName);
        return template;
    }
}

