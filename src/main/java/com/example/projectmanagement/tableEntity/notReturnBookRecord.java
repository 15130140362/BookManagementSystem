package com.example.projectmanagement.tableEntity;

import lombok.Data;

@Data
public class notReturnBookRecord {

   public notReturnBookRecord() {

   }

   public notReturnBookRecord(Integer id, String bookName, String borrowDate, String shouldReturnDate) {
      this.id = id;
      this.bookName = bookName;
      this.borrowDate = borrowDate;
      this.shouldReturnDate = shouldReturnDate;
   }
   private Integer id;
   private String bookName;
   private String borrowDate;
   private String shouldReturnDate;
   private Integer fine;
   private String bookImage;
   private String comment;
}
