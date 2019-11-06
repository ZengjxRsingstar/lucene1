package com.zengjx.dao;

import com.zengjx.pojo.Book;

import java.util.List;

/**
 * @ClassName HelloController
 * @Description TODO
 * @Author zengjx
 * @Company zengjx
 * @Date 2019/11/6  8:38
 * @Version V1.0
 */
public interface BookDao {

  List<Book>  queryBookList();
}
