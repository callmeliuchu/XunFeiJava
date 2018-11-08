package com.util;


import java.lang.reflect.Array;
import java.util.*;


public class ProcessData {

    public static Set<String> getAlreadyData(){
        Set<String> set = new HashSet<>();
        List<String>files = CommonUtil.getFiles("tmp");
        for(String s : files){
            String[] arr = s.split("/");
            String pid = arr[arr.length-1].split("\\.")[0];
            set.add(pid);
        }
        return set;
    }

    public static void saveData(){
        Set<String> alreadySet = getAlreadyData();
        CommonUtil.print(alreadySet);


//        String str = "knowledgefiles/ch/videos/201606/f9d6f8c6ae644ff8b36b557fb2ea91c0.wmv";
//        String  []arr = str.split("\\.");
//        System.out.println(arr.length);
        ArrayList<HashMap<String,String>>  gwKnowledgeList = DBUtil.query("pid,bucketName,bucketKey,orgId","gw_knowledge","  kngType='VideoKnowledge' ");
        ArrayList<HashMap<String,String>> coreOrgProfileList = DBUtil.query("id","core_organizationprofile","isofficialcustomer=1");
        HashSet<String> officalDataSet = new HashSet<>();
        for(Map<String,String> e : coreOrgProfileList){
            String id = e.get("id");
            officalDataSet.add(id);
        }
        int count = 0;
        for(Map<String,String> e : gwKnowledgeList) {
            String pid = e.get("pid");
            String bucketName = e.get("bucketName");
            String bucketKey = e.get("bucketKey");

//            System.out.println(bucketKey);
            String type = bucketKey.split("\\.")[1];
//            System.out.println(bucketKey.split(".")[1]);
            String orgId = e.get("orgId");
//            if("mp3".equalsIgnoreCase(type)){
            if (officalDataSet.contains(orgId) && (!alreadySet.contains(pid))) {
                BaiduUtil.saveBucketContent(bucketName,bucketKey,"tmp/"+pid+"."+type);
                count += 1;
                if(count>40){
                    break;
                }
            }
//            }
        }
        System.out.println(gwKnowledgeList.size());
        System.out.println(coreOrgProfileList.size());
        System.out.println(count);
    }

    public static void main(String[] args) throws Exception{
//        List<List<String>> topHotVideos = CommonUtil.readCSV("hot_videos_top.tsv",true);
//        List<String> topHotVideoList = CommonUtil.makeListList(topHotVideos,0);
//        ArrayList<HashMap<String,String>>  gwKnowledgeList = DBUtil.query("pid,bucketName,bucketKey,orgId","gw_knowledge","  kngType='VideoKnowledge' ");
////        SELECT id,orgid,readcount,uploaddate,(readcount+1)/power(datediff(now(),uploaddate)+2,1.8) score FROM core_knowledge WHERE isDeleted=0 and uploaddate < now();
////        SELECT id,orgid,readcount,uploaddate,(readcount+1)/power(datediff(now(),uploaddate)+2,1.8) score FROM core_knowledge WHERE isDeleted=0 and uploaddate < now() order by score desc;
////        ArrayList<HashMap<String,String>>  coreKnowledgeList = DBUtil.query("id,orgid,readcount,uploaddate,(readcount+1)/power(datediff(now(),uploaddate)+2,1.8) score","core_knowledge","  isDeleted=0 and uploaddate < now() order by score desc limit 1000 ");
////        CommonUtil.print(coreKnowledgeList);
////        Set<String> hotIds = new HashSet<>();
////        for(Map<String,String> e: coreKnowledgeList){
////            String id = e.get("id");
////            hotIds.add(id);
////        }
//        Map<String,Map<String,String>>queryMap = new HashMap<>();
//        for(Map<String,String> e : gwKnowledgeList) {
//            String pid = e.get("pid");
//
//            String bucketName = e.get("bucketName");
//            String bucketKey = e.get("bucketKey");
//            queryMap.put(pid,e);
////            if(hotIds.contains(pid)){
////                CommonUtil.print(pid);
////            }
//            String type = bucketKey.split("\\.")[1];
//            String orgId = e.get("orgId");
////            if (topHotVideoList.contains(pid)) {
//////                CommonUtil.print(pid);
//////                try {
//////                    BaiduUtil.saveBucketContent(bucketName, bucketKey, "hot_videos/" + pid + "." + type);
//////                }catch(Exception h){
//////                    CommonUtil.print(h);
////////                    179ee4ed-00b5-4f7c-83b1-4887f276e979.wmv
//////                }
////            }
//
//
//
//        }
//
//
//
//        for(String video : topHotVideoList){
//            if(queryMap.containsKey(video)){
//                CommonUtil.print(video);
//            }
//        }

    }

}
