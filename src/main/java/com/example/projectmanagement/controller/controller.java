package com.example.projectmanagement.controller;

import com.example.projectmanagement.addBook.*;
import com.example.projectmanagement.tableEntity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.swing.text.html.Option;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@Controller
public class controller {
    @Autowired
    userInfoRepository userInfoRepository;

    @Autowired
    bookTypeRepository bookTypeRepository;

    @Autowired
    sysInfoSettingRepository sysInfoSettingRepository;

    @Autowired
    bookRepository bookRepository;

    @GetMapping("/login")
    public String getRegister(Model model) {
        List<announcementMessage> announcementMessageList = announcementMessageRepository.findAll();
        Collections.sort(announcementMessageList);
        model.addAttribute("announcementList", announcementMessageList);
        return "/homepage";
    }


    @Autowired
    returnBookRecordRepository returnBookRecordRepository;
    @Autowired
    announcementMessageRepository announcementMessageRepository;
    @Autowired
    readerEmailNumberRepository readerEmailNumberRepository;

    @Autowired
    deleteBookRecordRepository deleteBookRecordRepository;

    @Autowired
    personalInformationRepository personalInformationRepository;

    @Autowired
    fineRepository fineRepository;

    @Autowired
    oneBookRepository oneBookRepository;

    @GetMapping("/")
    public String index(Model model) {
        Set<String> roles = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //为界面添加一个用户名的隐藏域
        //reader和librarian都可以看到announcement
        List<announcementMessage> announcementMessageList = announcementMessageRepository.findAll();
        Collections.sort(announcementMessageList);
        model.addAttribute("announcementMessageList", announcementMessageList);

        if (roles.contains("ROLE_READER")) {
            Optional<personalInformation> optionalPersonalInformation =
                    personalInformationRepository.findByUserid(sysUser.getId());
            personalInformation pf = new personalInformation();
            pf.setNull();
            if (optionalPersonalInformation.isPresent()) {
                pf = optionalPersonalInformation.get();
            }
            model.addAttribute("readerInformation", pf);
            model.addAttribute("email", readerEmailNumberRepository.findByUserid(sysUser.getId()).get().getEmail());

            Integer fineValue = getValueFromSysUserSetting("fine");
            Integer totalFine = 0;
            model.addAttribute("level", "reader");

            List<Object[]> resultList = bookRepository.
                    findReaderLendAndOverDueBook("LEND", sysUser.getUsername());
            List<notReturnBookRecord> notReturnlist = new LinkedList<>();
            //字段的格式相同，所以使用相同的对象是可以的。
            List<notReturnBookRecord> overDueList = new LinkedList<>();
            String shouleReturnDate = "";
            String bookName = "";
            Integer bookId = 0;
            for (Object object : resultList) {
                Object[] temp = (Object[]) object;
                notReturnBookRecord notReturn = new notReturnBookRecord();
                bookId = (Integer) temp[0];
                notReturn.setId(bookId);
                bookName = String.valueOf(temp[1]);
                notReturn.setBookName(bookName);
                notReturn.setBorrowDate(String.valueOf(temp[2]));
                shouleReturnDate = String.valueOf(temp[3]);
                notReturn.setShouldReturnDate(shouleReturnDate);
                int minusDay = 0;
                try {
                    minusDay = getDifferenceDayBetweenThisTwoDay(shouleReturnDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (minusDay < 0) {
                    //这是超期的图书。
                    int tval = Math.abs(minusDay) * fineValue;
                    notReturn.setFine(tval);
                    totalFine += tval;
                    overDueList.add(notReturn);
                } else {
                    notReturnlist.add(notReturn);
                }
            }
            model.addAttribute("totalFine", totalFine);
            model.addAttribute("notReturnBookList", notReturnlist);
            model.addAttribute("overDueBookList", overDueList);
            //超期的图书。
            List<returnBookRecord> lists = returnBookRecordRepository.findByReaderName(sysUser.getUsername());
            List<notReturnBookRecord> hasReturnedBook = new LinkedList<>();
            for (returnBookRecord t : lists) {
                notReturnBookRecord not = new notReturnBookRecord(t.getOne_book_id(), t.getBookName(), t.getBorrowDate(), t.getReturnBookDate());
                Optional<fine> optionalFine=fineRepository.findByBookIdAndUsernameAndBorrowDate(t.getOne_book_id(), sysUser.getUsername(),t.getBorrowDate());
                if(optionalFine.isPresent())
                {
                    not.setFine(optionalFine.get().getFineValue());
                }else{
                    not.setFine(0);
                }
                not.setComment(t.getComment());
                hasReturnedBook.add(not);
            }
            model.addAttribute("hasReturnBookList", hasReturnedBook);
            return "readerHome";
        } else if (roles.contains("ROLE_LIBRARIAN")) {
            model.addAttribute("level", "librarian");
            //librarian登陆的时候加载相应的图书种类信息
            List<bookType> bookTypeList = bookTypeRepository.findAll();
            model.addAttribute("bookCategoryList", bookTypeList);
            List<deleteBookRecord> deleteBookRecords = deleteBookRecordRepository.findAll();
            model.addAttribute("deleteBookRecord", deleteBookRecords);
            return "librarianHome";
        } else {

            model.addAttribute("level", "admin");
            //root登陆的时候会加载所有的librarian的信息
            List<SysUser> librariansInfo = new LinkedList<>();
            List<Object[]> lists = userInfoRepository.findByRoles(2);
            for (Object object : lists) {
                Object[] objects = (Object[]) object;
                SysUser t = new SysUser();
                t.setId(Integer.parseInt(String.valueOf(objects[0])));
                t.setUsername(String.valueOf(objects[1]));
                t.setPassword(String.valueOf(objects[2]));
                librariansInfo.add(t);
            }
            model.addAttribute("librariansInfo", librariansInfo);
            //系统参数
            model.addAttribute("fine", String.valueOf(getValueFromSysUserSetting("fine")));
            model.addAttribute("deposit", String.valueOf(getValueFromSysUserSetting("deposit")));
            model.addAttribute("period", String.valueOf(getValueFromSysUserSetting("period")));
            model.addAttribute("borrowUpperBound", String.valueOf(getValueFromSysUserSetting("borrowUpperBound")));
            model.addAttribute("reserveHours", String.valueOf(getValueFromSysUserSetting("reserveHours")));
            model.addAttribute("notifyDays", String.valueOf(getValueFromSysUserSetting("notifyMinusDays")));
            model.addAttribute("reserveAmount", String.valueOf(getValueFromSysUserSetting("reserveBookUpperBound")));
            return "rootHome";
        }
    }

    @GetMapping("/paySuccess")
    public String success() {
        return "/paySuccess";
    }

    @GetMapping("/payFail")
    public String fail() {
        return "/payFail";
    }

    @GetMapping("/test")
    public String test() {
        return "/test";
    }

    //dat1为读者应该归还图书的时间，
    public int getDifferenceDayBetweenThisTwoDay(String dat1) throws ParseException {
        String dat2 = currentTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cl = Calendar.getInstance();
        cl.setTime(simpleDateFormat.parse(dat1));
        long dat1Mil = cl.getTimeInMillis();
        cl.setTime(simpleDateFormat.parse(dat2));
        long dat2Mil = cl.getTimeInMillis();
        long days = (dat1Mil - dat2Mil) / (1000 * 3600 * 24);
        return (int) days;
    }

    public String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    @GetMapping("/error")
    public String error() {
        return "/error";
    }

    public Integer getValueFromSysUserSetting(String strId) {
        Optional<SysInfoSetting> optionalSysInfoSetting = sysInfoSettingRepository.findById(strId);
        SysInfoSetting sysInfoSetting = optionalSysInfoSetting.get();
        return Integer.parseInt(sysInfoSetting.getInfoValue());
    }

}
