package com.example.projectmanagement.addBook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.projectmanagement.Enum.BookState.*;

@Slf4j
@RestController
public class AddBookController {

    @Autowired
    bookRepository bookRepository;
    @Autowired
    oneBookRepository oneBookRepository;
    @Autowired
    bookTypeRepository bookTypeRepository;


    public String deleteDoubleAndOne(String str) {
        String regexp = "\'";
        String regxp2 = "\"";
        str = str.replaceAll(regexp, "");
        str = str.replaceAll(regxp2, "");
        return str;
    }


    @RequestMapping(value = "/librarian/addBook", method = RequestMethod.POST)
    public List<Integer> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request,
                                @RequestParam("bookName") String bookName,
                                @RequestParam("author") String author,
                                @RequestParam("introduction") String introduction,
                                @RequestParam("bookNumber") String bookNumber,
                                @RequestParam("bookTypeId") String bookTypeId,
                                @RequestParam("bookPublisher") String bookPublisher,
                                @RequestParam("bookPrice") String bookPrice) throws IOException {

        bookName = deleteDoubleAndOne(bookName);
        author = deleteDoubleAndOne(author);
        introduction = deleteDoubleAndOne(introduction);
        bookNumber = deleteDoubleAndOne(bookNumber);
        bookTypeId = deleteDoubleAndOne(bookTypeId);
        bookPublisher = deleteDoubleAndOne(bookPublisher);
        bookPrice = deleteDoubleAndOne(bookPrice);


        List<Integer> oneBookId = new LinkedList<>();
        int bookTypeIdNumber = Integer.parseInt(bookTypeId);
        int numbers = Integer.parseInt(bookNumber);
        String originFileName = file.getOriginalFilename();
        //上传文件的目录，这个如果移植项目的话需要改动。
        String uploadPath = "F:\\idea2017.1.1代码\\images\\"; // 上传文件的目录
        String fileName;
        String ext = originFileName.
                substring(originFileName.indexOf('.'));
        File ff;
        do {
            fileName = UUID.randomUUID().toString();
            ff = new File(uploadPath + fileName + ext);
        } while (ff.exists());
        File file1 = new File(uploadPath, fileName + ext);
        file.transferTo(file1);
        //只在这里new了一次对象，hibernate只会在第一次将这个游离态的对象转换成为持久态的对象，
        // 之后在进行对同一个对象的save知识进行了update
        if (introduction.length() > 200) {
            introduction = introduction.substring(0, 201);
        }
        introduction = replaceSpecialChar(introduction);
        book bk = new book(bookName, author, introduction, numbers,
                fileName + ext, numbers, bookTypeIdNumber, getBookLocation(bookTypeIdNumber, numbers), bookPublisher, bookPrice);

        bk = bookRepository.save(bk);
        oneBook obk;

        for (int i = 0; i < numbers; i++) {
            obk = new oneBook();
            obk.setBookId(bk.getId());
            obk.setBookState(String.valueOf(AVAILABLE));
            obk = oneBookRepository.save(obk);
            oneBookId.add(obk.getId());
        }
        return oneBookId;
    }


    @Autowired
    bookShelfRepository bookShelfRepository;

    public String getBookLocation(Integer typeID, Integer bookAmount) {
        List<bookShelf> bookShelfList = bookShelfRepository.findByType(typeID);
        for (int i = 0; i < bookShelfList.size(); i++) {
            bookShelf bkS = bookShelfList.get(i);
            if (bkS.getSum() + bookAmount > bkS.getMax()) {
                continue;
            }
            bkS.setSum(bkS.getSum() + bookAmount);
            bookShelfRepository.save(bkS);
            String floor = bkS.getId() / 50 + 1 + " floor ";
            String area = bkS.getId() % 50 / 10 + 1 + " area ";
            String shelf = bkS.getId() % 10 + 1 + " shelf ";
            return floor + area + shelf;
        }
        return "full";
    }

    public String replaceSpecialChar(String str) {
        Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

}
