package com.zengjx;


import com.zengjx.dao.BookDao;
import com.zengjx.dao.Impl.BookDapImpl;
import com.zengjx.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.List;

/**
 * @author Steven
 * @version 1.0
 * @description com.itheima.test
 * @date 2019-11-6
 */
public class TestIndex2 {
    
    @Test
    public void testCreateIndex() throws Exception{
        //1.采集数据：(jdbc采集数据通过BookDao调用方法得到结果集)
        BookDao bookDao = new BookDapImpl();
        List<Book> bookList = bookDao.queryBookList();
        //2.遍历book结果集，组装Document数据列表
        for (Book book : bookList) {
            //3.构建Field域，说白了就是将要存储的数据字段需要用到new TextField对象三个参数的构造方法，book中有多个字段，所以创建多个Field对象。

            //学习业务域前的代码
            /*Field id = new TextField("id", book.getId() + "", Field.Store.YES);
            Field name = new TextField("name", book.getName(), Field.Store.YES);
            Field price = new TextField("price", book.getPrice() + "", Field.Store.YES);
            Field pic = new TextField("pic", book.getPic(), Field.Store.YES);
            Field desc = new TextField("desc", book.getDesc(), Field.Store.YES);*/

            //学习业务域后的代码
            Field id = new StringField("id", book.getId() + "", Field.Store.YES);
            Field name = new TextField("name", book.getName(), Field.Store.YES);
            Field price = new FloatField("price", book.getPrice(), Field.Store.YES);
            Field pic = new StoredField("pic", book.getPic());
            Field desc = new TextField("desc", book.getDesc(), Field.Store.YES);

            //4.将Field域所有对象，添加到文档对象中。调用Document.add
            Document doc = new Document();
            doc.add(id);
            doc.add(name);
            doc.add(price);
            doc.add(pic);
            doc.add(desc);
            //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
            //Analyzer analyzer = new StandardAnalyzer();
            Analyzer analyzer = new IKAnalyzer();
            //6.指定索引储存目录，使用FSDirectory.open(new File("").toPath())方法。
            Directory directory = FSDirectory.open(new File("D:\\itheima\\ee75\\index").toPath());
            //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
            IndexWriterConfig conf = new IndexWriterConfig(analyzer);
            //8.创建IndexWriter对象，直接new
            IndexWriter indexWriter = new IndexWriter(directory,conf);
            //9.添加文档对象到索引库输出对象中，使用IndexWriter.addDocuments方法
            indexWriter.addDocument(doc);
            //10.释放资源IndexWriter.close();
            indexWriter.close();
        }
    }
    
    @Test
    public void testQuery() throws  Exception{
        //1.创建一个Directory对象，FSDirectory.open指定索引库存放的位置
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //2.创建一个IndexReader对象，DirectoryReader.open需要指定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //3.创建一个Indexsearcher对象，直接new，需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new StandardAnalyzer();

        //扩展知识点讲解前的代码
        //5.创建一个QueryParser对象， new QueryParser (域名称，分词器)
        /*QueryParser parser = new QueryParser("name",analyzer);
        //6.调用QueryParser.parser(搜索的内容)，得到Query
        Query query = parser.parse("java");*/

        //扩展知识点讲解后的代码-
        //在Lucene中，只有两查询方式：1、Query的子类查询;2、QueryParser查询
        //QueryParser的用法-支持自定义语法查询
        //QueryParser(默认搜索的域名,分词器)
        /*QueryParser parser = new QueryParser("name",analyzer);
        //6.调用QueryParser.parser(搜索的内容)，得到Query
        Query query = parser.parse("java lucene");*/

        //Query子类用法-不支持自定义语法查询，而且不手动指定分词器
        //1、查询所有文档
        //Query query = new MatchAllDocsQuery();
        //2、词条搜索
        //Query query = new TermQuery(new Term("name","java lucene"));
        //3、范围匹配搜索
        //Query query = NumericRangeQuery.newFloatRange("price",55F,66F,true,false);
        //4、相似度查询-失真
        Query query = new FuzzyQuery(new Term("name","lucaae"));

        System.out.println("查询语法：" + query);
        //7.执行查询，IndexSearcher.search(Query对象,查询排名靠多少名前的记录数)，得到结果TopDocs
        TopDocs topDocs = indexSearcher.search(query, 100);
        //8.遍历查询结果并输出，
        // TopDocs.totalHits总记录数，
        System.out.println("总记录数为：" + topDocs.totalHits);
        // topDocs.scoreDocs数据列表，
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            // 通过scoreDoc.doc得到唯一id,
            int docId = scoreDoc.doc;
            // 再通过IndexSearcher.doc(id)，得到文档对象Document再Document.get(域名称)得到结果
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("id") + "-->" + doc.get("name"));
        }
        //9.关闭IndexReader对象
        indexReader.close();
    }

    @Test
    public void testDelete() throws  Exception{
        //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        //Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //6.指定索引储存目录，使用FSDirectory.open(new File("").toPath())方法。
        Directory directory = FSDirectory.open(new File("D:\\itheima\\ee75\\index").toPath());
        //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        //8.创建IndexWriter对象，直接new
        IndexWriter indexWriter = new IndexWriter(directory,conf);

        //删除文档-跟据条件删除内容
        //indexWriter.deleteDocuments(new Term("name","java"));

        //删除所有内容-慎用
        indexWriter.deleteAll();

        indexWriter.close();
    }

    @Test
    public void testUpdate() throws Exception{
        //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        //Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //6.指定索引储存目录，使用FSDirectory.open(new File("").toPath())方法。
        Directory directory = FSDirectory.open(new File("D:\\itheima\\ee75\\index").toPath());
        //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        //8.创建IndexWriter对象，直接new
        IndexWriter indexWriter = new IndexWriter(directory,conf);

        //updateDocument(修改范围，修改的值)
        Document doc = new Document();
        doc.add(new TextField("name","java-修改后", Field.Store.YES));

        indexWriter.updateDocument(new Term("name","java"),doc);

        indexWriter.close();
    }
}
