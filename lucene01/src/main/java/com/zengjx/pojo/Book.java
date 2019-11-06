package com.zengjx.pojo;

import java.io.Serializable;

/**
 * @ClassName HelloController
 * @Description TODO
 * @Author zengjx
 * @Company zengjx
 * @Date 2019/11/6  8:34
 * @Version V1.0
 */
public class Book   implements Serializable {

    // 图书ID
    private Integer id;
    // 图书名称
    private String name;
    // 图书价格
    private Float price;
    // 图书图片
    private String pic;
    // 图书描述
    private String desc;

    public Book() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", pic='" + pic + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
