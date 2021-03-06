package com.xxx.autoupdate.apiserver.dao;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.xxx.autoupdate.apiserver.util.CommonUtils;
import com.xxx.autoupdate.apiserver.util.ContentVersionConvert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilTest {

    
    @Test
    public void testSave() throws Exception {
        String a =ContentVersionConvert.contentVersionFromLongToStr(10200300000L);
        Assert.assertEquals("10.200.300000", a);
    }
    
    @Test
    public void testUnzip() throws IOException {
    	CommonUtils.unzip("D:\\ddd.zip", "D:\\abc");
    }
    
    @Test
    public void testConvertIntToByte() throws Exception {
    	byte[] bytes = CommonUtils.convertIntToByte(1);
    	int a = CommonUtils.convertByteToInt(bytes);
    	Assert.assertEquals(1, a);
    }
    
    @Test
    public void tmpFile() throws IOException {
    	File file = File.createTempFile("aaa", "package.zip");
    	file.getPath();
    }
}
