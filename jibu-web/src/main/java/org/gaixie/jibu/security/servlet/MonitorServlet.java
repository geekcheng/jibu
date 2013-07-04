package org.gaixie.jibu.security.servlet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.gaixie.jibu.security.annotation.CIVerify;
import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.security.service.MonitorService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应系统监控相关操作的请求。
 * <p>
 */
@Singleton
public class MonitorServlet extends HttpServlet {
    final static Logger logger = LoggerFactory.getLogger(MonitorServlet.class);

    @Inject
    private Injector injector;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String rst = null;
        if ("getCount".equals(req.getParameter("ci"))) {
            rst = getCount(req,injector);
        }

        PrintWriter pw = resp.getWriter();
        pw.println(rst);
        pw.close();
    }

    public void doPost(HttpServletRequest req,  HttpServletResponse resp)
        throws IOException{
        doGet(req, resp);
    }

    @CIVerify
    protected String getCount(HttpServletRequest req,
                              Injector injector) {
        MonitorService monitorService = injector.getInstance(MonitorService.class);
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
        return "{max:"+monitorService.getMaxActive()
            +",active:"+monitorService.getNumActive()
            +",idle:"+monitorService.getNumIdle()
            +",session:"+SessionCounter.getActiveSessions()
            +",totalMemory:"+(long)Math.floor(totalMemory/(1024*1024))
            +",usedMemory:"+(long)Math.floor(usedMemory/(1024*1024))
            +"}";
    }
}
