package com.util;
import java.io.File;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;


public class BaiduUtil {
    public static final String BOS_HOST = "http://bj.bcebos.com";
    public static final String ACCESS_KEY_ID = "8e8652db0d5a4738aa61ae13b5b30b77";
    public static final String SECRET_ACCESS_KEY = "e0cae447375b44428898017065c35a5c";

    public static BosClient getBosClient() {
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
        config.setEndpoint(BOS_HOST);
        BosClient bc = new BosClient(config);
        return bc;
    }

    public static void saveBucketContent(String bucketName, String key, String localFile) {
        BosClient client = getBosClient();
        client.getObject(bucketName, key, new File(localFile));
    }





    public static void main(String[] args) {
        String bucketName = "elearningo";
        String key = "knowledgefiles/13621298972/videos/201806/96c7cff20d7843b3be9ef6476b16b34c.mp3";
        String localFile = "tmp/video";
        saveBucketContent(bucketName,key,localFile);
    }

}
