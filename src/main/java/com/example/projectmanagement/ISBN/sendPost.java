package com.example.projectmanagement.ISBN;


import com.example.projectmanagement.addBook.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.projectmanagement.Enum.BookState.AVAILABLE;

@Slf4j
@RestController
public class sendPost {

    @Autowired
    bookRepository bookRepository;

    @Autowired
    oneBookRepository oneBookRepository;


    public String deleteDoubleAndOne(String str) {
        String regexp = "\'";
        String regxp2 = "\"";
        str = str.replaceAll(regexp, "");
        str = str.replaceAll(regxp2, "");
        return str;
    }

    @PostMapping("/librarian/searchBookWithIsbn")
    public String getBookInformation(@RequestParam("ISBN") String ISBN) {
        ISBN = ISBN.trim();
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        String ans = "";
        String urlNameString = "https://api.douban.com/v2/book/isbn/:" + ISBN;
        try {
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            //   log.info("得到的信息为 " + result.toString());

            ans = getImageAndSave(result.toString());
        } catch (MalformedURLException e) {
            log.info("发送ISBN请求失败");
            e.printStackTrace();
        } catch (FileNotFoundException file) {
            return "fileNotFound";
        } catch (IOException e) {
            log.info("打开连接失败");
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return ans;
    }

    public String getImageAndSave(String str) {
        book bk = new book();
        JSONObject jsonObject = JSONObject.fromObject(str);
        String title = jsonObject.getString("title");

        bk.setName(title);
        List<String> authors = (List<String>) jsonObject.get("author");
        StringBuilder author = new StringBuilder();
        author.append(listToString(authors));

        bk.setAuthor(author.toString());
        String publisher = jsonObject.getString("publisher");
        bk.setPublisher(publisher);
        String price = jsonObject.getString("price");

        bk.setPrice(price);
        String introduction = jsonObject.getString("summary");
        if (introduction.length() > 200) {
            introduction = introduction.substring(0, 200);
        }
        bk.setIntroduction(introduction);

        Object imgs = jsonObject.get("images");
        String image = getSmallPic(imgs);
        String downPic = image;
        String suffix = image.substring(image.lastIndexOf("."));
        File ff = null;
        String fileName;
        String uploadPath = "F:\\idea2017.1.1代码\\images\\"; // 上传文件的目录
        do {
            fileName = UUID.randomUUID().toString();
            ff = new File(uploadPath + fileName + suffix);
        } while (ff.exists());
        image = fileName + suffix;
        downLoadPic(downPic, image);
        bk.setBookPicNumber(image);
        //      log.info("得到的图书的信息为 ： " + bk.toString());
        return JSONObject.fromObject(bk).toString();
    }

    public static void downLoadPic(String Location, String picName) {
        try {
            URL url = new URL(Location);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream
                    (new File("F:\\idea2017.1.1代码\\images\\" + picName));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            fileOutputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String listToString(List<String> list) {
        String str = list.toString();
        str = str.replace("\"", "");
        str = str.replace("[", " ");
        str = str.replace("]", " ");
        return str;
    }

    public String getSmallPic(Object obj) {
        JSONObject json = JSONObject.fromObject(obj.toString());
        return json.getString("small");
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


    public static String replaceSpecialChar(String str) {
        Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    @PostMapping("/librarian/ISBNAddBook")
    public String ISBNAddBook(@RequestParam("picture") String picture,
                              @RequestParam("bookTypeId") String bookTypeId,
                              @RequestParam("bookName") String bookName,
                              @RequestParam("author") String author,
                              @RequestParam("introduction") String introduction,
                              @RequestParam("bookNumber") String bookNumber,
                              @RequestParam("bookPublisher") String bookPublisher,
                              @RequestParam("bookPrice") String bookPrice) {

        bookTypeId = deleteDoubleAndOne(bookTypeId);
        bookName = deleteDoubleAndOne(bookName);
        author = deleteDoubleAndOne(author);
        introduction = deleteDoubleAndOne(introduction);
        bookName = deleteDoubleAndOne(bookName);
        bookPublisher = deleteDoubleAndOne(bookPublisher);
        bookPrice = deleteDoubleAndOne(bookPrice);

        book bk = new book();
        bk.setBookPicNumber(picture);
        introduction = replaceSpecialChar(introduction);
        bk.setIntroduction(introduction);
        bk.setBookTypeId(Integer.parseInt(bookTypeId));
        bk.setName(bookName);
        bk.setAuthor(author);
        bk.setPrice(bookPrice);
        bk.setSurPlus(Integer.parseInt(bookNumber));
        bk.setNumbers(Integer.parseInt(bookNumber));
        bk.setBookLocation(getBookLocation(Integer.parseInt(bookTypeId), Integer.parseInt(bookNumber)));
        bk.setPublisher(bookPublisher);
        log.info("图书书的信息为" + bk.toString());
        bk = bookRepository.save(bk);
        oneBook oneBook;
        List<Integer> bookIdList = new LinkedList<>();
        for (int i = 0; i < Integer.parseInt(bookNumber); i++) {
            oneBook = new oneBook();
            oneBook.setBookId(bk.getId());
            oneBook.setBookState(String.valueOf(AVAILABLE));
            oneBook = oneBookRepository.save(oneBook);
            bookIdList.add(oneBook.getId());
        }
        return JSONArray.fromObject(bookIdList).toString();
    }
}
