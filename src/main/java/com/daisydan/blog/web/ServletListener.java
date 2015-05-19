package com.daisydan.blog.web;

import com.daisydan.blog.enums.ArticleType;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 * <p/>
 * Created by veryyoung on 2015/3/12.
 */

@WebListener
public class ServletListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContextEvent.getServletContext())
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        ServletContext context = servletContextEvent.getServletContext();
        context.setAttribute("appName", "Skeleton");
        context.setAttribute(ArticleType.class.getSimpleName(), ArticleType.values());

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
