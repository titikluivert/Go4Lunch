package com.ngtiofack.go4lunch;

import com.ngtiofack.go4lunch.utils.mainUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ConvertDataTypeTest {


    @Test
    public void convertHexToDecimalTEST() {

        Assert.assertEquals(195, mainUtils.convertHexToDecimal("01","02"));
        Assert.assertNotEquals(50, mainUtils.convertHexToDecimal("35","a2"));
    }

    @Test
    public void convertStringToHexTEST() {
        Assert.assertEquals("31", mainUtils.convertStringToHex("1"));
        Assert.assertEquals("3132", mainUtils.convertStringToHex("12"));
    }
}

