package com.heima.wemedia;

import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.file.service.FileStorageService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class AliyunTest {
    private static final String SUGGESTION = "suggestion";
    private static final String PASS = "pass";
    @Autowired
    private GreenTextScan greenTextScan;
    @Autowired
    private GreenImageScan greenImageScan;
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * text verification
     */
    @Test
    public void testScanTextPass() throws Exception {
        Map map = greenTextScan.greeTextScan("I am a good person");
        Assertions.assertThat(map.get(SUGGESTION)).isEqualTo(PASS);
    }

    @Test
    public void testScanTextFail() throws Exception {
        Map map = greenTextScan.greeTextScan("冰毒");
        Assertions.assertThat(map.get(SUGGESTION)).isNotEqualTo(PASS);
    }

    /**
     * image verification
     */

    @Test
    public void testScanImagePass() throws Exception {
        byte[] bytes = fileStorageService.downLoadFile("http://localhost:9000/news/2023/07/08/2eb4c95f146f45749fd8278603bdbc1c.jpg");
        List<byte[]> list = new ArrayList<>();
        list.add(bytes);
        Map map = greenImageScan.imageScan(list);
        Assertions.assertThat(map.get(SUGGESTION)).isEqualTo(PASS);
    }

    //@Test
    public void testScanImageFail() throws Exception {
        byte[] bytes = fileStorageService.downLoadFile("http://localhost:9000/news/2023/07/08/2eb4c95f146f45749fd8278603bdbc1c.jpg");
        List<byte[]> list = new ArrayList<>();
        list.add(bytes);
        Map map = greenImageScan.imageScan(list);
        Assertions.assertThat(map.get(SUGGESTION)).isNotEqualTo(PASS);    }

}
