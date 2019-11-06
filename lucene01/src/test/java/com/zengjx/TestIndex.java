package com.zengjx;


import com.zengjx.dao.BookDao;
import com.zengjx.dao.Impl.BookDapImpl;
import com.zengjx.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HelloController
 * @Description TODO
 * @Author zengjx
 * @Company zengjx
 * @Date 2019/11/6  9:18
 * @Version V1.0
 */
public class TestIndex {

    @Test
    public void testCreateIndex() throws Exception{
        //1.采集数据：(jdbc采集数据通过BookDao调用方法得到结果集)
        BookDao bookDao = new BookDapImpl();
        List<Book> books = bookDao.queryBookList();
        //2.遍历book结果集，组装Document数据列表
        List<Document> docs = new ArrayList<>();
        Document doc = null;
        for (Book book : books) {
            //3.构建Field域，说白了就是将要存储的数据字段需要用到new TextField对象三个参数的构造方法，
            // book中有多个字段，所以创建多个Field对象。
            Field id = new TextField("id", book.getId().toString(), Field.Store.YES);
            Field name = new TextField("name", book.getName(), Field.Store.YES);
            Field price;
            price = new TextField("price", book.getPrice().toString(), Field.Store.YES);
            Field pic = new TextField("pic", book.getPic(), Field.Store.YES);
            Field desc = new TextField("desc", book.getDesc(), Field.Store.YES);
            //4.将Field域所有对象，添加到文档对象中。调用Document.add
            doc = new Document();
            doc.add(id);
            doc.add(name);
            doc.add(price);
            doc.add(pic);
            doc.add(desc);
            //记录文档对象列表
            docs.add(doc);
        }
        //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new StandardAnalyzer();
        //6.指定索引储存目录，使用FSDirectory.open()方法。
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //8.创建IndexWriter对象，直接new
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        //9.添加文档对象到索引库输出对象中，使用IndexWriter.addDocuments方法
        indexWriter.addDocuments(docs);
        //10.释放资源IndexWriter.close();
        indexWriter.close();
    }
   @Test
     public   void   testIndex2() throws IOException {
       //1. 	采集数据：(jdbc采集数据通过BookDao调用方法得到结果集)
       BookDao  bookDao  =new BookDapImpl();

       //2. 	遍历book结果集，组装Document数据列表
       List<Book> bookList = bookDao.queryBookList();
       List<Document>  docs =new ArrayList<>();
       //3. 	构建Field域，说白了就是将要存储的数据字段需要用到new TextField对象三个参数的构造方法，book中有多个字段，所以创建多个Field对象。
       for (Book book : bookList) {
        Field  id=  new TextField("id",book.getId().toString(),Field.Store.YES);
        Field  name=  new TextField("name",book.getName(),Field.Store.YES);
        Field  price=  new TextField("price",book.getPrice().toString(),Field.Store.YES);
        Field  pic=  new TextField("pic",book.getPic(),Field.Store.YES);
        Field  desc=  new TextField("desc",book.getDesc(),Field.Store.YES);
        Document   doc= new Document();
        doc.add(id);
        doc.add(name);
        doc.add(price);
        doc.add(pic);
        doc.add(desc);
       docs.add(doc);
       }
       //4. 	将Field域所有对象，添加到文档对象中。调用Document.add
       //5. 	创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
       Analyzer  analyzer =new StandardAnalyzer();
       //6. 	指定索引储存目录，使用FSDirectory.open(new File("").toPath())方法。
       FSDirectory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
       //7. 	创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
       IndexWriterConfig  indexWriterConfig =new IndexWriterConfig(analyzer);
       //8. 	创建IndexWriter对象，直接new
       IndexWriter  indexWriter =new IndexWriter(directory,indexWriterConfig);
       //9. 	添加文档对象到索引库输出对象中，使用IndexWriter.addDocuments方法
       indexWriter.addDocuments(docs);
       //10. 	释放资源IndexWriter.close();
       indexWriter.close();
       //域是字段

     }

