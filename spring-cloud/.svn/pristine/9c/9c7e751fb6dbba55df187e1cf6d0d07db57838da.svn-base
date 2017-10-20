package com.feiniu.member.dto;

/**
 * 枚举
 * 
 * @author dongpo.hu
 * 
 */
public class SubscribeEnum {
    /**
     * 枚举像普通的类一样可以添加属性和方法，可以为它添加静态和非静态的属性或方法
     * 
     * @author 
     *
     */
    public enum SeasonEnum {
        //注：枚举写在最前面，否则编译出错
        spring, summer, autumn, winter;

        private final static String position = "test";

        public static SeasonEnum getSeason() {
            if ("test".equals(position))
                return spring;
            else
                return winter;
        }
    }
    
    /**
     * 大类别
     * 
     * 实现带有构造器的枚举
     * 
     * @author dongpo.hu
     *
     */
    public enum Gender{
        //通过括号赋值,而且必须带有一个参构造器和一个属性跟方法，否则编译出错
        //赋值必须都赋值或都不赋值，不能一部分赋值一部分不赋值；如果不赋值则不能写构造器，赋值编译也出错
        system_message("1,2,3,4,5,6"), member_care("1,2,3"),activity_notice("1");
        
        private final String value;

        //构造器默认也只能是private, 从而保证构造函数只能在内部使用
        Gender(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    
    public static void main(String[] args) {
        
        
        
        //--------------
        for(Gender gender : Gender.values()){
            String name = gender.name();
            int ordinal = gender.ordinal();
            String string = gender.toString();
            System.out.println(string);
            System.out.println(ordinal);
           System.out.println(name);
            System.out.println(gender.value);
            System.out.println("===========");
        }
       
    }
    
}