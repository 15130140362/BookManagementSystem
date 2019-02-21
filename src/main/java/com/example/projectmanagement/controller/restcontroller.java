package com.example.projectmanagement.controller;

import com.example.projectmanagement.addBook.*;
import com.example.projectmanagement.sendEmail.SendMail;
import com.example.projectmanagement.sendEmail.sendVerificationCode;
import com.example.projectmanagement.tableEntity.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.swing.text.html.Option;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.projectmanagement.Enum.BookState.*;


@Slf4j
@RestController
public class restcontroller {
    @Autowired
    bookRepository bookRepository;
    @Autowired
    userInfoRepository userInfoRepository;


    @PostMapping(value = {"/searchBook", "/homepage/serarchBook"})
    public String searchBook(@RequestParam("sbn") String name) {
        //    List<book> list = bookRepository.findByName(name);
        List<Object[]> result = bookRepository.getSearchBookInformation("%" + name + "%");
        List<book> bookList = new LinkedList<>();
        for (Object object : result) {
            Object[] temp = (Object[]) object;
            book bk = new book();
            bk.setId(Integer.parseInt(String.valueOf(temp[0])));
            bk.setAuthor(String.valueOf(temp[1]));
            bk.setIntroduction(String.valueOf(temp[2]));
            bk.setName(String.valueOf(temp[3]));
            bk.setNumbers(Integer.parseInt(String.valueOf(temp[4])));
            bk.setBookPicNumber(String.valueOf(temp[5]));
            bk.setSurPlus(Integer.parseInt(String.valueOf(temp[6])));
            bk.setBookLocation(String.valueOf(temp[7]));
            bk.setBookTypeId(Integer.parseInt(String.valueOf(temp[8])));
            bk.setPublisher(String.valueOf(temp[10]));
            bk.setPrice(String.valueOf(temp[11]));
            bookList.add(bk);
        }
        for (book tempbook : bookList) {
            tempbook.setSimilarityDegree(minEditDistance(name, tempbook.getName()));
        }
        Collections.sort(bookList);
        //   System.out.println(Arrays.toString(new List[]{bookList}));
        return JSONArray.fromObject(bookList).toString();

    }