     //
     @Test
     public void testQuery() throws Exception{
         //1.创建一个Directory对象，FSDirectory.open指定索引库存放的位置
         Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
         //2.创建一个IndexReader对象，DirectoryReader.open需要指定Directory对象
         IndexReader indexReader = DirectoryReader.open(directory);
         //3.创建一个Indexsearcher对象，直接new，需要指定IndexReader对象
         IndexSearcher indexSearcher = new IndexSearcher(indexReader);
         //4.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
         Analyzer analyzer = new StandardAnalyzer();
         //5.创建一个QueryParser对象， new QueryParser (域名称，分词器)
         QueryParser queryParser = new QueryParser("desc",analyzer);
         //6.调用QueryParser.parser(搜索的内容)，得到Query
         Query query = queryParser.parse("java");
         //7.执行查询，IndexSearcher.search(Query对象,查询排名靠多少名前的记录数)，得到结果TopDocs
         TopDocs topDocs = indexSearcher.search(query, 10);
         //8.遍历查询结果并输出，TopDocs.totalHits总记录数，topDocs.scoreDocs数据列表，
         // 通过scoreDoc.doc得到唯一id,再通过IndexSearcher.doc(id)，
         // 得到文档对象Document再Document.get(域名称)得到结果
         System.out.println("总记录数为：" + topDocs.totalHits);
         for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
             int docId = scoreDoc.doc;
             Document doc = indexSearcher.doc(docId);
             System.out.println(doc.get("id") + "->" + doc.get("name") + "," + doc.get("price"));
         }
         //9.关闭IndexReader对象
         indexReader.close();
     }
    //IK分词器  替换 StandardAnalyzer()------>IKAnalyzer
    @Test
    public void testCreateIndexIK() throws Exception{
        //1.采集数据：(jdbc采集数据通过BookDao调用方法得到结果集)
        BookDao bookDao = new BookDapImpl();
        List<Book> books = bookDao.queryBookList();
        //2.遍历book结果集，组装Document数据列表
        List<Document> docs = new ArrayList<>();
        Document doc = null;
        for (Book book : books) {
            //3.构建Field域，说白了就是将要存储的数据字段需要用到new TextField对象三个参数的构造方法，
            // book中有多个字段，所以创建多个Field对象。
            Field id = new TextField("id", book.getId().toString(), Field.Store.YES);
            Field name = new TextField("name", book.getName(), Field.Store.YES);
            Field price;
            price = new TextField("price", book.getPrice().toString(), Field.Store.YES);
            Field pic = new TextField("pic", book.getPic(), Field.Store.YES);
            Field desc = new TextField("desc", book.getDesc(), Field.Store.YES);
            //4.将Field域所有对象，添加到文档对象中。调用Document.add
            doc = new Document();
            doc.add(id);
            doc.add(name);
            doc.add(price);
            doc.add(pic);
            doc.add(desc);
            //记录文档对象列表
            docs.add(doc);
        }
        //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new IKAnalyzer();
        //6.指定索引储存目录，使用FSDirectory.open()方法。
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //8.创建IndexWriter对象，直接new
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        //9.添加文档对象到索引库输出对象中，使用IndexWriter.addDocuments方法
        indexWriter.addDocuments(docs);
        //10.释放资源IndexWriter.close();
        indexWriter.close();
    }

   //IK 查询

   @Test
    public   void  testQuery2() throws ParseException, IOException {

//       1. 	创建一个Directory对象，FSDirectory.open指定索引库存放的位置
       Directory  directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
//       2. 	创建一个IndexReader对象，DirectoryReader.open需要指定Directory对象
       IndexReader indexReader= DirectoryReader.open(directory);
//       3. 	创建一个Indexsearcher对象，直接new，需要指定IndexReader对象
       IndexSearcher searcher =  new IndexSearcher(indexReader);

//       4. 	创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
       Analyzer analyzer =new IKAnalyzer();
//       5. 	创建一个QueryParser对象， new QueryParser (域名称，分词器)
       QueryParser queryParser =new QueryParser("desc",analyzer);
//       6. 	调用QueryParser.parser(搜索的内容)，得到Query
       Query query = queryParser.parse("java");
//       7. 	执行查询，IndexSearcher.search(Query对象,查询排名靠多少名前的记录数)，得到结果TopDocs
       TopDocs topDocs = searcher.search(query, 10);
//       8. 	遍历查询结果并输出，TopDocs.totalHits总记录数，topDocs.scoreDocs数据列表，通过scoreDoc.doc得到唯一id,再通过IndexSearcher.doc(id)，得到文档对象Document再Document.get(域名称)得到结果
       System.out.println("topdoc 总记录数"+topDocs.totalHits);
       ScoreDoc[] scoreDocs = topDocs.scoreDocs;
       for (int i = 0; i < scoreDocs.length; i++) {
           System.out.println(" doc:"+scoreDocs[i].doc+" name "+scoreDocs[i].score);
          int docid= scoreDocs[i].doc;
          Document  document =searcher.doc(docid);
           System.out.println(document.get("name"));
       }

//       9. 	关闭IndexReader对象
      indexReader.close();


   }


    //  filed
    @Test
    public void testCreateIndexFiled() throws Exception{
        //1.采集数据：(jdbc采集数据通过BookDao调用方法得到结果集)
        BookDao bookDao = new BookDapImpl();
        List<Book> books = bookDao.queryBookList();
        //2.遍历book结果集，组装Document数据列表
        List<Document> docs = new ArrayList<>();
        Document doc = null;
        for (Book book : books) {
            //3.构建Field域，说白了就是将要存储的数据字段需要用到new TextField对象三个参数的构造方法，
            // book中有多个字段，所以创建多个Field对象。
            //不分词 不索引   存储
            Field id = new TextField("id", book.getId().toString(), Field.Store.YES);
            //   分词索引
            Field name = new TextField("name", book.getName(), Field.Store.YES);
            Field price;//  分词  索引 存储
            price = new FloatField("price", book.getPrice(), Field.Store.YES);
            //图片地址   不分词  不索引
            Field pic = new StoredField("pic",book.getPic());
            Field desc = new TextField("desc", book.getDesc(), Field.Store.NO);
            //4.将Field域所有对象，添加到文档对象中。调用Document.add
            doc = new Document();
            doc.add(id);
            doc.add(name);
            doc.add(price);
            doc.add(pic);
            doc.add(desc);
            //记录文档对象列表
            docs.add(doc);
        }
        //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new IKAnalyzer();
        //6.指定索引储存目录，使用FSDirectory.open()方法。
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //8.创建IndexWriter对象，直接new
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        //9.添加文档对象到索引库输出对象中，使用IndexWriter.addDocuments方法
        indexWriter.addDocuments(docs);
        //10.释放资源IndexWriter.close();
        indexWriter.close();
    }

    @Test
    public void testDelele() throws Exception{
        //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new IKAnalyzer();
        //6.指定索引储存目录，使用FSDirectory.open()方法。
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //8.创建IndexWriter对象，直接new
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);

        //删除索引
        indexWriter.deleteDocuments(new Term("name", "java"));
        //释放资源
        indexWriter.close();
    }
    //删除所有
    @Test
    public void testDelele2() throws Exception{
        //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new IKAnalyzer();
        //6.指定索引储存目录，使用FSDirectory.open()方法。
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //8.创建IndexWriter对象，直接new
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);

        //删除索引
        //indexWriter.deleteDocuments(new Term("name", "java"));
        indexWriter.deleteAll();//慎重

        indexWriter.close();//必须
    }
    //更新
    @Test
    public void testUpdate() throws Exception{
        //5.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new IKAnalyzer();
        //6.指定索引储存目录，使用FSDirectory.open()方法。
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //7.创建IndexWriterConfig对象，直接new，用于接下来创建IndexWriter对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //8.创建IndexWriter对象，直接new
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
       Document  doc =new Document();
        //不分词 不索引   存储
        Field id = new TextField("id", "1", Field.Store.YES);
        //   分词索引
        Field name = new TextField("name", "222222修改过", Field.Store.YES);

        doc.add(name);
        //删除索引
        //indexWriter.deleteDocuments(new Term("name", "java"));
        indexWriter.updateDocument(new Term("name","java"),doc);

        indexWriter.close();//必须
    }


    //测试结果：
    //总记录数为：2
    //搜索语法:name:java name:lucene
    //2->apache lucene,66.0
    //1->java 编程思想,71.5
    @Test
    public void testQuery3() throws Exception{
        //1.创建一个Directory对象，FSDirectory.open指定索引库存放的位置
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //2.创建一个IndexReader对象，DirectoryReader.open需要指定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //3.创建一个Indexsearcher对象，直接new，需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new StandardAnalyzer();
        //5.创建一个QueryParser对象， new QueryParser (域名称，分词器)
        QueryParser queryParser = new QueryParser("name",analyzer);
        //6.调用QueryParser.parser(搜索的内容)，得到Query
        Query query = queryParser.parse("java lucene");
        //7.执行查询，IndexSearcher.search(Query对象,查询排名靠多少名前的记录数)，得到结果TopDocs
        TopDocs topDocs = indexSearcher.search(query, 10);
        //8.遍历查询结果并输出，TopDocs.totalHits总记录数，topDocs.scoreDocs数据列表，
        // 通过scoreDoc.doc得到唯一id,再通过IndexSearcher.doc(id)，
        // 得到文档对象Document再Document.get(域名称)得到结果
        System.out.println("总记录数为：" + topDocs.totalHits);
        System.out.println("搜索语法:"+query);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docId = scoreDoc.doc;
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("id") + "->" + doc.get("name") + "," + doc.get("price"));
        }
        //9.关闭IndexReader对象
        indexReader.close();
    }

