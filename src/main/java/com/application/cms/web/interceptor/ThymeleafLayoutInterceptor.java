package com.application.cms.web.interceptor;

import com.application.cms.domain.LayoutTh;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author jperraudeau
 */
public class ThymeleafLayoutInterceptor extends HandlerInterceptorAdapter {
 
    private static final String DEFAULT_LAYOUT = "layouts/default";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";
 

    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null || !modelAndView.hasView()) {
            return;
        }
        String originalViewName = modelAndView.getViewName();
        if (isRedirectOrForward(originalViewName)) {
            return;
        }
        //modelAndView.setViewName(DEFAULT_LAYOUT);
        modelAndView.addObject("customVal", "Jerome");
        
        HttpSession session = request.getSession();
        if(session != null && session.getAttribute("layoutTh") == null) {
            session.setAttribute("layoutTh", new LayoutTh());
        }
        if(session != null && session.getAttribute("layoutTh") != null) {
            LayoutTh layoutTh = (LayoutTh) session.getAttribute("layoutTh");
            System.out.print(layoutTh.toString());
            layoutTh.setHeader1(layoutTh.getHeader1() + "A");
            session.setAttribute("layoutTh", layoutTh);
        }
    } 
    
    private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }   
}    

