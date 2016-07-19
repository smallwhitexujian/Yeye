package com.will.yeye.util;

import org.junit.Test;

import java.text.MessageFormat;

/**
 * Created by jjfly on 16-4-20.
 */
public class StringHelperTest {

    @Test
    public void testReplaceStr() throws Exception {
        String str = "+86";
        System.out.println("========="+StringHelper.replaceStr(str,0,1,'-'));

        System.out.println("=========replace======"+str.replace("+",""));
    }

    @Test
    public void testFormat() throws Exception {
        String formatStr = MessageFormat.format("+{0}{1}","86","---");
        System.out.println("======+++"+formatStr);
        System.out.println(StringHelper.formatStr("jjfly牛逼--",1111,""));
    }

    @Test
    public void testMerge(){

        String str1 = "1211-";
        String str2 = "33333";

        System.out.println(StringHelper.stringMerge("jjfly牛逼--",str1,str2));
    }


}