    public int minEditDistance(String str1, String str2) {
        int[][] dis = new int[str1.length() + 1][str2.length() + 1];
        for (int i = 0; i <= str1.length(); i++) {
            dis[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dis[0][j] = j;
        }
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dis[i][j] = dis[i - 1][j - 1];
                } else {
                    dis[i][j] = min(dis[i - 1][j - 1] + 1, dis[i - 1][j] + 1, dis[i][j - 1] + 1);
                }
            }
        }
        return dis[str1.length()][str2.length()];
    }

    public int min(int a, int b, int c) {
        if (a > b) {
            if (b > c) {
                return c;
            } else {
                return b;
            }
        } else {
            if (a < c) {
                return a;
            } else {
                return c;
            }
        }
    }


    @PostMapping("/admin/deleteTheLibrarian")
    public String deleteTheLibrarian(@RequestParam("id") String id) {
        SysUser s = new SysUser();
        int temp = Integer.parseInt(id);
        s.setId(temp);
        userInfoRepository.delete(s);
        userInfoRepository.deleteTheLibrarian(temp);
        return "";
    }

    @PostMapping("/admin/addLibrarian")
    public String addLibrarian(@RequestParam("username") String librarianName,
                               @RequestParam("password") String librarianPassword) {
        librarianName=librarianName.trim();
        Optional<SysUser> optionalSysUser=userInfoRepository.findByUsername(librarianName);
        if(optionalSysUser.isPresent())
        {
            return "present";
        }
        SysUser su = new SysUser(librarianName, librarianPassword);
        su = userInfoRepository.save(su);
        userInfoRepository.insertToSysRole(su.getId(), 2);
        return su.getId().toString();
    }

    @PostMapping("/librarian/addReader")
    public String addReader(@RequestParam("id") String id, @RequestParam("username") String username, @RequestParam("password") String password) {
        userInfoRepository.save(Integer.parseInt(id), username, password);
        userInfoRepository.insertToSysRole(Integer.parseInt(id), 1);
        return "ok";
    }

    @Autowired
    announcementMessageRepository announcementMessageRepository;

    @PostMapping("/librarian/deleteAnnouncement")
    public String deleteAnnouncement(@RequestParam("announcemantId") String announcementId) {
        announcementMessage announcementMessage = new announcementMessage();
        announcementMessage.setId(Integer.parseInt(announcementId));
        announcementMessageRepository.delete(announcementMessage);

        return "ok";
    }


    @Autowired
    borrowBookRecordRepository borrowBookRecordRepository;

    @Autowired
    oneBookRepository oneBookRepository;


    @PostMapping("/librarian/borrowOneBook")
    public String borrowOneBook(@RequestParam("bookId") String bookId, @RequestParam("userName") String userName) {
        bookId = bookId.trim();
        userName = userName.trim();

        Optional<SysUser> optionalSysUser = userInfoRepository.findByUsername(userName);
        if (!optionalSysUser.isPresent()) {
            return "nothisuser";
        }
        //这个用户名称存在的话判断是否是读者
        SysUser sysUser = optionalSysUser.get();
        Integer role = userInfoRepository.findRoleID(sysUser.getId());
        if (role != 1) {
            return "notreader";
        }
        Integer borrowBookUpperBound = getValueFromSysUserSetting("borrowUpperBound");
        List<borrowBookRecord> hasBorrowList = borrowBookRecordRepository.findByReaderName(userName);
        boolean isReservedBook = false;

        if (hasBorrowList.size() < borrowBookUpperBound) {//判断是否达到借书上限
            //查看预约这本图书的人是否是当前的借阅的用户
            Optional<reserveBook> optionalReserveBook = reserveBookRepository.
                    findByBookIdAndIsVaild(Integer.parseInt(bookId), "true");
            if (optionalReserveBook.isPresent()) {
                reserveBook res = optionalReserveBook.get();
                if (res.getIsVaild().equals("true")) {
                    if (!res.getUsername().equals(userName)) {
                        return "this book has been reserved";
                    } else {
                        isReservedBook = true;
                        res.setIsVaild("false");
                        reserveBookRepository.save(res);
                    }
                }
            }
            Optional<oneBook> optionalOneBook = oneBookRepository.findById(Integer.parseInt(bookId));
            if (optionalOneBook.isPresent()) {
                oneBook oneBook = optionalOneBook.get();
                if (oneBook.getBookState().equals("LEND")) {
                    return "This Book Has Been Borrowed";
                }
                //这本书没有被借阅,没有被预约
                Optional<book> optionalBook = bookRepository.findById(oneBook.getBookId());
                if (optionalBook.isPresent()) {
                    book bk = optionalBook.get();
                    if (bk.getSurPlus() > 0) {
                        borrowBookRecord bR = new borrowBookRecord();
                        bR.setBookId(oneBook.getId());
                        bR.setBorrowdate(currentTime());
                        bR.setShouldReturnDate(shouldReturnDate());
                        bR.setReaderName(userName);
                        ///更新book表中的图书的数量
                        if (!isReservedBook) {
                            bookRepository.borrowBookMinusSurPlus(bk.getSurPlus() - 1, oneBook.getBookId());
                        }
                        //增加借阅记录
                        bR = borrowBookRecordRepository.save(bR);
                        //更新oneBook中的状态
                        oneBook.setBookState("LEND");
                        oneBook.setTradeId(bR.getId());
                        oneBookRepository.save(oneBook);
                        return "ok";
                    } else {
                        return "bookIsNull";
                    }
                } else {
                    return "bookIsNull";
                }
            } else {
                return "This Book Is Not Present";
            }
        } else {
            return "Reached The Borrow Amount Upper Bound";
        }
    }


    @Autowired
    returnBookRecordRepository returnBookRecordRepository;

    @PostMapping("/librarian/returnBook")
    public String returnBook(@RequestParam("bookId") String bookId, @RequestParam("comment") String comment) throws ParseException {
        Optional<oneBook> optionalOneBook = oneBookRepository.findById(Integer.parseInt(bookId));
        if (!optionalOneBook.isPresent()) {
            return "This Book is Not Present";
        }
        oneBook oneBook = optionalOneBook.get();
        if (!oneBook.getBookState().equals(String.valueOf(LEND))) {
            return "This Book Is Not Lend";
        }
        //获取tradeid得到借书的时间
        Optional<borrowBookRecord> optionalBorrowBookRecord = borrowBookRecordRepository.findByBookId(oneBook.getId());
        if (!optionalBorrowBookRecord.isPresent()) {
            return "error";
        }
        borrowBookRecord borrowBookRecord = optionalBorrowBookRecord.get();
        if (getDifferenceDayBetweenThisTwoDay(borrowBookRecord.getShouldReturnDate()) < 0) {
            return "Please Pay Fine First";
        }

        oneBook.setBookState(String.valueOf(AVAILABLE));
        oneBook.setTradeId(-1);
        bookRepository.updateSurPlus(oneBook.getBookId());
        oneBookRepository.saveAndFlush(oneBook);

        Optional<book> optionalBook = bookRepository.findById(oneBook.getBookId());
        if (!optionalBook.isPresent()) {
            return "error";
        }
        book bk = optionalBook.get();
        //增加还书的记录
        returnBookRecord returnBookRecord = new returnBookRecord();
        returnBookRecord.setOne_book_id(Integer.parseInt(bookId));
        returnBookRecord.setBookName(bk.getName());
        returnBookRecord.setBorrowDate(borrowBookRecord.getBorrowdate());
        returnBookRecord.setReturnBookDate(borrowDate());
        returnBookRecord.setReaderName(borrowBookRecord.getReaderName());
        comment = comment.trim();
        if (comment.equals("")) {
            returnBookRecord.setComment("");
        } else {
            returnBookRecord.setComment(comment);
        }
        returnBookRecordRepository.save(returnBookRecord);
        borrowBookRecordRepository.delete(borrowBookRecord);
        return "ok";
    }

    public String borrowDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public String shouldReturnDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        int period = 30;
        Optional<SysInfoSetting> optionalSysInfoSetting = sysInfoSettingRepository.findById("period");
        if (optionalSysInfoSetting.isPresent()) {
            period = Integer.parseInt(optionalSysInfoSetting.get().getInfoValue());
        }
        c.add(Calendar.DATE, period);
        return simpleDateFormat.format(c.getTime());
    }


    @PostMapping("/confirmOldPassword")
    public String confirmOldPassword(@RequestParam("oldPassWord") String oldPassword) {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (sysUser.getPassword().equals(oldPassword)) {
            return "passwordOk";
        } else {
            return "passwordError";
        }
    }

    @PostMapping("/changePassword")
    public String ChangePassword(@RequestParam("NewPassword1") String NewPassword1) {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (sysUser.getPassword().equals(NewPassword1)) {
            return "oldAndNewPasswordEqual";
        }
        sysUser.setPassword(NewPassword1);
        userInfoRepository.save(sysUser);
        return "ok";
    }


    //得改
    @PostMapping("/getMyPassword")
    public String getReaderPassword(@RequestParam("userEmail") String email) {
        Optional<readerEmailNumber> optionalReaderEmailNumber = readerEmailNumberRepository.findByEmail(email);
        if (!optionalReaderEmailNumber.isPresent()) {
            return "nouser";
        }
        Optional<SysUser> optionalSysUser = userInfoRepository.findById(optionalReaderEmailNumber.get().getUserid());
        if (!optionalSysUser.isPresent()) {
            return "nouser";
        }
        SysUser sysUser = optionalSysUser.get();
        int id = userInfoRepository.findRoleId(sysUser.getId());
        if (id == 1) {
            new Thread(new SendMail(email, " password is " + sysUser.getPassword())).start();
            return "1";
        } else if (id == 2) {
            return "2";
        } else {
            return "3";
        }
    }

    @Autowired
    reserveBookRepository reserveBookRepository;

    @PostMapping("/reader/reserveBook")
    public String reserveBook(@RequestParam("bookId") String bookId, @RequestParam("reserveNum") String reserveNum) throws ParseException {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int canReserveAMount = getValueFromSysUserSetting("reserveBookUpperBound");
        List<reserveBook> reserveBookList = reserveBookRepository.findByUsernameAndIsVaild(sysUser.getUsername(), "true");
        if (reserveBookList.size() >= canReserveAMount) {
            return "You have reached the reservation upper limit";
        } else if (reserveBookList.size() + Integer.parseInt(reserveNum) > canReserveAMount) {
            return "You Reserve Too Much";
        } else {
            Optional<book> optionalbk = bookRepository.findById(Integer.parseInt(bookId));
            book bk = optionalbk.get();
            //剩余的图书不够的话
            if (bk.getSurPlus() < Integer.parseInt(reserveNum)) {
                return "There are not enough books";
            }
            bk.setSurPlus(bk.getSurPlus() - Integer.parseInt(reserveNum));
            ///更新图书中状态的信息。
            bookRepository.save(bk);
            //更新one_book中的图书的状态信息，
            List<oneBook> oneBookList = oneBookRepository.findByBookIdAndBookState(Integer.parseInt(bookId), String.valueOf(AVAILABLE));
            List<Integer> oneBookIdList = new LinkedList<>();
            for (int i = 0; i < Integer.parseInt(reserveNum); i++) {
                reserveBook reserveBook = new reserveBook();
                reserveBook.setBookId(oneBookList.get(i).getId());

                oneBookIdList.add(oneBookList.get(i).getId());

                reserveBook.setUsername(sysUser.getUsername());
                reserveBook.setIsVaild("true");
                String currentTime = reserveCurrentTime();
                reserveBook.setReservTime(currentTime);
               /* reserveBook.setEndTIme(reserveEndTime(currentTime));*/
                reserveBook = reserveBookRepository.save(reserveBook);
                oneBookList.get(i).setBookState(String.valueOf(RESERVED));
                oneBookList.get(i).setTradeId(reserveBook.getId());//设置交易的id
                oneBookRepository.save(oneBookList.get(i));
            }
            return JSONArray.fromObject(oneBookIdList).toString();
        }
    }


    public String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }


    public String reserveCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }


    public String reserveEndTime(String date) throws ParseException {
        Integer reserveHours = getValueFromSysUserSetting("reserveHours");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.HOUR, reserveHours);
        date1 = calendar.getTime();
        return sdf.format(date1);
    }

    @PostMapping("/librarian/findUserReserveInformation")
    public String findUserReserveINformation(@RequestParam("searchName")
                                                     String searchName) {
        List<reserveBook> reserveBooks = reserveBookRepository.findByUsername(searchName);
        if (reserveBooks.size() == 0) {
            return "noreserve";
        }
        log.info(Arrays.toString(new List[]{reserveBooks}));
        return JSONArray.fromObject(reserveBooks).toString();
    }


    @PostMapping("/librarian/reserveBookNo")
    public String reserveBookNo(@RequestParam("bookId") String bookId) {
        bookId = bookId.trim();
        Optional<reserveBook> optionalRB = reserveBookRepository.findByBookIdAndIsVaild(Integer.parseInt(bookId), "true");

        reserveBook rb = optionalRB.get();
        rb.setIsVaild("false");
        reserveBookRepository.save(rb);

        // oneBookRepository.updateReserveState(Integer.parseInt(bookId), String.valueOf(AVAILABLE));
        Optional<oneBook> optionaloneBook = oneBookRepository.findById(Integer.parseInt(bookId));
        oneBook oneBook = optionaloneBook.get();
        oneBook.setBookState(String.valueOf(AVAILABLE));
        oneBook.setTradeId(-1);
        oneBookRepository.save(oneBook);
        Optional<book> optionalBook = bookRepository.findById(oneBook.getBookId());
        book bk = optionalBook.get();
        bk.setSurPlus(bk.getSurPlus() + 1);
        bookRepository.save(bk);
        return bookId;
    }


    @PostMapping("/librarian/addAnnouncement")
    public String saveAnnouncement(@RequestParam("title") String title,
                                   @RequestParam("body") String body,
                                   @RequestParam("messageType") String messageType) {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        announcementMessage announcementMessage = new announcementMessage();
        announcementMessage.setBody(body);
        announcementMessage.setTitle(title);
        announcementMessage.setMessageType(messageType);
        announcementMessage.setDate(currentTime());
        announcementMessage.setName(sysUser.getUsername());
        announcementMessage temp = announcementMessageRepository.save(announcementMessage);
        return JSONObject.fromObject(temp).toString();
    }


    @PostMapping(value = {"/librarian/requestAnnouncementListInfo", "/reader/requestAnnouncementListInfo"})
    public String requestAnnouncementList(Model model) {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<announcementMessage> announcementMessageList = announcementMessageRepository.findAll();
        Collections.sort(announcementMessageList);
        return JSONArray.fromObject(announcementMessageList).toString();
    }


    @PostMapping(value = {"/librarian/announcementTitleAndContent", "/reader/announcementTitleAndContent"})
    public String announcementTitleAndContent(Model model, @RequestParam("id") String id) {
        int identity = Integer.parseInt(id);
        Optional<announcementMessage> optional = announcementMessageRepository.findById(identity);
        announcementMessage announcementMessage = optional.get();
        return JSONObject.fromObject(announcementMessage).toString();
    }

    @Autowired
    fineRepository fineRepository;
    @Autowired
    readerDepositRepository readerDepositRepository;

    @PostMapping(value = {"/librarian/searchYearInfo", "/librarian/searchMonthlyInfo", "/librarian/searchdailyInfo"})
    public String getAnnualInformation(@RequestParam("requestTime") String requestTime) {
        requestTime = requestTime.trim();
        if (requestTime.equals("")) {
            return "error";
        } else {
            Optional<SysInfoSetting> optionalSysInfoSetting = sysInfoSettingRepository.findById("deposit");
            SysInfoSetting sysInfo = optionalSysInfoSetting.get();
            Integer depositValue = Integer.parseInt(String.valueOf(sysInfo.getInfoValue()));
            int fineIncome = getFineValue(requestTime);
            int depositIncome = getDepositValue(requestTime, depositValue);
            if (fineIncome == 0 && depositIncome == 0) {
                return "noincome";
            } else {
                return "{'fineIncome':'" + fineIncome + "','depositIncome':'" + depositIncome + "'}";
            }
        }
    }


    @Autowired
    sysInfoSettingRepository sysInfoSettingRepository;

    @PostMapping("/librarian/searchFineValue")
    public String librariansearchFineValue(@RequestParam("userName") String username) throws ParseException {
        username = username.trim();
        int fineValue = getValueFromSysUserSetting("fine");

        List<notReturnBookRecord> overDueList = new LinkedList<>();
        List<Object[]> resultList = bookRepository.
                findReaderLendAndOverDueBook("LEND", username);
        if (resultList.size() == 0) {
            return "error";
        }
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
            notReturn.setBookImage(String.valueOf(temp[4]));
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
                overDueList.add(notReturn);
            }
        }
        return JSONArray.fromObject(overDueList).toString();
    }


    public List<String> weekList(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(str);
        List<String> weeklist = new LinkedList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 7; i++) {
            Date date1 = new Date(calendar.getTimeInMillis());
            weeklist.add(simpleDateFormat.format(date1));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return weeklist;
    }

    @PostMapping("/librarian/searchWeeklyInfo")
    public String searchWeeklyInfo(@RequestParam("weekTime") String weekTime) throws ParseException {
        weekTime = weekTime.trim();
        if (weekTime.equals("")) {
            return "error";
        } else {
            Optional<SysInfoSetting> optionalSysInfoSetting = sysInfoSettingRepository.findById("deposit");
            SysInfoSetting sysInfo = optionalSysInfoSetting.get();
            Integer depositValue = Integer.parseInt(String.valueOf(sysInfo.getInfoValue()));
            List<String> weekList = weekList(weekTime);
            //给每个应该增加上"%"
            weekIncome wI = new weekIncome();
            wI.setMondayFine(getFineValue(weekList.get(0)));
            wI.setMondayDeposit(getDepositValue(weekList.get(0), depositValue));

            wI.setTuesdayFine(getFineValue(weekList.get(1)));
            wI.setTuesdayDeposit(getDepositValue(weekList.get(1), depositValue));

            wI.setWednesdayFine(getFineValue(weekList.get(2)));
            wI.setWednesdayDeposit(getDepositValue(weekList.get(2), depositValue));

            wI.setThursdayFine(getFineValue(weekList.get(3)));
            wI.setThursdayDeposit(getDepositValue(weekList.get(3), depositValue));

            wI.setFridayFine(getFineValue(weekList.get(4)));
            wI.setFridayDeposit(getDepositValue(weekList.get(4), depositValue));

            wI.setSaturdayFine(getFineValue(weekList.get(5)));
            wI.setSaturdayDeposit(getDepositValue(weekList.get(5), depositValue));

            wI.setSundayFine(getFineValue(weekList.get(6)));
            wI.setSundayDeposit(getDepositValue(weekList.get(6), depositValue));

            wI.setTotalDeposit(wI.TotalDeposit());
            wI.setTotalFine(wI.TotalFine());
            wI.setTotalIncome(wI.getTotalIncome(wI.getTotalFine(), wI.getTotalDeposit()));

            return JSONObject.fromObject(wI).toString();

           /* if (wI.getMondayFine() == 0 && wI.getMondayDeposit() == 0
                    && wI.getTuesdayFine() == 0 && wI.getTuesdayDeposit() == 0
                    && wI.getWednesdayFine() == 0 && wI.getWednesdayDeposit() == 0
                    && wI.getThursdayFine() == 0 && wI.getThursdayDeposit() == 0
                    && wI.getFridayFine() == 0 && wI.getFridayDeposit() == 0
                    && wI.getSaturdayFine() == 0 && wI.getSaturdayDeposit() == 0
                    && wI.getSundayFine() == 0 && wI.getSundayDeposit() == 0) {
                return "noincome";
            } else {
                return JSONObject.fromObject(wI).toString();
            }*/
        }

    }

    public Integer getFineValue(String dateTime) {
        List<Object> fineList = fineRepository.getFineValue(dateTime + "%");
        int fine = 0;
        for (Object obj : fineList) {
            fine += Integer.parseInt(String.valueOf(obj));
        }
        return fine;
    }

    public Integer getDepositValue(String dateTime, Integer depositValue) {
        List<Object[]> objects = readerDepositRepository.getDeposit(dateTime + "%");
        return depositValue * objects.size();
    }

    public int getDifferenceDayBetweenThisTwoDay(String dat1)
            throws ParseException {
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

    //dat1减dat2
    public int overdue(String nowTime, String dat2) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cl = Calendar.getInstance();
        cl.setTime(simpleDateFormat.parse(nowTime));
        long dat1Mil = cl.getTimeInMillis();
        cl.setTime(simpleDateFormat.parse(dat2));
        long dat2Mil = cl.getTimeInMillis();
        long sub = (dat1Mil - dat2Mil) / (1000 * 3600 * 24);
        return (int) sub;
    }

    @Autowired
    bookTypeRepository bookTypeRepository;

    @PostMapping("/librarian/addBookCategory")
    public String addBookCategry(@RequestParam("categoryName") String categoryName, @RequestParam("bookShelfNumber") String bookShelfNumber) {
        bookType bt = bookTypeRepository.save(new bookType(categoryName));
        allocateBookShelf(bt.getId(), Integer.parseInt(bookShelfNumber));
        return String.valueOf(bt.getId());
    }

    @Autowired
    bookShelfRepository bookShelfRepository;

    //设置一个类别5个书架默认
    public Integer allocateBookShelf(Integer typeId, Integer needShelf) {
        //一个类别最多只能增加十个书架
        int MaxShelfPerarea = 10;
        List<Integer> resultList = bookShelfRepository.findValidBookShelf();
        //每个区10个书架
        for (int i = 0; i < resultList.size() - needShelf + 1; i++) {
            if (resultList.get(i) / MaxShelfPerarea == resultList.get(i + needShelf - 1) / MaxShelfPerarea && resultList.get(i + needShelf - 1) - resultList.get(i) == needShelf - 1) {
                bookShelfRepository.updateBookType(typeId, resultList.get(i), resultList.get(i + needShelf - 1));
                return 1;
            }
        }
        return -1;
    }

    @PostMapping("/librarian/deleteTheBookCategory")
    public String addBookCategory(@RequestParam("id") String id) {
        id = id.trim();
        bookType bkt = new bookType();
        bkt.setId(Integer.parseInt(id));
        bookTypeRepository.delete(bkt);
        bookShelfRepository.updateBookType(Integer.parseInt(id), -1);
        return "ok";
    }

    @PostMapping("/librarian/saveCategoryEditBook")
    public String saveEditBook(@RequestParam("id") String id, @RequestParam("newName") String newName) {
        bookType bkt = new bookType();
        bkt.setId(Integer.parseInt(id));
        bkt.setTypename(newName);
        bookTypeRepository.save(bkt);
        return "ok";
    }

    @PostMapping("/librarian/deleteTheOneBook")
    public String deleteTheOneBook(@RequestParam("keyWord") String keyWord) {
        oneBook obk;
        book bk;
        Optional<oneBook> optionalOneBook = oneBookRepository.findById(Integer.parseInt(keyWord));
        if (optionalOneBook.isPresent()) {
            log.info(optionalOneBook.get().toString());
            obk = optionalOneBook.get();
            Optional<book> optionalBook = bookRepository.findById(obk.getBookId());
            if (optionalBook.isPresent()) {

                bk = optionalBook.get();
                Optional<bookType> optionalBookType = bookTypeRepository.findById(bk.getBookTypeId());
                String bookTypestr = "";
                if (optionalBookType.isPresent()) {
                    bookType bookType = optionalBookType.get();
                    bookTypestr = bookType.getTypename();
                }//删除图书类别之后就没有图书类别了
                else {
                    bookTypestr = "other";
                }
                return "{'oneBookId':'" + obk.getId() + "','bookName':'" + bk.getName() + "'," +
                        "'author':'" + bk.getAuthor() + "'," +
                        "'introduction':'" + bk.getIntroduction() + "'," +
                        "'surplus':'" + bk.getSurPlus() + "'," +
                        "'publisher':'" + bk.getPublisher() + "'," +
                        "'price':'" + bk.getPrice() + "'," +
                        "'bookTypeName':'" + bookTypestr + "'," +
                        "'bookImag':'" + bk.getBookPicNumber() + "'," +
                        "'bookLocation':'" + bk.getBookLocation() + "'," +
                        "'bookState':'" + obk.getBookState() + "'}";
            } else {
                return "error";
            }
        } else {
            return "error";
        }

    }

    @Autowired
    deleteBookRecordRepository deleteBookRecordRepository;

    @PostMapping("/librarian/deleteBookById")
    public String deleteTheBookById(@RequestParam("id") String id) {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer bookID = Integer.parseInt(id);
        Optional<oneBook> optionalOneBook = oneBookRepository.findById(bookID);
        oneBook obk = optionalOneBook.get();
        String bookState = obk.getBookState();

        Optional<book> optionalBook = bookRepository.findById(obk.getBookId());
        if (optionalBook.isPresent()) {
            book bk = optionalBook.get();
            List<oneBook> oneBooks = oneBookRepository.findByBookId(obk.getBookId());
            if (oneBooks.size() == 1) {//只有一本书就删除book信息
                bookRepository.delete(bk);
                oneBookRepository.delete(obk);
                return "ok";
            } else {
                if (obk.getBookState().equals(String.valueOf(AVAILABLE))) {
                    bk.setNumbers(bk.getNumbers() - 1);//可用的话更新总的图书个数和剩余的图书的数量
                    bk.setSurPlus(bk.getSurPlus() - 1);
                } else if (bookState.equals("LEND") || bookState.equals("OVERDUE") || bookState.equals("RESERVED")) {

                    bk.setNumbers(bk.getNumbers() - 1);//预约的话让那个人找不到书如果书被删除了

                    Optional<borrowBookRecord> optionalBorrowBookRecord = borrowBookRecordRepository.findByBookId(bookID);


                    optionalBorrowBookRecord.ifPresent(borrowBookRecord -> borrowBookRecordRepository.delete(borrowBookRecord));

                    Optional<reserveBook> optionalReserveBook = reserveBookRepository.findByBookIdAndIsVaild(bookID, "true");

                    optionalReserveBook.ifPresent(reserveBook -> reserveBookRepository.delete(reserveBook));
                }
                bookRepository.save(bk);
                oneBookRepository.delete(obk);
                deleteBookRecord deleteBookRecord = new deleteBookRecord();
                deleteBookRecord.setBookId(bookID);
                deleteBookRecord.setBookName(bk.getName());
                deleteBookRecord.setLibrarianName(sysUser.getUsername());
                deleteBookRecord.setDeleteTime(currentTime());
                deleteBookRecordRepository.save(deleteBookRecord);
                return "ok";
            }
        } else {
            return "error";
        }
    }


    @Autowired
    readerEmailNumberRepository readerEmailNumberRepository;


    @PostMapping("/librarian/searchReader")
    public String librarianSearchReader(@RequestParam("readerId") String readerId) {
        readerId = readerId.trim();
        //用户名为用户的电话号码，11位
        SysUser user;
        if (readerId.length() == 11) {     //使用username进行搜索
            Optional<SysUser> optionalSysUser = userInfoRepository.findByUsername(readerId);
            if (!optionalSysUser.isPresent()) {
                return "nouser";
            }
            user = optionalSysUser.get();

        } else {
            Optional<SysUser> optionalSysUser = userInfoRepository.findById(Integer.parseInt(readerId));
            if (!optionalSysUser.isPresent()) {
                return "nouser";
            }
            user = optionalSysUser.get();
        }
        Optional<readerEmailNumber> optionalReaderEmailNumber = readerEmailNumberRepository.findByUserid(user.getId());
        if (!optionalReaderEmailNumber.isPresent()) {
            return "nouser";
        }
        readerEmailNumber readerEmailNumber = optionalReaderEmailNumber.get();
        return "{'id':'" + user.getId() + "'," +
                "'telephone':'" + user.getUsername() + "'," +
                "'email':'" + readerEmailNumber.getEmail() + "'}";

    }

    @PostMapping("/librarian/resetPassword")
    public String librarianResetPassword(@RequestParam("id") String id) {
        Optional<SysUser> optionalSysUser = userInfoRepository.findById(Integer.parseInt(id));
        SysUser sysUser = optionalSysUser.get();
        Integer defalult = getValueFromSysUserSetting("readerDefaultPassword");
        sysUser.setPassword(String.valueOf(defalult));
        userInfoRepository.saveAndFlush(sysUser);
        return "ok";
    }

    @PostMapping("/librarian/deleteReader")
    public String librariandeleteReader(@RequestParam("username") String username) {
        username = username.trim();
        Integer fineValue = getValueFromSysUserSetting("fine");
        List<Object[]> resultList = bookRepository.
                findReaderLendAndOverDueBook("LEND", username);

        if (resultList.size() > 0) {
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
                    overDueList.add(notReturn);
                } else {
                    notReturnlist.add(notReturn);
                }
            }
            if (notReturnlist.size() > 0 && overDueList.size() > 0) {
                return "overdueAndLend";
            } else if (notReturnlist.size() > 0) {
                return "lend";
            } else {
                return "overdue";
            }
        } else {

            Optional<SysUser> optionalSysUser = userInfoRepository.findByUsername(username);
            if (!optionalSysUser.isPresent()) {
                return "nouser";
            }
            SysUser sysUser = optionalSysUser.get();
            int temp = sysUser.getId();
            readerEmailNumberRepository.deleteByUserID(temp);
            userInfoRepository.delete(sysUser);
            userInfoRepository.deleteTheLibrarian(temp);
            return "ok";
        }
    }


    @PostMapping("/searchOneBookId")
    public String SearchOneBookId(@RequestParam("bookId") String bookid) {
        Integer id = Integer.parseInt(bookid);
        List<oneBook> oneBookList = oneBookRepository.findByBookIdAndBookState(id, String.valueOf(AVAILABLE));
        return JSONArray.fromObject(oneBookList).toString();
    }


    @PostMapping("/librarian/searchOneBookNameAndPicNumber")
    public String searchOneBookNameAndPicNumber(@RequestParam("bookId") String bookId) {
        bookId = bookId.trim();
        if (bookId.equals("")) {
            return "error";
        }
        Optional<oneBook> optionalOneBook = oneBookRepository.findById(Integer.parseInt(bookId));
        if (optionalOneBook.isPresent()) {
            oneBook oneBook = optionalOneBook.get();
            Optional<book> optionalBook = bookRepository.findById(oneBook.getBookId());
            if (optionalBook.isPresent()) {
                book bk = optionalBook.get();
                return "{'name':'" + bk.getName() + "','bookPicNumber':'" + bk.getBookPicNumber() + "'}";
            }
        }
        return "error";
    }

    public Integer getValueFromSysUserSetting(String strId) {
        Optional<SysInfoSetting> optionalSysInfoSetting = sysInfoSettingRepository.findById(strId);
        SysInfoSetting sysInfoSetting = optionalSysInfoSetting.get();
        return Integer.parseInt(sysInfoSetting.getInfoValue());
    }


    @PostMapping("/librarian/searchReaderBorrowHistory")
    public String getReaderBorrowHistory(@RequestParam("username") String username) {
        username = username.trim();
        Integer fineValue = getValueFromSysUserSetting("fine");
        List<Object[]> resultList = bookRepository.
                findReaderLendAndOverDueBook("LEND", username);
        if (resultList.size() == 0) {
            return "This Reader Have No Borrow History";
        }
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
                /*totalFine += tval;*/
                overDueList.add(notReturn);
            } else {
                notReturnlist.add(notReturn);
            }
        }

        List<returnBookRecord> lists = returnBookRecordRepository.findByReaderName(username);
        List<notReturnBookRecord> hasReturnedBookList = new LinkedList<>();
        for (returnBookRecord t : lists) {
            notReturnBookRecord not = new notReturnBookRecord(t.getOne_book_id(), t.getBookName(), t.getBorrowDate(), t.getReturnBookDate());
            Optional<fine> optionalFine = fineRepository.findByBookIdAndUsernameAndBorrowDate(t.getOne_book_id(), username, t.getBorrowDate());
            if (optionalFine.isPresent()) {
                not.setFine(optionalFine.get().getFineValue());
            } else {
                not.setFine(0);
            }
            not.setComment(t.getComment());
            hasReturnedBookList.add(not);
        }
        Map<String, List<notReturnBookRecord>> map = new HashMap<>();
        map.put("notReturnBookList", notReturnlist);
        map.put("overDueBookList", overDueList);
        map.put("hasReturnBookList", hasReturnedBookList);
        return JSONObject.fromObject(map).toString();
    }


    @PostMapping("/librarian/saveFineHistoryInformation")
    public String saveFineHistooryInformation(@RequestParam("jsonData") String jsonData, @RequestParam("readerName") String readerName) {
        JSONArray jsonArray = JSONArray.fromObject(jsonData);
        for (Object object : jsonArray) {
            notReturnBookRecord notReturnBookRecord =
                    (notReturnBookRecord) JSONObject.toBean((JSONObject) object, notReturnBookRecord.class);
            fine f = new fine();
            f.setBookId(notReturnBookRecord.getId());
            f.setFineValue(notReturnBookRecord.getFine());
            f.setUsername(readerName);
            f.setBorrowDate(notReturnBookRecord.getBorrowDate());
            f.setShouldReturnDate(notReturnBookRecord.getShouldReturnDate());
            f.setReturnDate(currentTime());
            f.setBookName(notReturnBookRecord.getBookName());
            fineRepository.save(f);
            //更新应该还书的日期这样的话就没有罚金了
            Optional<borrowBookRecord> optionalBorrowBookRecord = borrowBookRecordRepository.findByBookId(f.getBookId());
            if (!optionalBorrowBookRecord.isPresent()) {
                return "borrowhistorynotpresent";
            }
            borrowBookRecord borrowBookRecord = optionalBorrowBookRecord.get();
            borrowBookRecord.setShouldReturnDate(currentTime());
            borrowBookRecordRepository.save(borrowBookRecord);//保存罚金记录的同时修改借书历史中的

        }
        return "ok";
    }


    @RequestMapping("/librarian/searchReaderFineHistory")
    public String searchReaderFineHistory(@RequestParam("username") String username) {
        username = username.trim();
        List<fine> fineList = fineRepository.findByUsername(username);
        if (fineList.size() == 0) {
            return "No Recored";
        }
        Collections.sort(fineList);
        return JSONArray.fromObject(fineList).toString();
    }

    @PostMapping("/librarian/sendNotificationCode")
    public String sendVerificationCode(@RequestParam("telephone") String telephone) {
        telephone = telephone.trim();
        String number = sendVerificationCode.createRandNum();
        //sendVerificationCode.randNum=number;
        String temp=sendVerificationCode.smsContent;
        sendVerificationCode.smsContent += number;
        sendVerificationCode.sendMsgTo(telephone);
        sendVerificationCode.smsContent=temp;
        return number;
    }

    @PostMapping("/admin/searchLibrarian")
    public String searchLibrarian(@RequestParam("username") String username) {
        Optional<SysUser> optionalSysUser = userInfoRepository.findByUsername(username);
        if (!optionalSysUser.isPresent()) {
            return "error";
        }
        SysUser sysUser = optionalSysUser.get();
        Integer roleId = userInfoRepository.findRoleID(sysUser.getId());
        if (roleId != 2) {
            return "error";
        } else {
            return JSONObject.fromObject(sysUser).toString();
        }
    }

    @PostMapping("/admin/changeLibrarianEmail")
    public String changeLibrarianEmail(@RequestParam("id") String id, @RequestParam("email") String email) {
        Optional<SysUser> optionalSysUser = userInfoRepository.findById(Integer.parseInt(id));
        SysUser sysUser = optionalSysUser.get();
        sysUser.setUsername(email);
        userInfoRepository.save(sysUser);
        return "ok";
    }

    @PostMapping("/admin/saveSystemInformation")
    public String saveSystemInformation(@RequestParam("fineValue") String fineValue,
                                        @RequestParam("returnPeriod") String returnPeriod,
                                        @RequestParam("deposit") String deposit,
                                        @RequestParam("upperBound") String upperBound,
                                        @RequestParam("ReaderCanreserveHours") String ReaderCanreserveHours,
                                        @RequestParam("ReaderBenotifyDaya") String ReaderBenotifyDaya,
                                        @RequestParam("ReaderCanreserveAmount") String ReaderCanreserveAmount) {
        sysInfoSettingRepository.updateValue("fine", fineValue);
        sysInfoSettingRepository.updateValue("deposit", deposit);
        sysInfoSettingRepository.updateValue("period", returnPeriod);
        sysInfoSettingRepository.updateValue("borrowUpperBound", upperBound);
        sysInfoSettingRepository.updateValue("reserveHours", ReaderCanreserveHours);
        sysInfoSettingRepository.updateValue("notifyMinusDays", ReaderBenotifyDaya);
        sysInfoSettingRepository.updateValue("reserveBookUpperBound", ReaderCanreserveAmount);
        return "ok";
    }

    @PostMapping("/librarian/searchDeleteBookInformation")
    public String searchDeleteBookInformation(@RequestParam("bookId") String bookId) {
        bookId = bookId.trim();
        if (bookId.matches("[0-9]{1,}")) {
            Optional<deleteBookRecord> optionalDeleteBookRecord = deleteBookRecordRepository.findByBookId(Integer.parseInt(bookId));
            if (optionalDeleteBookRecord.isPresent()) {
                return JSONObject.fromObject(optionalDeleteBookRecord.get()).toString();
            } else {
                return "nobook";
            }
        } else {
            List<deleteBookRecord> deleteBookRecords = deleteBookRecordRepository.findByBookName("%" + bookId + "%");
            if (deleteBookRecords.size() != 0) {
                return JSONArray.fromObject(deleteBookRecords).toString();
            } else {
                return "nobook";
            }
        }
    }

    @PostMapping("/librarian/saveEditBook")
    public String saveEditBook(@RequestParam("bookId") String bookId,
                               @RequestParam("bookName") String bookName,
                               @RequestParam("author") String author,
                               @RequestParam("introduction") String introduction,
                               @RequestParam("bookSurplus") String bookSurplus,
                               @RequestParam("bookLocation") String bookLocaion,
                               @RequestParam("publisher") String publisher,
                               @RequestParam("bookAmount") String bookAmount) {
        Optional<book> optionalBook = bookRepository.findById(Integer.parseInt(bookId));
        if (!optionalBook.isPresent()) {
            return "nobook";
        }
        book bk = optionalBook.get();
        bk.setName(bookName);
        bk.setAuthor(author);
        introduction = replaceSpecialChar(introduction);
        bk.setIntroduction(introduction);
        bk.setNumbers(Integer.parseInt(bookAmount));
        bk.setSurPlus(Integer.parseInt(bookSurplus));
        bk.setBookLocation(bookLocaion);
        bk.setPublisher(publisher);
        bookRepository.save(bk);
        return "ok";
    }


    @Autowired
    personalInformationRepository personalInformationRepository;

    @RequestMapping("/reader/changePersonalInformation")
    public String changeMyEmail(@RequestParam("email") String email,
                                @RequestParam("name") String name,
                                @RequestParam("position") String position,
                                @RequestParam("sex") String sex,
                                @RequestParam("birthday") String birthday,
                                @RequestParam("about") String about) {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        about = replaceSpecialCharIgnoreSpace(about);
        Optional<personalInformation> optionalPersonalInformation = personalInformationRepository.findByUserid(sysUser.getId());
        personalInformation pf;
        if (optionalPersonalInformation.isPresent()) {
            pf = optionalPersonalInformation.get();
            pf.setName(name);
            pf.setPosition(position);
            pf.setSex(sex);
            pf.setBirthday(birthday);
            pf.setAbout(about);
        } else {
            pf = new personalInformation(name, position, sex, birthday, about, sysUser.getId());
        }
        personalInformationRepository.save(pf);

        Optional<readerEmailNumber> optionalReaderEmailNumber = readerEmailNumberRepository.findByUserid(sysUser.getId());
        if (!optionalReaderEmailNumber.isPresent()) {
            return "error";
        }
        readerEmailNumber rn = optionalReaderEmailNumber.get();
        rn.setEmail(email);
        readerEmailNumberRepository.save(rn);
        return "ok";
    }


    public String replaceSpecialChar(String str) {
        Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    public String replaceSpecialCharIgnoreSpace(String str) {
        Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    @PostMapping("/librarian/editReaderUserInformation")
    public String editReaderUserInformation(@RequestParam("newTelephone") String newTelephone, @RequestParam("id") String id) {
        newTelephone = newTelephone.trim();
        id = id.trim();
        Optional<SysUser> optionalSysUser = userInfoRepository.findById(Integer.valueOf(id));
        if (!optionalSysUser.isPresent()) {
            return "error";
        }
        SysUser sysUser = optionalSysUser.get();
        sysUser.setUsername(newTelephone);
        userInfoRepository.save(sysUser);
        return "ok";
    }


    @PostMapping("/librarian/searchBookCategory")
    public String searchBookCategory(@RequestParam("categoryName") String categoryName) {
        List<bookType> bookTypeList = bookTypeRepository.findByTypename("%" + categoryName + "%");
        if (bookTypeList.size() == 0) {
            return "error";
        }
        return JSONArray.fromObject(bookTypeList).toString();
    }

    @PostMapping("/librarian/getTypeCategory")
    public String categoryCapacity(@RequestParam("id") String id) {
        id = id.trim();
        List<bookShelf> bookShelfList = bookShelfRepository.findByType(Integer.parseInt(id));
        if (bookShelfList.size() == 0) {
            return "error";
        }
        return bookShelfList.size() + "";
    }

    @PostMapping("/librarian/changeBookTypeShelfAmount")
    public String changeBookTypeShelfAmount(@RequestParam("bookTypeId") String bookTypeId,
                                            @RequestParam("amount") String amount) {
        bookTypeId = bookTypeId.trim();
        List<bookShelf> bookShelfList = bookShelfRepository.findByType(Integer.parseInt(bookTypeId));
        if (bookShelfList.size() == Integer.parseInt(amount)) {
            return "ok";
        } else if (bookShelfList.size() < Integer.parseInt(amount)) {
            allocateBookShelf(Integer.parseInt(bookTypeId), Integer.parseInt(amount) - bookShelfList.size());
            return "ok";
        } else {
            int minus = bookShelfList.size() - Integer.parseInt(amount);
            for (int i = 0; i < minus; i++) {
                bookShelfList.get(bookShelfList.size() - 1 - i).setType(-1);
                bookShelfRepository.save(bookShelfList.get(bookShelfList.size() - 1 - i));
            }
            return "ok";
        }
    }

    @PostMapping("librarian/editAnnouncementContent")
    public String editAnnouncementContent(@RequestParam("announceMentId") String id,
                                          @RequestParam("title") String title, @RequestParam("body") String body) {
        Optional<announcementMessage> optionalAnnouncementMessage = announcementMessageRepository.findById(Integer.parseInt(id));
        if (!optionalAnnouncementMessage.isPresent()) {
            return "nopresent";
        }
        announcementMessage announcementMessage = optionalAnnouncementMessage.get();
        announcementMessage.setTitle(title.trim());
        announcementMessage.setBody(body.trim());
        announcementMessageRepository.save(announcementMessage);
        return "ok";
    }

}
