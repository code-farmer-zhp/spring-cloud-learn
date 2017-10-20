package consul.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaowen on 2017/7/24.
 */
@ConfigurationProperties(prefix = "userInfo.dn")
public class UserInfo {

    private String name;

    private String sex;

    private int age;

    private String like;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }


    @Override
    public String toString() {
        return "My name : ["+name+"] sex : ["+sex+"] age : ["+age+"] like : ["+like+"]";
    }
}
