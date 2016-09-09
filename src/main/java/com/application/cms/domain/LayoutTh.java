package com.application.cms.domain;

import java.io.Serializable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class LayoutTh implements Serializable {

    private static final long serialVersionUID = 1L;

    private String header1="header1";
    private String header2="header2";
    private String left1="left1";
    private String left2="left2";
    private String right1="right1";
    private String right2="right2";    
    private String menu="menu";
    private String menuLeft="menuLeft";

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public String getHeader2() {
        return header2;
    }

    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    public String getLeft1() {
        return left1;
    }

    public void setLeft1(String left1) {
        this.left1 = left1;
    }

    public String getLeft2() {
        return left2;
    }

    public void setLeft2(String left2) {
        this.left2 = left2;
    }

    public String getRight1() {
        return right1;
    }

    public void setRight1(String right1) {
        this.right1 = right1;
    }

    public String getRight2() {
        return right2;
    }

    public void setRight2(String right2) {
        this.right2 = right2;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMenuLeft() {
        return menuLeft;
    }

    public void setMenuLeft(String menuLeft) {
        this.menuLeft = menuLeft;
    }

    @Override
    public String toString() {
        return "LayoutTh{" + "header1=" + header1 + ", header2=" + header2 + ", left1=" + left1 + ", left2=" + left2 + ", right1=" + right1 + ", right2=" + right2 + ", menu=" + menu + ", menuLeft=" + menuLeft + '}';
    }


}
