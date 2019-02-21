package com.example.projectmanagement.aliPay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.example.projectmanagement.addBook.bookRepository;
import com.example.projectmanagement.addBook.oneBookRepository;
import com.example.projectmanagement.aliPay.config.AlipayProperties;
import com.example.projectmanagement.tableEntity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 支付宝-电脑网站支付.
 * <p>
 * 电脑网站支付 https://docs.open.alipay.com/270/105898/
 *
 * @author Mengday Zhang
 * @version 1.0
 * @since 2018/6/14
 */
@Slf4j
@Controller
public class AlipayPagePayController {

    @Autowired
    private AlipayProperties alipayProperties;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayController alipayController;

    @Autowired
    bookRepository bookRepository;

    @Autowired
    oneBookRepository oneBookRepository;

    @Autowired
    returnBookRecordRepository returnBookRecordRepository;


    @Autowired
    fineRepository fineRepository;

    public String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    @PostMapping("/gotoPayPageFine")
    public void gotoPayPageFine(HttpServletResponse response, HttpServletRequest request) throws AlipayApiException, IOException {
        // log.info("订单 title "+orderTitle+" 订单 money ");
        String title = request.getParameter("orderTitle");
        String money = request.getParameter("orderMoney");
        log.info("title : " + title + " money : " + money);
        // 订单模型
        String productCode = "FAST_INSTANT_TRADE_PAY";//销售产品码，只支持FAST_INSTANT_TRADE_PAY
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(UUID.randomUUID().toString());//商户订单号，64个字符以内，保证在商户端不重复
        model.setSubject(title);//订单标题
        model.setTotalAmount(money);//订单总金额
        model.setBody("pay " + money + " yuan ");//订单描述
        model.setProductCode(productCode);
        AlipayTradePagePayRequest pagePayRequest = new AlipayTradePagePayRequest();
        pagePayRequest.setReturnUrl(alipayProperties.getReturnUrl());
        pagePayRequest.setNotifyUrl(alipayProperties.getNotifyUrl());
        pagePayRequest.setBizModel(model);
        // 调用SDK生成表单, 并直接将完整的表单html输出到页面
        String form = alipayClient.pageExecute(pagePayRequest).getBody();
        response.setContentType("text/html;charset=" + alipayProperties.getCharset());
        response.getWriter().write(form);
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Autowired
    readerDepositRepository readerDepositRepository;

    @Autowired
    userInfoRepository userInfoRepository;
    @Autowired
    readerEmailNumberRepository readerEmailNumberRepository;

    @Autowired
    sysInfoSettingRepository sysInfoSettingRepository;

    public Integer getValueFromSysUserSetting(String strId) {
        Optional<SysInfoSetting> optionalSysInfoSetting = sysInfoSettingRepository.findById(strId);
        SysInfoSetting sysInfoSetting = optionalSysInfoSetting.get();
        return Integer.parseInt(sysInfoSetting.getInfoValue());
    }

    @PostMapping("/gotoPayPageDeposit")
    public void gotoPayPagerDeposit(HttpServletResponse response, HttpServletRequest request)
            throws AlipayApiException, IOException {
        //支付押金的时候保存用户信息

        String title = "Pay for deposit ";
        String depositMoney = String.valueOf(getValueFromSysUserSetting("deposit"));
        String email = request.getParameter("email");
        String username = request.getParameter("telephone");
        String password = request.getParameter("password");

        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setPassword(password);
        sysUser = userInfoRepository.save(sysUser);
        userInfoRepository.insertToSysRole(sysUser.getId(), 1);

        //保存用户的邮箱
        readerEmailNumber readerEmailNumber = new readerEmailNumber();
        readerEmailNumber.setEmail(email);
        readerEmailNumber.setUserid(sysUser.getId());
        readerEmailNumberRepository.save(readerEmailNumber);

        log.info("title : " + title + " money : " + depositMoney + "username" + username
                + "password" + password);

        readerDeposit rD = new readerDeposit();
        rD.setDate(currentTime());
        rD.setReturnTheMoney("no");
        rD.setPayUsername(username);
        readerDepositRepository.save(rD);


        // 订单模型
        String productCode = "FAST_INSTANT_TRADE_PAY";//销售产品码，只支持FAST_INSTANT_TRADE_PAY
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(UUID.randomUUID().toString());//商户订单号，64个字符以内，保证在商户端不重复
        model.setSubject(title);//订单标题
        model.setTotalAmount(depositMoney);//订单总金额
        model.setBody("pay " + depositMoney + " yuan ");//订单描述
        model.setProductCode(productCode);
        String returnUrl = alipayProperties.getReturnUrl();
        String notifyUrl = alipayProperties.getNotifyUrl();
        AlipayTradePagePayRequest pagePayRequest = new AlipayTradePagePayRequest();
        pagePayRequest.setReturnUrl(alipayProperties.getReturnUrl());
        pagePayRequest.setNotifyUrl(alipayProperties.getNotifyUrl());
        pagePayRequest.setBizModel(model);
        // 调用SDK生成表单, 并直接将完整的表单html输出到页面
        String form = alipayClient.pageExecute(pagePayRequest).getBody();
        response.setContentType("text/html;charset=" + alipayProperties.getCharset());
        response.getWriter().write(form);
        response.getWriter().flush();
        response.getWriter().close();
    }


    @Autowired
    userAndTradeNoRepository userAndTradeNoRepository;

    @RequestMapping("/returnUrl")
    public String returnUrl(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, AlipayApiException {
        // System.out.println("进入回掉地址");
        response.setContentType("text/html;charset=" + alipayProperties.getCharset());

        boolean verifyResult = alipayController.rsaCheckV1(request);
        if (verifyResult) {
            //验证成功
            //请在这里加上商户的业务逻辑程序代码，如保存支付宝交易号
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            log.info("订单的金额" + total_amount + "out_trade_no" + out_trade_no);

            return "/";
        } else {
            return "payFail";
        }
    }
}