////Query子类用法-不支持自定义语法查询，而且不手动指定分词器
    @Test
    public void testQuery4() throws Exception{
        //1.创建一个Directory对象，FSDirectory.open指定索引库存放的位置
        Directory directory = FSDirectory.open(new File("D:/zengjx/index").toPath());
        //2.创建一个IndexReader对象，DirectoryReader.open需要指定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //3.创建一个Indexsearcher对象，直接new，需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4.创建一个标准分词器(Analyzer与StandardAnalyzer)，对文档中的Field域进行分词
        Analyzer analyzer = new StandardAnalyzer();
        //5.创建一个QueryParser对象， new QueryParser (域名称，分词器)
        QueryParser queryParser = new QueryParser("name",analyzer);
        //6.调用QueryParser.parser(搜索的内容)，得到Query
       //   1.Query query = queryParser.parse("java lucene");
      // 2. Query  query =new MatchAllDocsQuery();//查询所有
      //
      //
      // 3.  Query  query  =new TermQuery(new Term("name","java lucene"));// 查询最小单元搜索语法:name:java lucene
      // 4.范围查找
      //  Query query = NumericRangeQuery.newFloatRange("price",55F,66F,true,false);

        //4、相似度查询-失真
        Query query = new FuzzyQuery(new Term("name","lucaae"));

        //7.执行查询，IndexSearcher.search(Query对象,查询排名靠多少名前的记录数)，得到结果TopDocs
        TopDocs topDocs = indexSearcher.search(query, 10);
        //8.遍历查询结果并输出，TopDocs.totalHits总记录数，topDocs.scoreDocs数据列表，
        // 通过scoreDoc.doc得到唯一id,再通过IndexSearcher.doc(id)，
        // 得到文档对象Document再Document.get(域名称)得到结果
        System.out.println("总记录数为：" + topDocs.totalHits);
        System.out.println("搜索语法:"+query);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docId = scoreDoc.doc;
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("id") + "->" + doc.get("name") + "," + doc.get("price"));
        }
        //9.关闭IndexReader对象
        indexReader.close();
    }
}
